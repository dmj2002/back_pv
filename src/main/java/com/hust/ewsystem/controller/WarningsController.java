package com.hust.ewsystem.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hust.ewsystem.DAO.DTO.*;
import com.hust.ewsystem.DAO.PO.ReportWarningRelate;
import com.hust.ewsystem.DAO.PO.Reports;
import com.hust.ewsystem.DAO.PO.Warnings;
import com.hust.ewsystem.DAO.VO.WarningsVO;
import com.hust.ewsystem.common.exception.CrudException;
import com.hust.ewsystem.common.result.EwsResult;
import com.hust.ewsystem.mapper.ReportWarningRelateMapper;
import com.hust.ewsystem.mapper.ReportsMapper;
import com.hust.ewsystem.service.WarningsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/warning")
public class WarningsController {

    private final WarningsService warningsService;

    private final ReportsMapper reportsMapper;

    private final ReportWarningRelateMapper reportWarningRelateMapper;

    @GetMapping("/list")
    public EwsResult<?> getWarningList(@RequestParam(value = "page") int page,
                                       @RequestParam(value = "page_size") int pageSize,
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
                                       @RequestParam(value = "page_size") int pageSize,
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
    @RequestMapping(value = "/queryTurbineWarnMatrix",method = RequestMethod.POST)
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
}
