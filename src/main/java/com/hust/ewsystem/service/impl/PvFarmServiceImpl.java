package com.hust.ewsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hust.ewsystem.DAO.DTO.*;
import com.hust.ewsystem.DAO.PO.Models;
import com.hust.ewsystem.DAO.PO.PvFarm;
import com.hust.ewsystem.DAO.PO.Reports;
import com.hust.ewsystem.common.constant.CommonConstant;
import com.hust.ewsystem.mapper.PvFarmMapper;
import com.hust.ewsystem.mapper.ReportsMapper;
import com.hust.ewsystem.mapper.WarningsMapper;
import com.hust.ewsystem.service.ModelsService;
import com.hust.ewsystem.service.PvFarmService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
@RequiredArgsConstructor
public class PvFarmServiceImpl extends ServiceImpl<PvFarmMapper, PvFarm> implements PvFarmService {

    private final PvFarmMapper pvFarmMapper;

    private final ModelsService modelsService;

    private final WarningsMapper warningMapper;

    private final ReportsMapper reportsMapper;

    @Override
    public List<PvFarmDTO> getPvFarmsByCompanyId(QueryWaitDoneInfoDTO queryWaitDoneInfoDTO) {
        List<PvFarmDTO> pvFarmDTOList = pvFarmMapper.getPvFarmsByCompanyId(queryWaitDoneInfoDTO);
        Set<Integer> inverterDeviceIds = pvFarmDTOList.stream()
                .flatMap(pvFarm -> pvFarm.getInverterWaitDoneInfo().stream())
                .map(InverterWaitDoneDTO::getDeviceId)
                .collect(Collectors.toSet());
        Set<Integer> combinerDeviceIds = pvFarmDTOList.stream()
                .flatMap(pvFarm -> pvFarm.getInverterWaitDoneInfo().stream())
                .flatMap(inverter -> inverter.getCombinerBoxWaitDoneInfo().stream())
                .map(CombinerBoxWaitDoneDTO::getDeviceId)
                .collect(Collectors.toSet());
        Map<Integer, List<ModelsDTO>> inverterModelsMap = modelsService.list(new QueryWrapper<Models>().in("device_id", inverterDeviceIds).eq("model_type", 2))
                .stream()
                .collect(Collectors.groupingBy(
                        Models::getDeviceId,
                        Collectors.mapping(model -> new ModelsDTO()
                                        .setModelId(model.getModelId())
                                        .setModelName(model.getModelName()),
                                Collectors.toList())
                ));
        Map<Integer, List<ModelsDTO>> combinerModelsMap = modelsService.list(new QueryWrapper<Models>().in("device_id", combinerDeviceIds).eq("model_type", 1))
                .stream()
                .collect(Collectors.groupingBy(
                        Models::getDeviceId,
                        Collectors.mapping(model -> new ModelsDTO()
                                        .setModelId(model.getModelId())
                                        .setModelName(model.getModelName()),
                                Collectors.toList())
                ));
        // 收集所有模型id
        Set<Integer> allModelIds = new HashSet<>();
        inverterModelsMap.values().forEach(list -> list.forEach(dto -> allModelIds.add(dto.getModelId())));
        combinerModelsMap.values().forEach(list -> list.forEach(dto -> allModelIds.add(dto.getModelId())));

        //模型Id -> WarnStatusDTO
        Map<Integer, WarnStatusDTO> warningCountsMap = warningMapper.batchGetCount(
                allModelIds,
                queryWaitDoneInfoDTO.getStartDate(),
                queryWaitDoneInfoDTO.getEndDate()
        ).stream().collect(Collectors.toMap(WarnStatusDTO::getModelId, Function.identity()));

        // 批量查询报告计数
        Map<Integer, Integer> inverterReportCounts = inverterDeviceIds.stream()
                .collect(Collectors.toMap(Function.identity(), id -> 0));
        reportsMapper.selectMaps(
                new QueryWrapper<Reports>()
                        .select("device_id, count(*) as count")
                        .in("device_id", inverterDeviceIds)
                        .eq("device_type", 2)
                        .eq("status", CommonConstant.NUM_COMMON_0)
                        .ge("initial_time", queryWaitDoneInfoDTO.getStartDate())
                        .le("initial_time", queryWaitDoneInfoDTO.getEndDate())
                        .groupBy("device_id")
        ).forEach(map -> {
            Integer deviceId = (Integer) map.get("device_id");
            Long count = (Long) map.get("count");
            inverterReportCounts.put(deviceId, count.intValue());
        });
        // 先创建包含所有设备ID的Map，默认值0
        Map<Integer, Integer> combinerReportCounts = combinerDeviceIds.stream()
                .collect(Collectors.toMap(Function.identity(), id -> 0));
        reportsMapper.selectMaps(
                new QueryWrapper<Reports>()
                        .select("device_id, count(*) as count")
                        .in("device_id", combinerDeviceIds)
                        .eq("device_type", 1)
                        .eq("status", CommonConstant.NUM_COMMON_0)
                        .ge("initial_time", queryWaitDoneInfoDTO.getStartDate())
                        .le("initial_time", queryWaitDoneInfoDTO.getEndDate())
                        .groupBy("device_id")
        ).forEach(map -> {
            Integer deviceId = (Integer) map.get("device_id");
            Long count = (Long) map.get("count");
            combinerReportCounts.put(deviceId, count.intValue());
        });
        for(PvFarmDTO pvFarmDTO : pvFarmDTOList){
            List<InverterWaitDoneDTO> inverterWaitDoneInfo = pvFarmDTO.getInverterWaitDoneInfo();
            for(InverterWaitDoneDTO waitDoneInfo : inverterWaitDoneInfo){
                int warningLevel1waitDoneSum = 0;
                int warningLevel2waitDoneSum = 0;
                int warningLevel1waitHangUpSum = 0;
                int warningLevel2waitHangUpSum = 0;
                int warningLevel1waitCloseWaitSum = 0;
                int warningLevel2waitCloseWaitSum = 0;
                int infoType = queryWaitDoneInfoDTO.getInfoType();
                List<ModelsDTO> modelsDTOList = inverterModelsMap.getOrDefault(waitDoneInfo.getDeviceId(), new ArrayList<>());
                waitDoneInfo.setModelList(modelsDTOList);
                for(ModelsDTO modelsDTO : modelsDTOList){
                    WarnStatusDTO warnStatusDTO = warningCountsMap.get(modelsDTO.getModelId());
                    if(Objects.isNull(warnStatusDTO)){
                        warnStatusDTO = new WarnStatusDTO();
                        warnStatusDTO.setModelId(modelsDTO.getModelId());
                        warnStatusDTO.setWarningLevel1waitDone(0);
                        warnStatusDTO.setWarningLevel2waitDone(0);
                        warnStatusDTO.setWarningLevel1waitHangUp(0);
                        warnStatusDTO.setWarningLevel2waitHangUp(0);
                        warnStatusDTO.setWarningLevel1waitCloseWait(0);
                        warnStatusDTO.setWarningLevel2waitCloseWait(0);
                    }
                    int warningLevel1Sum = Stream.of(
                            warnStatusDTO.getWarningLevel1waitDone(),
                            warnStatusDTO.getWarningLevel1waitHangUp(),
                            warnStatusDTO.getWarningLevel1waitCloseWait()
                    ).mapToInt(Integer::intValue).sum();
                    int warningLevel2Sum = Stream.of(
                            warnStatusDTO.getWarningLevel2waitDone(),
                            warnStatusDTO.getWarningLevel2waitHangUp(),
                            warnStatusDTO.getWarningLevel2waitCloseWait()
                    ).mapToInt(Integer::intValue).sum();
                    if (CommonConstant.NUM_COMMON_1.equals(infoType)){
                        modelsDTO.setWarningLevel1Sum(warningLevel1Sum);
                    } else if (CommonConstant.NUM_COMMON_2.equals(infoType)) {
                        modelsDTO.setWarningLevel2Sum(warningLevel2Sum);
                    } else if (CommonConstant.NUM_COMMON_0.equals(infoType)) {
                        modelsDTO.setWarningLevel1Sum(warningLevel1Sum);
                        modelsDTO.setWarningLevel2Sum(warningLevel2Sum);
                    }
                    warningLevel1waitDoneSum += warnStatusDTO.getWarningLevel1waitDone();
                    warningLevel2waitDoneSum += warnStatusDTO.getWarningLevel2waitDone();
                    warningLevel1waitHangUpSum += warnStatusDTO.getWarningLevel1waitHangUp();
                    warningLevel2waitHangUpSum += warnStatusDTO.getWarningLevel2waitHangUp();
                    warningLevel1waitCloseWaitSum += warnStatusDTO.getWarningLevel2waitCloseWait();
                    warningLevel2waitCloseWaitSum += warnStatusDTO.getWarningLevel2waitCloseWait();
                }
                waitDoneInfo.setWarningLevel1waitDoneSum(infoType == 0 || infoType == 1 ? warningLevel1waitDoneSum : 0);
                waitDoneInfo.setWarningLevel2waitDoneSum(infoType == 0 || infoType == 2  ? warningLevel2waitDoneSum : 0);
                waitDoneInfo.setWarningLevel1waitHangUpSum(infoType == 0 || infoType == 1 ? warningLevel1waitHangUpSum : 0);
                waitDoneInfo.setWarningLevel2waitHangUpSum(infoType == 0 || infoType == 2  ? warningLevel2waitHangUpSum : 0);
                waitDoneInfo.setWarningLevel1waitCloseWaitSum(infoType == 0 || infoType == 1 ?warningLevel1waitCloseWaitSum : 0);
                waitDoneInfo.setWarningLevel2waitCloseWaitSum(infoType == 0 || infoType == 2  ? warningLevel2waitCloseWaitSum : 0);

                if (CommonConstant.NUM_COMMON_3.equals(infoType) || CommonConstant.NUM_COMMON_0.equals(infoType)){
                    Integer rePortSum = inverterReportCounts.get(waitDoneInfo.getDeviceId());
                    waitDoneInfo.setReportSum(rePortSum);
                }
                for (CombinerBoxWaitDoneDTO waitDoneDTO :waitDoneInfo.getCombinerBoxWaitDoneInfo()){
                    warningLevel1waitDoneSum = 0;
                    warningLevel2waitDoneSum = 0;
                    warningLevel1waitHangUpSum = 0;
                    warningLevel2waitHangUpSum = 0;
                    warningLevel1waitCloseWaitSum = 0;
                    warningLevel2waitCloseWaitSum = 0;
                    modelsDTOList = combinerModelsMap.getOrDefault(waitDoneDTO.getDeviceId(), new ArrayList<>());
                    waitDoneDTO.setModelList(modelsDTOList);
                    for(ModelsDTO modelsDTO : modelsDTOList){
                        WarnStatusDTO warnStatusDTO = warningCountsMap.get(modelsDTO.getModelId());
                        if(Objects.isNull(warnStatusDTO)){
                            warnStatusDTO = new WarnStatusDTO();
                            warnStatusDTO.setModelId(modelsDTO.getModelId());
                            warnStatusDTO.setWarningLevel1waitDone(0);
                            warnStatusDTO.setWarningLevel2waitDone(0);
                            warnStatusDTO.setWarningLevel1waitHangUp(0);
                            warnStatusDTO.setWarningLevel2waitHangUp(0);
                            warnStatusDTO.setWarningLevel1waitCloseWait(0);
                            warnStatusDTO.setWarningLevel2waitCloseWait(0);
                        }
                        int warningLevel1Sum = Stream.of(
                                warnStatusDTO.getWarningLevel1waitDone(),
                                warnStatusDTO.getWarningLevel1waitHangUp(),
                                warnStatusDTO.getWarningLevel1waitCloseWait()
                        ).mapToInt(Integer::intValue).sum();
                        int warningLevel2Sum = Stream.of(
                                warnStatusDTO.getWarningLevel2waitDone(),
                                warnStatusDTO.getWarningLevel2waitHangUp(),
                                warnStatusDTO.getWarningLevel2waitCloseWait()
                        ).mapToInt(Integer::intValue).sum();
                        if (CommonConstant.NUM_COMMON_1.equals(infoType)){
                            modelsDTO.setWarningLevel1Sum(warningLevel1Sum);
                        } else if (CommonConstant.NUM_COMMON_2.equals(infoType)) {
                            modelsDTO.setWarningLevel2Sum(warningLevel2Sum);
                        } else if (CommonConstant.NUM_COMMON_0.equals(infoType)) {
                            modelsDTO.setWarningLevel1Sum(warningLevel1Sum);
                            modelsDTO.setWarningLevel2Sum(warningLevel2Sum);
                        }
                        warningLevel1waitDoneSum += warnStatusDTO.getWarningLevel1waitDone();
                        warningLevel2waitDoneSum += warnStatusDTO.getWarningLevel2waitDone();
                        warningLevel1waitHangUpSum += warnStatusDTO.getWarningLevel1waitHangUp();
                        warningLevel2waitHangUpSum += warnStatusDTO.getWarningLevel2waitHangUp();
                        warningLevel1waitCloseWaitSum += warnStatusDTO.getWarningLevel2waitCloseWait();
                        warningLevel2waitCloseWaitSum += warnStatusDTO.getWarningLevel2waitCloseWait();
                    }
                    waitDoneDTO.setWarningLevel1waitDoneSum(infoType == 0 || infoType == 1 ? warningLevel1waitDoneSum : 0);
                    waitDoneDTO.setWarningLevel2waitDoneSum(infoType == 0 || infoType == 2  ? warningLevel2waitDoneSum : 0);
                    waitDoneDTO.setWarningLevel1waitHangUpSum(infoType == 0 || infoType == 1 ? warningLevel1waitHangUpSum : 0);
                    waitDoneDTO.setWarningLevel2waitHangUpSum(infoType == 0 || infoType == 2  ? warningLevel2waitHangUpSum : 0);
                    waitDoneDTO.setWarningLevel1waitCloseWaitSum(infoType == 0 || infoType == 1 ?warningLevel1waitCloseWaitSum : 0);
                    waitDoneDTO.setWarningLevel2waitCloseWaitSum(infoType == 0 || infoType == 2  ? warningLevel2waitCloseWaitSum : 0);

                    if (CommonConstant.NUM_COMMON_3.equals(infoType) || CommonConstant.NUM_COMMON_0.equals(infoType)){
                        Integer rePortSum = combinerReportCounts.get(waitDoneDTO.getDeviceId());
                        waitDoneDTO.setReportSum(rePortSum);
                    }
                }
            }
        }
        return pvFarmDTOList;
    }
//    public List<PvFarmDTO> getPvFarmsByCompanyId(QueryWaitDoneInfoDTO queryWaitDoneInfoDTO) {
//        List<PvFarmDTO> pvFarmDTOList = pvFarmMapper.getPvFarmsByCompanyId(queryWaitDoneInfoDTO);
//        LambdaQueryWrapper<Reports> queryWrapper;
//        for(PvFarmDTO pvFarmDTO : pvFarmDTOList){
//            List<InverterWaitDoneDTO> inverterWaitDoneInfo = pvFarmDTO.getInverterWaitDoneInfo();
//            for(InverterWaitDoneDTO waitDoneInfo : inverterWaitDoneInfo){
//                int warningLevel1waitDoneSum = 0;
//                int warningLevel2waitDoneSum = 0;
//                int warningLevel1waitHangUpSum = 0;
//                int warningLevel2waitHangUpSum = 0;
//                int warningLevel1waitCloseWaitSum = 0;
//                int warningLevel2waitCloseWaitSum = 0;
//                int infoType = queryWaitDoneInfoDTO.getInfoType();
//                List<ModelsDTO> modelsDTOList = modelsService.list(new QueryWrapper<Models>().eq("device_id", waitDoneInfo.getDeviceId()).eq("model_type", 2)).stream()
//                        .map(model -> new ModelsDTO()
//                                .setModelId(model.getModelId())
//                                .setModelName(model.getModelName())).collect(Collectors.toList());
//                waitDoneInfo.setModelList(modelsDTOList);
//                for(ModelsDTO modelsDTO : modelsDTOList){
//                    GetWarningsCountDTO getWarningsCountDTO = initGetWarningsCountInfo(queryWaitDoneInfoDTO, waitDoneInfo, modelsDTO);
//                    int warningsCount = warningMapper.getWarningsCount(getWarningsCountDTO);
//                    if (CommonConstant.NUM_COMMON_1.equals(infoType)){
//                        modelsDTO.setWarningLevel1Sum(warningsCount);
//                    } else if (CommonConstant.NUM_COMMON_2.equals(infoType)) {
//                        modelsDTO.setWarningLevel2Sum(warningsCount);
//                    } else if (CommonConstant.NUM_COMMON_0.equals(infoType)) {
//                        getWarningsCountDTO.setWarningLevel(CommonConstant.NUM_COMMON_1);
//                        warningsCount = warningMapper.getWarningsCount(getWarningsCountDTO);
//                        modelsDTO.setWarningLevel1Sum(warningsCount);
//                        getWarningsCountDTO.setWarningLevel(CommonConstant.NUM_COMMON_2);
//                        warningsCount = warningMapper.getWarningsCount(getWarningsCountDTO);
//                        modelsDTO.setWarningLevel2Sum(warningsCount);
//                    }
//                    WarnStatusDTO count = warningMapper.getCount(modelsDTO.getModelId(),queryWaitDoneInfoDTO.getStartDate(),queryWaitDoneInfoDTO.getEndDate());
//                    if (Objects.nonNull(count)){
//                        warningLevel1waitDoneSum += count.getWarningLevel1waitDone();
//                        warningLevel2waitDoneSum += count.getWarningLevel2waitDone();
//                        warningLevel1waitHangUpSum += count.getWarningLevel1waitHangUp();
//                        warningLevel2waitHangUpSum += count.getWarningLevel2waitHangUp();
//                        warningLevel1waitCloseWaitSum += count.getWarningLevel2waitCloseWait();
//                        warningLevel2waitCloseWaitSum += count.getWarningLevel2waitCloseWait();
//                    }
//                }
//                waitDoneInfo.setWarningLevel1waitDoneSum(infoType == 0 || infoType == 1 ? warningLevel1waitDoneSum : 0);
//                waitDoneInfo.setWarningLevel2waitDoneSum(infoType == 0 || infoType == 2  ? warningLevel2waitDoneSum : 0);
//                waitDoneInfo.setWarningLevel1waitHangUpSum(infoType == 0 || infoType == 1 ? warningLevel1waitHangUpSum : 0);
//                waitDoneInfo.setWarningLevel2waitHangUpSum(infoType == 0 || infoType == 2  ? warningLevel2waitHangUpSum : 0);
//                waitDoneInfo.setWarningLevel1waitCloseWaitSum(infoType == 0 || infoType == 1 ?warningLevel1waitCloseWaitSum : 0);
//                waitDoneInfo.setWarningLevel2waitCloseWaitSum(infoType == 0 || infoType == 2  ? warningLevel2waitCloseWaitSum : 0);
//
//                if (CommonConstant.NUM_COMMON_3.equals(infoType) || CommonConstant.NUM_COMMON_0.equals(infoType)){
//                    queryWrapper = new LambdaQueryWrapper<>();
//                    queryWrapper.eq(Reports::getDeviceId,waitDoneInfo.getDeviceId())
//                            .eq(Reports::getDeviceType,2)
//                            .eq(Reports::getStatus, CommonConstant.NUM_COMMON_0)
//                            .ge(Reports::getInitialTime,queryWaitDoneInfoDTO.getStartDate())
//                            .le(Reports::getInitialTime,queryWaitDoneInfoDTO.getEndDate());
//                    Long count = reportsMapper.selectCount(queryWrapper);
//                    Integer rePortSum = Optional.ofNullable(count).map(Long::intValue).orElse(0);
//                    waitDoneInfo.setReportSum(rePortSum);
//                }
//                for (CombinerBoxWaitDoneDTO waitDoneDTO :waitDoneInfo.getCombinerBoxWaitDoneInfo()){
//                    warningLevel1waitDoneSum = 0;
//                    warningLevel2waitDoneSum = 0;
//                    warningLevel1waitHangUpSum = 0;
//                    warningLevel2waitHangUpSum = 0;
//                    warningLevel1waitCloseWaitSum = 0;
//                    warningLevel2waitCloseWaitSum = 0;
//                    modelsDTOList = modelsService.list(new QueryWrapper<Models>().eq("device_id", waitDoneDTO.getDeviceId()).eq("model_type", 1)).stream()
//                            .map(model -> new ModelsDTO()
//                                    .setModelId(model.getModelId())
//                                    .setModelName(model.getModelName())).collect(Collectors.toList());
//                    waitDoneDTO.setModelList(modelsDTOList);
//                    for(ModelsDTO modelsDTO : modelsDTOList){
//                        GetWarningsCountDTO getWarningsCountDTO = initGetWarningsCountDTO(queryWaitDoneInfoDTO, waitDoneDTO, modelsDTO);
//                        int warningsCount = warningMapper.getWarningsCount(getWarningsCountDTO);
//                        if (CommonConstant.NUM_COMMON_1.equals(infoType)){
//                            modelsDTO.setWarningLevel1Sum(warningsCount);
//                        } else if (CommonConstant.NUM_COMMON_2.equals(infoType)) {
//                            modelsDTO.setWarningLevel2Sum(warningsCount);
//                        } else if (CommonConstant.NUM_COMMON_0.equals(infoType)) {
//                            getWarningsCountDTO.setWarningLevel(CommonConstant.NUM_COMMON_1);
//                            warningsCount = warningMapper.getWarningsCount(getWarningsCountDTO);
//                            modelsDTO.setWarningLevel1Sum(warningsCount);
//                            getWarningsCountDTO.setWarningLevel(CommonConstant.NUM_COMMON_2);
//                            warningsCount = warningMapper.getWarningsCount(getWarningsCountDTO);
//                            modelsDTO.setWarningLevel2Sum(warningsCount);
//                        }
//                        WarnStatusDTO count = warningMapper.getCount(modelsDTO.getModelId(),queryWaitDoneInfoDTO.getStartDate(),queryWaitDoneInfoDTO.getEndDate());
//                        if (Objects.nonNull(count)){
//                            warningLevel1waitDoneSum += count.getWarningLevel1waitDone();
//                            warningLevel2waitDoneSum += count.getWarningLevel2waitDone();
//                            warningLevel1waitHangUpSum += count.getWarningLevel1waitHangUp();
//                            warningLevel2waitHangUpSum += count.getWarningLevel2waitHangUp();
//                            warningLevel1waitCloseWaitSum += count.getWarningLevel2waitCloseWait();
//                            warningLevel2waitCloseWaitSum += count.getWarningLevel2waitCloseWait();
//                        }
//                    }
//                    waitDoneDTO.setWarningLevel1waitDoneSum(infoType == 0 || infoType == 1 ? warningLevel1waitDoneSum : 0);
//                    waitDoneDTO.setWarningLevel2waitDoneSum(infoType == 0 || infoType == 2  ? warningLevel2waitDoneSum : 0);
//                    waitDoneDTO.setWarningLevel1waitHangUpSum(infoType == 0 || infoType == 1 ? warningLevel1waitHangUpSum : 0);
//                    waitDoneDTO.setWarningLevel2waitHangUpSum(infoType == 0 || infoType == 2  ? warningLevel2waitHangUpSum : 0);
//                    waitDoneDTO.setWarningLevel1waitCloseWaitSum(infoType == 0 || infoType == 1 ?warningLevel1waitCloseWaitSum : 0);
//                    waitDoneDTO.setWarningLevel2waitCloseWaitSum(infoType == 0 || infoType == 2  ? warningLevel2waitCloseWaitSum : 0);
//
//                    if (CommonConstant.NUM_COMMON_3.equals(infoType) || CommonConstant.NUM_COMMON_0.equals(infoType)){
//                        queryWrapper = new LambdaQueryWrapper<>();
//                        queryWrapper.eq(Reports::getDeviceId,waitDoneDTO.getDeviceId())
//                                .eq(Reports::getDeviceType,1)
//                                .eq(Reports::getStatus, CommonConstant.NUM_COMMON_0)
//                                .ge(Reports::getInitialTime,queryWaitDoneInfoDTO.getStartDate())
//                                .le(Reports::getInitialTime,queryWaitDoneInfoDTO.getEndDate());
//                        Long count = reportsMapper.selectCount(queryWrapper);
//                        Integer rePortSum = Optional.ofNullable(count).map(Long::intValue).orElse(0);
//                        waitDoneDTO.setReportSum(rePortSum);
//                    }
//                }
//            }
//        }
//        return pvFarmDTOList;
//    }

