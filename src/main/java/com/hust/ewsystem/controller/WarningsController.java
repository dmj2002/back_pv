package com.hust.ewsystem.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hust.ewsystem.DAO.DTO.*;
import com.hust.ewsystem.DAO.PO.*;
import com.hust.ewsystem.DAO.VO.PicturesVO;
import com.hust.ewsystem.DAO.VO.WarningsVO;
import com.hust.ewsystem.common.exception.CrudException;
import com.hust.ewsystem.common.result.EwsResult;
import com.hust.ewsystem.mapper.PictureStandRelateMapper;
import com.hust.ewsystem.mapper.PicturesMapper;
import com.hust.ewsystem.mapper.ReportWarningRelateMapper;
import com.hust.ewsystem.mapper.ReportsMapper;
import com.hust.ewsystem.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.sql.Timestamp;
import java.util.stream.Collectors;

import static com.hust.ewsystem.service.impl.ModelsServiceImpl.getTableName;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/warning")
public class WarningsController {

    private final WarningsService warningsService;

    private final ReportsMapper reportsMapper;

    private final ReportWarningRelateMapper reportWarningRelateMapper;

    private final ReportWarningRelateService reportWarningRelateService;

    private final PictureStandRelateMapper pictureStandRelateMapper;

    private final RealPointService realPointService;

    private final StandRealRelateService standRealRelateService;

    private final ModelsService modelsService;

    private final StandPointService standPointService;

    private final CommonDataService commonDataService;

    private final PicturesMapper picturesMapper;

    private final CombinerBoxService combinerBoxService;

    private final BoxTransService boxTransService;

    private final InverterService inverterService;

