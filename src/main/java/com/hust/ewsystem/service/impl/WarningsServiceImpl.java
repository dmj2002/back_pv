package com.hust.ewsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hust.ewsystem.DAO.PO.*;
import com.hust.ewsystem.common.exception.CrudException;
import com.hust.ewsystem.common.result.EwsResult;
import com.hust.ewsystem.mapper.WarningsMapper;
import com.hust.ewsystem.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class WarningsServiceImpl extends ServiceImpl<WarningsMapper, Warnings> implements WarningsService {

    private final CombinerBoxService combinerBoxService;

    private final BoxTransService boxTransService;

    private final InverterService inverterService;

    private final ModelsService modelsService;

    @Override
    public EwsResult<?> getWarningList(int page, int pageSize, String startDate, String endDate, Integer warningLevel, Integer companyId, Integer pvFarmId, Integer inverterId, Integer combinerBoxId) {
        QueryWrapper<Models> queryWrapper = new QueryWrapper<>();
        List<Integer> modelIdlist = new ArrayList<>();
        if(pvFarmId != null){
            List<Integer> boxIds = boxTransService.list(new QueryWrapper<BoxTrans>().eq("pv_farm_id", pvFarmId)).stream().map(BoxTrans::getId).collect(Collectors.toList());
            List<Integer> combinerIds = combinerBoxService.list(new QueryWrapper<CombinerBox>().in("box_id", boxIds)).stream().map(CombinerBox::getId).collect(Collectors.toList());
            List<Integer> inverterIds = inverterService.list(new QueryWrapper<Inverter>().in("box_id", boxIds)).stream().map(Inverter::getId).collect(Collectors.toList());
            queryWrapper.nested(wrapper -> wrapper.in("device_id", inverterIds).eq("model_type", 2))
                    .or(wrapper -> wrapper.in("device_id", combinerIds).eq("model_type", 1));
            modelIdlist = modelsService.list(queryWrapper).stream().map(Models::getModelId).collect(Collectors.toList());
        }else if(inverterId != null) {
            List<Integer> combinerIds = combinerBoxService.list(new QueryWrapper<CombinerBox>().eq("inverter_id", inverterId)).stream().map(CombinerBox::getId).collect(Collectors.toList());
            queryWrapper.nested(wrapper -> wrapper.eq("device_id", inverterId).eq("model_type", 2))
                    .or(wrapper -> wrapper.in("device_id", combinerIds).eq("model_type", 1));
            modelIdlist = modelsService.list(queryWrapper).stream().map(Models::getModelId).collect(Collectors.toList());

        }else if(combinerBoxId != null) {
            queryWrapper.eq("device_id", combinerBoxId)
                    .eq("model_type", 1);
            modelIdlist = modelsService.list(queryWrapper).stream().map(Models::getModelId).collect(Collectors.toList());
        }else{
            List<Integer> boxIds = boxTransService.list().stream().map(BoxTrans::getId).collect(Collectors.toList());
            List<Integer> combinerIds = combinerBoxService.list(new QueryWrapper<CombinerBox>().in("box_id", boxIds)).stream().map(CombinerBox::getId).collect(Collectors.toList());
            List<Integer> inverterIds = inverterService.list(new QueryWrapper<Inverter>().in("box_id", boxIds)).stream().map(Inverter::getId).collect(Collectors.toList());
            queryWrapper.nested(wrapper -> wrapper.in("device_id", inverterIds).eq("model_type", 2))
                    .or(wrapper -> wrapper.in("device_id", combinerIds).eq("model_type", 1));
            modelIdlist = modelsService.list(queryWrapper).stream().map(Models::getModelId).collect(Collectors.toList());
        }
        Page<Warnings> warningsPage = new Page<>(page, pageSize);
        QueryWrapper<Warnings> queryWrapper2 = new QueryWrapper<>();
        queryWrapper2.in("model_id", modelIdlist);
        if(endDate != null){
            queryWrapper2.ge("start_time", startDate).le("end_time", endDate);
        }
        else{
            queryWrapper2.ge("start_time", startDate).le("end_time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        if(warningLevel != null){
            queryWrapper2.eq("warning_level", warningLevel);
        }
        Page<Warnings> page1 = page(warningsPage, queryWrapper2);
        Map<String,Object> result = new HashMap<>();
        if(!page1.getRecords().isEmpty()){
            result.put("warningList",page1.getRecords());
        }else {
            result.put("warningList",new ArrayList<>());
        }
        result.put("total_count",page1.getTotal());
        result.put("page",page1.getCurrent());
        result.put("page_size",page1.getSize());
        result.put("total_pages",page1.getPages());
        return EwsResult.OK("查询成功", result);
    }
}
