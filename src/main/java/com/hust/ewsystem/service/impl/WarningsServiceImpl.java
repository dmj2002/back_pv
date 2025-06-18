package com.hust.ewsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hust.ewsystem.DAO.DTO.*;
import com.hust.ewsystem.DAO.PO.*;
import com.hust.ewsystem.DAO.VO.PvWarnMatrixVO;
import com.hust.ewsystem.DAO.VO.WarningsVO;
import com.hust.ewsystem.common.result.EwsResult;
import com.hust.ewsystem.mapper.StandPointMapper;
import com.hust.ewsystem.mapper.WarningsMapper;
import com.hust.ewsystem.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class WarningsServiceImpl extends ServiceImpl<WarningsMapper, Warnings> implements WarningsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WarningsServiceImpl.class);

    private final CombinerBoxService combinerBoxService;

    private final BoxTransService boxTransService;

    private final InverterService inverterService;

    private final ModelsService modelsService;

    private final StandRealRelateService standRealRelateService;

    private final StandPointMapper standPointMapper;

    private final RealPointService realPointService;

    private final PvFarmService pvFarmService;

    private final WarningsMapper warningsMapper;

    @Override
    public EwsResult<?> getWarningList(int page, int pageSize, String startDate, String endDate, Integer warningLevel, Integer companyId, Integer pvFarmId, Integer inverterId, Integer combinerBoxId) {
        // 校验分页参数
        if (page <= 0 || pageSize <= 0) {
            return EwsResult.error("分页参数不合法");
        }

        // 获取 modelIdlist
        QueryWrapper<Models> queryWrapper = new QueryWrapper<>();
        List<Integer> modelIdlist = new ArrayList<>();

        if (pvFarmId != null) {
            List<Integer> boxIds = boxTransService.list(new QueryWrapper<BoxTrans>().eq("pv_farm_id", pvFarmId))
                    .stream().map(BoxTrans::getId).collect(Collectors.toList());
            List<Integer> combinerIds = combinerBoxService.list(new QueryWrapper<CombinerBox>().in("box_id", boxIds))
                    .stream().map(CombinerBox::getId).collect(Collectors.toList());
            List<Integer> inverterIds = inverterService.list(new QueryWrapper<Inverter>().in("box_id", boxIds))
                    .stream().map(Inverter::getId).collect(Collectors.toList());

            // 构建条件：避免 device_id IN () 的情况
            if (!CollectionUtils.isEmpty(inverterIds)) {
                queryWrapper.nested(wrapper -> wrapper.in("device_id", inverterIds).eq("model_type", 2));
            }
            if (!CollectionUtils.isEmpty(combinerIds)) {
                queryWrapper.or(wrapper -> wrapper.in("device_id", combinerIds).eq("model_type", 1));
            }

            modelIdlist = modelsService.list(queryWrapper).stream().map(Models::getModelId).collect(Collectors.toList());
        } else if (inverterId != null) {
            List<Integer> combinerIds = combinerBoxService.list(new QueryWrapper<CombinerBox>().eq("inverter_id", inverterId))
                    .stream().map(CombinerBox::getId).collect(Collectors.toList());

            // 构建条件：避免 device_id IN () 的情况
            queryWrapper.nested(wrapper -> wrapper.eq("device_id", inverterId).eq("model_type", 2));
            if (!CollectionUtils.isEmpty(combinerIds)) {
                queryWrapper.or(wrapper -> wrapper.in("device_id", combinerIds).eq("model_type", 1));
            }

            modelIdlist = modelsService.list(queryWrapper).stream().map(Models::getModelId).collect(Collectors.toList());
        } else if (combinerBoxId != null) {
            queryWrapper.eq("device_id", combinerBoxId).eq("model_type", 1);
            modelIdlist = modelsService.list(queryWrapper).stream().map(Models::getModelId).collect(Collectors.toList());
        } else {
            List<Integer> boxIds = boxTransService.list().stream().map(BoxTrans::getId).collect(Collectors.toList());
            List<Integer> combinerIds = combinerBoxService.list(new QueryWrapper<CombinerBox>().in("box_id", boxIds))
                    .stream().map(CombinerBox::getId).collect(Collectors.toList());
            List<Integer> inverterIds = inverterService.list(new QueryWrapper<Inverter>().in("box_id", boxIds))
                    .stream().map(Inverter::getId).collect(Collectors.toList());

            // 构建条件：避免 device_id IN () 的情况
            if (!CollectionUtils.isEmpty(inverterIds)) {
                queryWrapper.nested(wrapper -> wrapper.in("device_id", inverterIds).eq("model_type", 2));
            }
            if (!CollectionUtils.isEmpty(combinerIds)) {
                queryWrapper.or(wrapper -> wrapper.in("device_id", combinerIds).eq("model_type", 1));
            }

            modelIdlist = modelsService.list(queryWrapper).stream().map(Models::getModelId).collect(Collectors.toList());
        }

        // 如果 modelIdlist 为空，直接返回空结果
        if (CollectionUtils.isEmpty(modelIdlist)) {
            Map<String, Object> result = new HashMap<>();
            result.put("warningList", new ArrayList<>());
            result.put("total_count", 0);
            result.put("page", page);
            result.put("page_size", pageSize);
            result.put("total_pages", 0);
            return EwsResult.OK("查询成功", result);
        }

        // 构建警告查询条件
        QueryWrapper<Warnings> queryWrapper2 = new QueryWrapper<>();
        queryWrapper2.in("model_id", modelIdlist);

        // 处理时间范围
        if (StringUtils.isNotBlank(startDate)) {
            queryWrapper2.ge("start_time", startDate);
        }
        if (StringUtils.isNotBlank(endDate)) {
            queryWrapper2.le("end_time", endDate);
        } else {
            queryWrapper2.le("end_time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }

        // 处理警告级别
        if (warningLevel != null) {
            queryWrapper2.eq("warning_level", warningLevel);
        }

        // 分页查询
        Page<Warnings> warningsPage = new Page<>(page, pageSize);
        Page<Warnings> page1 = page(warningsPage, queryWrapper2);
        // 构建返回结果
        if (page1.getRecords().isEmpty()) {
            Map<String, Object> result = new HashMap<>();
            result.put("warningList", new ArrayList<>());
            result.put("total_count", 0);
            result.put("page", page);
            result.put("page_size", pageSize);
            result.put("total_pages", 0);
            return EwsResult.OK("查询成功", result);
        }

        // 获取所有 model_id
        List<Integer> modelIds = page1.getRecords().stream()
                .map(Warnings::getModelId)
                .distinct()
                .collect(Collectors.toList());

        // 批量查询 models 表
        Map<Integer, Models> modelsMap = modelsService.listByIds(modelIds).stream()
                .collect(Collectors.toMap(Models::getModelId, model -> model));

        // 构造返回的 warningList，动态添加 deviceId 和 modelType
        List<Map<String, Object>> warningList = page1.getRecords().stream().map(warning -> {
            Map<String, Object> warningMap = new HashMap<>();
            warningMap.put("warningId", warning.getWarningId());
            warningMap.put("warningLevel", warning.getWarningLevel());
            warningMap.put("warningStatus", warning.getWarningStatus());
            warningMap.put("modelId", warning.getModelId());
            warningMap.put("startTime", warning.getStartTime());
            warningMap.put("endTime", warning.getEndTime());
            warningMap.put("handleTime", warning.getHandleTime());
            warningMap.put("warningDescription", warning.getWarningDescription());
            warningMap.put("valid", warning.getValid());
            warningMap.put("repetition", warning.getRepetition());

            // 动态添加 deviceId 和 modelType
            Models model = modelsMap.get(warning.getModelId());
            if (model != null) {
                warningMap.put("deviceId", model.getDeviceId());
                warningMap.put("modelType", model.getModelType());
            } else {
                warningMap.put("deviceId", null);
                warningMap.put("modelType", null);
            }

            return warningMap;
        }).collect(Collectors.toList());

        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("warningList", warningList);
        result.put("total_count", page1.getTotal());
        result.put("page", page1.getCurrent());
        result.put("page_size", page1.getSize());
        result.put("total_pages", page1.getPages());

        return EwsResult.OK("查询成功", result);
    }

    @Override
    public EwsResult<?> getWarningNowList(int page, int pageSize, Integer warningLevel, Integer companyId, Integer pvFarmId, Integer inverterId, Integer combinerBoxId) {
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

    @Override
    public EwsResult<?> getWarningTrendData(QueryWarnDetailsDTO queryWarnDetailsDTO) {
        List<Integer> standPointIdList = queryWarnDetailsDTO.getPointIdList();
        QueryWrapper<StandRealRelate> queryWrapper;
        QueryWrapper<RealPoint> realPointQueryWrapper;
        List<Map<Integer, RealPoint>> relPointAndLableList = new ArrayList<>();
        Map<Integer,RealPoint> relPointAndLableMap;
        for (Integer standPointId : standPointIdList) {
            StandPoint standPoint = standPointMapper.selectById(standPointId);
            queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(StandRealRelate::getStandPointId,standPointId);
            List<StandRealRelate> standRealRelateList = standRealRelateService.list(queryWrapper);
            if (CollectionUtils.isEmpty(standRealRelateList)){
                LOGGER.error(String.format("标准测点id【%s】与对应的真实测点关联关系不存在",standPointId));
                return EwsResult.error("测点不存在,请检查参数后重试",null);
            }
            List<Integer> realPointList = new ArrayList<>();
            for (StandRealRelate standRealRelate : standRealRelateList) {
                realPointList.add(standRealRelate.getRealPointId());
            }
            realPointQueryWrapper = new QueryWrapper<>();
            realPointQueryWrapper.lambda().in(RealPoint::getPointId,realPointList).eq(RealPoint::getDeviceId,queryWarnDetailsDTO.getDeviceId()).eq(RealPoint::getPointType,standPoint.getPointType());
            RealPoint realPoint = realPointService.getOne(realPointQueryWrapper);
            if (Objects.isNull(realPoint)){
                String realIdList = StringUtils.join(realPointList, ",");
                LOGGER.error(String.format("获取真实测点信息为空,真实测点id【%s】,设备id【%d】",realIdList,queryWarnDetailsDTO.getDeviceId()));
                return EwsResult.error("测点不存在,请检查参数后重试",null);
            }
            relPointAndLableMap = new HashMap<>();
            relPointAndLableMap.put(realPoint.getPointId(), realPoint);
            relPointAndLableList.add(relPointAndLableMap);
        }

        // 查询测点值
        List<TrendDataDTO> realPointValueList = realPointService.getRealPointValueList(relPointAndLableList, queryWarnDetailsDTO);
        return EwsResult.OK(realPointValueList);
    }

    @Override
    public EwsResult<?> queryTurbineWarnMatrix(QueryPvWarnMatrixDTO queryPvWarnMatrixDTO) {
        List<PvFarm> pvFarmList = getPvFarmList(queryPvWarnMatrixDTO.getPvFarmId());
        List<PvWarnMatrixVO> result = new ArrayList<>();
        WarnCountDTO warnCountDTO;
        PvWarnMatrixVO pvWarnMatrixVO;
        for(PvFarm pvFarm : pvFarmList){
            pvWarnMatrixVO = new PvWarnMatrixVO();
            pvWarnMatrixVO.setPvFarmName(pvFarm.getPvFarmName());
            Integer pvFarmId = pvFarm.getId();
            List<Integer> boxIds = boxTransService.list(new QueryWrapper<BoxTrans>().eq("pv_farm_id", pvFarmId)).stream().map(BoxTrans::getId).collect(Collectors.toList());
            List<Integer> combinerIds = combinerBoxService.list(new QueryWrapper<CombinerBox>().in("box_id", boxIds)).stream().map(CombinerBox::getId).collect(Collectors.toList());
            List<Integer> inverterIds = inverterService.list(new QueryWrapper<Inverter>().in("box_id", boxIds)).stream().map(Inverter::getId).collect(Collectors.toList());
            List<WarnCountDTO> warnCounts = new ArrayList<>();
            Map<Integer,Integer> combinerAndInverterMap = new HashMap<>();
            for(Integer combinerId : combinerIds){
                warnCountDTO = new WarnCountDTO();
                warnCountDTO.setDeviceId(combinerId);
                warnCountDTO.setDeviceType(1);
                int warnCount = getWarnCount(combinerId,1,queryPvWarnMatrixDTO);
                combinerAndInverterMap.put(combinerId, warnCount);
                warnCountDTO.setWarnCount(warnCount);
                warnCounts.add(warnCountDTO);
            }
            for(Integer inverterId : inverterIds){
                warnCountDTO = new WarnCountDTO();
                warnCountDTO.setDeviceId(inverterId);
                warnCountDTO.setDeviceType(2);
                int warnCount = getWarnCount(inverterId, 2, queryPvWarnMatrixDTO) +
                        combinerBoxService.list(new QueryWrapper<CombinerBox>().eq("inverter_id", inverterId)).stream()
                                .filter(combinerBox -> combinerAndInverterMap.containsKey(combinerBox.getId()))
                                .mapToInt(combinerBox -> combinerAndInverterMap.get(combinerBox.getId()))
                                .sum();
                warnCountDTO.setWarnCount(warnCount);
                warnCounts.add(warnCountDTO);
            }
            pvWarnMatrixVO.setWarnCountList(warnCounts);
            result.add(pvWarnMatrixVO);
        }
        return EwsResult.OK(result);
    }

    @Override
    public List<DeviceDTO> getDeviceByWarningIdList(List<Integer> warningId) {
        return warningsMapper.getDeviceByWarningIdList(warningId);
    }

    @Override
    public IPage<WarningsVO> getWarnInfo(QueryWarnDTO queryWarnDTO) {
        Page<Warnings> page = new Page<>(queryWarnDTO.getPageNo(),queryWarnDTO.getPageSize());
        return warningsMapper.selectWarningsPage(queryWarnDTO,page);
    }

    @Override
    public List<WarningsVO> getWarnDesc(QueryWarnInfoDTO queryWarnInfoDTO) {
        return warningsMapper.selectWarningsDesc(queryWarnInfoDTO);
    }

    @Override
    public List<WarningsVO> getWarnInfo(QueryWarnInfoDTO queryWarnInfoDTO) {
        List<WarningsVO> warnings = warningsMapper.selectWarningsNoPage(queryWarnInfoDTO);
        return warnings;
    }

    private int getWarnCount(Integer deviceId,Integer deviceType, QueryPvWarnMatrixDTO queryPvWarnMatrixDTO) {
        int warnCount = 0;
        LambdaQueryWrapper<Models> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Models::getDeviceId, deviceId).eq(Models::getModelType, deviceType);
        List<Integer> modelIds = modelsService.list(queryWrapper).stream().map(Models::getModelId).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(modelIds)){
            LOGGER.error(String.format("设备id【%d】,设备类型【%d】,查询模型为空",deviceId,deviceType));
            return warnCount;
        }
        LambdaQueryWrapper<Warnings> warningsWrapper = new LambdaQueryWrapper<>();
        warningsWrapper.in(Warnings::getModelId,modelIds)
                .ge(Warnings::getStartTime,queryPvWarnMatrixDTO.getStartDate()).le(Warnings::getEndTime,queryPvWarnMatrixDTO.getEndDate());
        List<Warnings> warnings = list(warningsWrapper);
        if (!CollectionUtils.isEmpty(warnings)){
            warnCount += warnings.size();
        }
        return warnCount;
    }

    public List<PvFarm> getPvFarmList(Integer pvFarmId){
        List<PvFarm> list;
        if(Objects.isNull(pvFarmId)){
            list = pvFarmService.list();
        } else {
            LambdaQueryWrapper<PvFarm> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(PvFarm::getId,pvFarmId);
            list = pvFarmService.list(wrapper);
        }
        return list;
    }
}