    @GetMapping("/list")
    public EwsResult<?> getWarningList(@RequestParam(value = "page") int page,
                                       @RequestParam(value = "pageSize") int pageSize,
                                       @RequestParam(value = "startDate") String startDate,
                                       @RequestParam(value = "endDate", required = false) String endDate,
                                       @RequestParam(value = "warningLevel", required = false) Integer warningLevel,
                                       @RequestParam(value = "companyId") Integer companyId,
                                       @RequestParam(value = "pvFarmId", required = false) Integer pvFarmId,
                                       @RequestParam(value = "inverterId", required = false) Integer inverterId,
                                       @RequestParam(value = "combinerBoxId", required = false) Integer combinerBoxId){
        return warningsService.getWarningList(page, pageSize, startDate, endDate, warningLevel, companyId, pvFarmId, inverterId, combinerBoxId);
    }
    @GetMapping("/nowList")
    public EwsResult<?> getWarningList(@RequestParam(value = "page") int page,
                                       @RequestParam(value = "pageSize") int pageSize,
                                       @RequestParam(value = "warningLevel", required = false) Integer warningLevel,
                                       @RequestParam(value = "companyId") Integer companyId,
                                       @RequestParam(value = "pvFarmId", required = false) Integer pvFarmId,
                                       @RequestParam(value = "inverterId", required = false) Integer inverterId,
                                       @RequestParam(value = "combinerBoxId", required = false) Integer combinerBoxId){
        return warningsService.getWarningNowList(page, pageSize, warningLevel, companyId, pvFarmId, inverterId, combinerBoxId);
    }
    @PostMapping("/trendData")
    public EwsResult<?> getWarningTrendData(@RequestBody QueryWarnDetailsDTO queryWarnDetailsDTO){
        return warningsService.getWarningTrendData(queryWarnDetailsDTO);
    }
    @RequestMapping(value = "/queryDeviceWarnMatrix",method = RequestMethod.POST)
    public EwsResult<?> queryTurbineWarnMatrix(@RequestBody QueryPvWarnMatrixDTO queryPvWarnMatrixDTO){
        return warningsService.queryTurbineWarnMatrix(queryPvWarnMatrixDTO);
    }
    @PostMapping("/operate")
    public EwsResult<?> operateWarning(@RequestBody WarningOperateDTO warningOperateDTO) {
        //关闭待确认操作
        if(warningOperateDTO.getOperateCode() == 0){
            for(Integer warningId : warningOperateDTO.getWarningId()){
                Warnings warning = warningsService.getById(warningId);
                if(warning == null){
                    throw new CrudException("预警不存在");
                }
                warning.setWarningStatus(3);
                warning.setHandlerId(warningOperateDTO.getOperatorId());
                warning.setHandleTime(LocalDateTime.now());
                warning.setRepetition(warningOperateDTO.getRepetition());
                warning.setValid(warningOperateDTO.getValid());
                warningsService.updateById(warning);
            }
            return EwsResult.OK("关闭成功");
        }
        //挂起操作
        else if(warningOperateDTO.getOperateCode() == 1){
            for(Integer warningId : warningOperateDTO.getWarningId()){
                Warnings warning = warningsService.getById(warningId);
                if(warning == null){
                    throw new CrudException("预警不存在");
                }
                warning.setWarningStatus(1);
                warning.setHandlerId(warningOperateDTO.getOperatorId());
                warning.setHandleTime(LocalDateTime.now());
                warningsService.updateById(warning);
            }
            return EwsResult.OK("挂起成功");
        }
        //分级操作
        else if(warningOperateDTO.getOperateCode() == 2){
            for(Integer warningId : warningOperateDTO.getWarningId()){
                Warnings warning = warningsService.getById(warningId);
                if(warning == null){
                    throw new CrudException("预警不存在");
                }
                warning.setHandleTime(LocalDateTime.now());
                warning.setHandlerId(warningOperateDTO.getOperatorId());
                warning.setWarningLevel(warningOperateDTO.getWarningLevel());
                warningsService.updateById(warning);
            }
            return EwsResult.OK("分级成功");
        }
        //通知操作
        else if(warningOperateDTO.getOperateCode() == 3){
            List<DeviceDTO> deviceList = warningsService.getDeviceByWarningIdList(warningOperateDTO.getWarningId());
            if(deviceList.size() > 1){
                throw new CrudException("设备不唯一，不可以转通知");
            }
            Reports report = Reports.builder()
                    .reportText(warningOperateDTO.getReportText())
                    .deviceId(deviceList.get(0).getDeviceId())
                    .deviceType(deviceList.get(0).getDeviceType())
                    .status(0)
                    .initialTime(LocalDateTime.now())
                    .valid(warningOperateDTO.getValid())
                    .repetition(warningOperateDTO.getRepetition())
                    .employeeId(warningOperateDTO.getOperatorId())
                    .build();
            reportsMapper.insert(report);
            for(Integer warningId : warningOperateDTO.getWarningId()){
                reportWarningRelateMapper.insert(ReportWarningRelate.builder()
                        .reportId(report.getReportId())
                        .warningId(warningId)
                        .build());
                Warnings warning = warningsService.getById(warningId);
                if(warning == null){
                    throw new CrudException("预警不存在");
                }
                warning.setWarningStatus(2);
                warning.setHandlerId(warningOperateDTO.getOperatorId());
                warning.setHandleTime(LocalDateTime.now());
                warningsService.updateById(warning);
            }
            return EwsResult.OK("通知成功",report);
        }
        //确认关闭操作
        else if(warningOperateDTO.getOperateCode() == 4){
            for(Integer warningId : warningOperateDTO.getWarningId()){
                Warnings warning = warningsService.getById(warningId);
                if(warning == null || warning.getWarningStatus() != 3){
                    throw new CrudException("预警不存在或未关闭");
                }
                warning.setWarningStatus(4);
                warning.setHandlerId(warningOperateDTO.getOperatorId());
                warning.setHandleTime(LocalDateTime.now());
                warningsService.updateById(warning);
            }
            return EwsResult.OK("确认关闭成功");
        }
        else{
            throw new CrudException("操作码错误");
        }

    }

    @RequestMapping(value = "/getWarnList",method = RequestMethod.POST)
    public EwsResult<IPage<WarningsVO>> getWarnList(@Valid @RequestBody QueryWarnDTO queryWarnDTO){
        IPage<WarningsVO> warnInfo = warningsService.getWarnInfo(queryWarnDTO);
        return EwsResult.OK(warnInfo);
    }

    @RequestMapping(value = "/getWarnInfoListByReportId",method = RequestMethod.GET)
    public EwsResult<List<Warnings>> getWarnInfoListByReportId(@RequestParam(value = "reportId") @NotNull(message = "通知ID不能为空") Integer reportId){
        LambdaQueryWrapper<ReportWarningRelate> relateWrapper = new LambdaQueryWrapper<>();
        relateWrapper.eq(ReportWarningRelate::getReportId,reportId);
        List<Integer> warnIdList = new ArrayList<>();
        List<ReportWarningRelate> reportWarningRelateList = reportWarningRelateService.list(relateWrapper);
        if (!CollectionUtils.isEmpty(reportWarningRelateList)){
            for (ReportWarningRelate reportWarningRelate : reportWarningRelateList) {
                warnIdList.add(reportWarningRelate.getWarningId());
            }
        }
        List<Warnings> warnInfoListByReportId = warningsService.getWarnInfoListByReportId(warnIdList);
        return EwsResult.OK("处理成功",warnInfoListByReportId);
    }
    @RequestMapping(value = "/getWarnInfoList",method = RequestMethod.POST)
    public EwsResult<List<WarningsVO>> getWarnInfoList(@Valid @RequestBody QueryWarnInfoDTO queryWarnInfoDTO){
        List<WarningsVO> warnInfo = warningsService.getWarnDesc(queryWarnInfoDTO);
        return EwsResult.OK(warnInfo);
    }