    private GetWarningsCountDTO initGetWarningsCountDTO(QueryWaitDoneInfoDTO queryWaitDoneInfoDTO, CombinerBoxWaitDoneDTO waitDoneDTO, ModelsDTO modelsDTO) {
        GetWarningsCountDTO getWarningsCountDTO = new GetWarningsCountDTO();
        getWarningsCountDTO.setDeviceId(waitDoneDTO.getDeviceId());
        getWarningsCountDTO.setDeviceType(1);
        getWarningsCountDTO.setModelId(modelsDTO.getModelId());
        getWarningsCountDTO.setWarningLevel(queryWaitDoneInfoDTO.getInfoType());
        getWarningsCountDTO.setStartTime(queryWaitDoneInfoDTO.getStartDate());
        getWarningsCountDTO.setEndTime(queryWaitDoneInfoDTO.getEndDate());
        return getWarningsCountDTO;
    }

    private GetWarningsCountDTO initGetWarningsCountInfo(QueryWaitDoneInfoDTO queryWaitDoneInfoDTO, InverterWaitDoneDTO waitDoneInfo, ModelsDTO modelsDTO) {
        GetWarningsCountDTO getWarningsCountDTO = new GetWarningsCountDTO();
        getWarningsCountDTO.setDeviceId(waitDoneInfo.getDeviceId());
        getWarningsCountDTO.setDeviceType(2);
        getWarningsCountDTO.setModelId(modelsDTO.getModelId());
        getWarningsCountDTO.setWarningLevel(queryWaitDoneInfoDTO.getInfoType());
        getWarningsCountDTO.setStartTime(queryWaitDoneInfoDTO.getStartDate());
        getWarningsCountDTO.setEndTime(queryWaitDoneInfoDTO.getEndDate());
        return getWarningsCountDTO;
    }
}
