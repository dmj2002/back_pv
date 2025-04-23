package com.hust.ewsystem.controller;

import com.hust.ewsystem.common.result.EwsResult;
import com.hust.ewsystem.service.WarningsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