    @RequestMapping(value = "/getWarnInfoListByDesc",method = RequestMethod.POST)
    public EwsResult<List<WarningsVO>> getWarnInfoListByDesc(@Valid @RequestBody QueryWarnInfoDTO queryWarnInfoDTO){
        List<WarningsVO> warnInfo = warningsService.getWarnInfo(queryWarnInfoDTO);
        return EwsResult.OK(warnInfo);
    }
    @PostMapping("/showPictures")
    public EwsResult<?> showPictures(@RequestBody Map<String, Object> warningForm) {
        Integer warningId = (Integer) warningForm.get("warningId");
        String startTime= (String) warningForm.get("startTime");
        String endTime= (String) warningForm.get("endTime");
        Warnings warning = warningsService.getById(warningId);
        Models model = modelsService.getById(warning.getModelId());
        Integer algorithmId = model.getAlgorithmId();
        Integer deviceId = model.getDeviceId();
        Integer deviceType = model.getModelType();
        Integer pvFarmId = getPvFarmId(deviceId, deviceType);
        List<Pictures> picturesList = picturesMapper.selectList(new QueryWrapper<Pictures>().eq("algorithm_id", algorithmId));
        List<PicturesVO> res = new ArrayList<>();
        for(Pictures picture : picturesList){
            PicturesVO picturesVO = new PicturesVO();
            Integer flag = picture.getFlag();
            if(flag == 0){
                picturesVO = initPictureVO(picture, pvFarmId, deviceId,deviceType, startTime, endTime);
            }else if(flag == 1){
                String pictureDescription = picture.getWarningDescription();
                String warningDescription = warning.getWarningDescription();
                if (warningDescription.startsWith("[") && warningDescription.endsWith("]")) {
                    warningDescription = warningDescription.substring(1, warningDescription.length() - 1);
                }
                String[] desc = warningDescription.split(",");
                for(String s : desc){
                    if (s.startsWith("'") && s.endsWith("'")) {
                        s = s.substring(1, s.length() - 1);
                    }
                    if(!pictureDescription.equals(s))continue;
                    picturesVO = initPictureVO(picture, pvFarmId,deviceId, deviceType,startTime, endTime);
                }
            }
            res.add(picturesVO);
        }
        return EwsResult.OK("查询成功", res);
    }

    private Integer getPvFarmId(Integer deviceId, Integer deviceType) {
        Integer pvFarmId = null;
        //获取电厂id
        if(deviceType == 1){
            Integer boxId = combinerBoxService.getById(deviceId).getBoxId();
            pvFarmId = boxTransService.getById(boxId).getPvFarmId();
        }else if(deviceType == 2){
            //获取逆变器id
            Integer boxId = inverterService.getById(deviceId).getBoxId();
            pvFarmId = boxTransService.getById(boxId).getPvFarmId();
        }
        return pvFarmId;
    }

