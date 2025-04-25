package com.hust.ewsystem.controller;

import com.hust.ewsystem.DAO.DTO.QueryPvWarnMatrixDTO;
import com.hust.ewsystem.DAO.DTO.QueryWarnDetailsDTO;
import com.hust.ewsystem.common.result.EwsResult;
import com.hust.ewsystem.service.WarningsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/warning")
public class WarningsController {

    private final WarningsService warningsService;

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
}
