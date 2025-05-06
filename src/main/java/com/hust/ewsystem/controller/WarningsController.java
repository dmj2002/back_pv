package com.hust.ewsystem.controller;

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
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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

    private final PictureStandRelateMapper pictureStandRelateMapper;

    private final RealPointService realPointService;

    private final StandRealRelateService standRealRelateService;

    private final ModelsService modelsService;

    private final StandPointService standPointService;

    private final CommonDataService commonDataService;

    private final PicturesMapper picturesMapper;

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
        List<Pictures> picturesList = picturesMapper.selectList(new QueryWrapper<Pictures>().eq("algorithm_id", algorithmId));
        List<PicturesVO> res = new ArrayList<>();
        for(Pictures picture : picturesList){
            PicturesVO picturesVO = new PicturesVO();
            Integer flag = picture.getFlag();
            if(flag == 0){
                picturesVO = initPictureVO(picture, deviceId,deviceType, startTime, endTime);
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
                    picturesVO = initPictureVO(picture, deviceId, deviceType,startTime, endTime);
                }
            }
            res.add(picturesVO);
        }
        return EwsResult.OK("查询成功", res);
    }
    public PicturesVO initPictureVO(Pictures picture, Integer deviceId,Integer deviceType, String startTime, String endTime){
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
            List<Integer> realPointIds = standRealRelateService.list(new QueryWrapper<StandRealRelate>().eq("stand_point_id", pictureStandRelate.getStandPointId())).stream().map(StandRealRelate::getRealPointId).collect(Collectors.toList());
            RealPoint one = realPointService.getOne(new QueryWrapper<RealPoint>().in("point_id", realPointIds).eq("device_id", deviceId).eq("point_type", deviceType));
            String tableName = getTableName(deviceType) + "_" + deviceId;
            String pointLabel = one.getPointLabel().toLowerCase();
            List<String> pointLabelList = Collections.singletonList(pointLabel);
            List<Map<String, Object>> mapList = commonDataService.selectDataByTime(tableName,pointLabelList, adjustedStartTime, endTime);
            List<CommonData> valueList = new ArrayList<>();
            for(Map<String, Object> map : mapList) {
                CommonData commonData = new CommonData();
                commonData.setDatetime((LocalDateTime) map.get("datetime"));
                commonData.setValue((Double) map.get(pointLabel));
                valueList.add(commonData);
            }
            standPointDTO.setPointValue(valueList);
            standPointDTOList.add(standPointDTO);
        });
        picturesVO.setPoints(standPointDTOList);
        return picturesVO;
    }
}