    public PicturesVO initPictureVO(Pictures picture, Integer pvFarmId, Integer deviceId,Integer deviceType, String startTime, String endTime){
        PicturesVO picturesVO = new PicturesVO();
        picturesVO.setPictureId(picture.getId());
        picturesVO.setWarningDescription(picture.getWarningDescription());
        picturesVO.setPicName(picture.getPicName());
        picturesVO.setThreshold(picture.getThreshold());
        picturesVO.setPicType(picture.getPicType());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startDateTime = LocalDateTime.parse(startTime, formatter);
        LocalDateTime adjustedDateTime = startDateTime.minusSeconds(picture.getBias());
        String adjustedStartTime = adjustedDateTime.format(formatter);
        List<StandPointDTO> standPointDTOList = new ArrayList<>();
        pictureStandRelateMapper.selectList(new QueryWrapper<PictureStandRelate>().eq("picture_id", picture.getId())).forEach(pictureStandRelate -> {
            StandPointDTO standPointDTO = new StandPointDTO();
            standPointDTO.setPointId(pictureStandRelate.getStandPointId());
            StandPoint standPoint = standPointService.getById(pictureStandRelate.getStandPointId());
            standPointDTO.setPointDescription(standPoint.getPointDescription());
            standPointDTO.setPointLabel(standPoint.getPointLabel());
            List<Integer> realPointIds = standRealRelateService.list(new QueryWrapper<StandRealRelate>().eq("stand_point_id", pictureStandRelate.getStandPointId())).stream().map(StandRealRelate::getRealPointId).collect(Collectors.toList());
            RealPoint one;
            if(standPoint.getPointType() == 0){
                one = realPointService.getOne(new QueryWrapper<RealPoint>().in("point_id", realPointIds).eq("device_id", pvFarmId).eq("point_type", standPoint.getPointType()));
            }else{
                one = realPointService.getOne(new QueryWrapper<RealPoint>().in("point_id", realPointIds).eq("device_id", deviceId).eq("point_type", deviceType));
            }
            Integer finalId = standPoint.getPointType() == 0 ? pvFarmId : deviceId;
            String tableName = getTableName(standPoint.getPointType()) + "_" + finalId;
            String pointLabel = one.getPointLabel().toLowerCase();
            List<String> pointLabelList = Collections.singletonList(pointLabel);
            List<Map<String, Object>> mapList = commonDataService.selectDataByTime(tableName,pointLabelList, adjustedStartTime, endTime);
            List<CommonData> valueList = new ArrayList<>();
            for(Map<String, Object> map : mapList) {
                CommonData commonData = new CommonData();
                LocalDateTime datetime = ((Timestamp) map.get("datetime")).toLocalDateTime();
                commonData.setDatetime(datetime);
                Double value = Double.valueOf(map.get(pointLabel).toString());
                commonData.setValue(value);
                valueList.add(commonData);
            }
            standPointDTO.setPointValue(valueList);
            standPointDTOList.add(standPointDTO);
        });
        List<CommonData> calculateValue = new ArrayList<>();
        if(picture.getId() >=1 && picture.getId() <= 6){
            //如果是图片1-6，则需要获取计算值就用这个表达式，你自己给功率起个英文名：光伏组串功率=inverter_branch_voltage_1*inverter_branch_current_1
            calculateValue = setCalculateValue(standPointDTOList,"multiply","inverter_branch_voltage_1", "inverter_branch_current_1");
        }else if(picture.getId() == 55 || picture.getId() == 56){
            calculateValue = setCalculateValue(standPointDTOList,"div","inverter_output_active_power", "inverter_input_total_DC_power");
        }
        picturesVO.setCalculateValue(calculateValue);
        picturesVO.setPoints(standPointDTOList);
        return picturesVO;
    }

    private static List<CommonData> setCalculateValue(List<StandPointDTO> standPointDTOList,String operations,String ... args) {
        Optional<StandPointDTO> voltageDTOOpt = standPointDTOList.stream()
                .filter(dto -> args[0].equals(dto.getPointLabel()))
                .findFirst();

        Optional<StandPointDTO> currentDTOOpt = standPointDTOList.stream()
                .filter(dto -> args[1].equals(dto.getPointLabel()))
                .findFirst();
        if (voltageDTOOpt.isPresent() && currentDTOOpt.isPresent()) {
            StandPointDTO voltageDTO = voltageDTOOpt.get();
            StandPointDTO currentDTO = currentDTOOpt.get();
            Map<LocalDateTime, Double> currentValueMap = currentDTO.getPointValue().stream()
                    .filter(data -> data.getDatetime() != null && data.getValue() != null)
                    .collect(Collectors.toMap(
                            CommonData::getDatetime,
                            CommonData::getValue
                    ));
            List<CommonData> powerValues = voltageDTO.getPointValue().stream()
                    .filter(voltageData ->
                            voltageData.getValue() != null &&
                                    voltageData.getDatetime() != null &&
                                    currentValueMap.containsKey(voltageData.getDatetime())
                    )
                    .map(voltageData -> {
                        Double currentValue = currentValueMap.get(voltageData.getDatetime());
                        Double powerValue = null;
                        if(operations.equals("div") && currentValue == 0){
                            powerValue = voltageData.getValue() / currentValue;
                        }
                        else if(operations.equals("multiply")){
                            powerValue = voltageData.getValue() * currentValue;
                        }
                        CommonData powerData = new CommonData();
                        powerData.setValue(powerValue);
                        powerData.setDatetime(voltageData.getDatetime());
                        return powerData;
                    })
                    .collect(Collectors.toList());
            return powerValues;
        }else {
            System.out.println("未找到对应测点");
            return Collections.emptyList();
        }
    }
}
