package com.hust.ewsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.hust.ewsystem.DAO.DTO.QueryReportsDTO;
import com.hust.ewsystem.DAO.PO.*;

import com.hust.ewsystem.mapper.EmployeeMapper;
import com.hust.ewsystem.mapper.ReportsMapper;
import com.hust.ewsystem.service.BoxTransService;
import com.hust.ewsystem.service.CombinerBoxService;
import com.hust.ewsystem.service.InverterService;
import com.hust.ewsystem.service.ReportsService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


/**
 * @BelongsProject: back
 * @BelongsPackage: com.hust.ewsystem.service.impl
 * @Author: xdy
 * @CreateTime: 2025-01-08  10:32
 * @Description:
 * @Version: 1.0
 */
@Service
@Transactional
@RequiredArgsConstructor
public class ReportsServiceImpl extends ServiceImpl<ReportsMapper, Reports> implements ReportsService {

    private final CombinerBoxService combinerBoxService;

    private final BoxTransService boxTransService;

    private final InverterService inverterService;

    private final ReportsMapper reportsMapper;


    @Override
    public IPage<Reports> getReportList(QueryReportsDTO queryReportsDTO) {
        Page<Reports> page = new Page<>(queryReportsDTO.getPageNo(), queryReportsDTO.getPageSize());
        QueryWrapper<Reports> queryWrapper = new QueryWrapper<>();
        if(queryReportsDTO.getPvFarmId() != null){
            List<Integer> boxIds = boxTransService.list(new QueryWrapper<BoxTrans>().eq("pv_farm_id", queryReportsDTO.getPvFarmId())).stream().map(BoxTrans::getId).collect(Collectors.toList());
            List<Integer> combinerIds = combinerBoxService.list(new QueryWrapper<CombinerBox>().in("box_id", boxIds)).stream().map(CombinerBox::getId).collect(Collectors.toList());
            List<Integer> inverterIds = inverterService.list(new QueryWrapper<Inverter>().in("box_id", boxIds)).stream().map(Inverter::getId).collect(Collectors.toList());
            queryWrapper.nested(wrapper -> wrapper.in("device_id", inverterIds).eq("device_type", 2))
                    .or(wrapper -> wrapper.in("device_id", combinerIds).eq("device_type", 1));
        }
        else if(queryReportsDTO.getInverterId() != null){
            List<Integer> combinerIds = combinerBoxService.list(new QueryWrapper<CombinerBox>().eq("inverter_id", queryReportsDTO.getInverterId())).stream().map(CombinerBox::getId).collect(Collectors.toList());
            queryWrapper.nested(wrapper -> wrapper.eq("device_id", queryReportsDTO.getInverterId()).eq("device_type", 2))
                    .or(wrapper -> wrapper.in("device_id", combinerIds).eq("device_type", 1));
        }else if(queryReportsDTO.getCombinerBoxId() != null){
            queryWrapper.eq("device_id", queryReportsDTO.getCombinerBoxId())
                    .eq("model_type", 1);
        }else{
            List<Integer> boxIds = boxTransService.list().stream().map(BoxTrans::getId).collect(Collectors.toList());
            List<Integer> combinerIds = combinerBoxService.list(new QueryWrapper<CombinerBox>().in("box_id", boxIds)).stream().map(CombinerBox::getId).collect(Collectors.toList());
            List<Integer> inverterIds = inverterService.list(new QueryWrapper<Inverter>().in("box_id", boxIds)).stream().map(Inverter::getId).collect(Collectors.toList());
            queryWrapper.nested(wrapper -> wrapper.in("device_id", inverterIds).eq("device_type", 2))
                    .or(wrapper -> wrapper.in("device_id", combinerIds).eq("device_type", 1));
        }
        queryWrapper.ge("initial_time",queryReportsDTO.getStartTime()).le("initial_time",queryReportsDTO.getEndTime());
        Page<Reports> reportsPage = reportsMapper.selectPage(page, queryWrapper);
        return reportsPage;
    }
}
