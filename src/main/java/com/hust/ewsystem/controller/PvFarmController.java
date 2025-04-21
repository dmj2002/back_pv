package com.hust.ewsystem.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hust.ewsystem.DAO.PO.PvFarm;
import com.hust.ewsystem.common.result.EwsResult;
import com.hust.ewsystem.service.PvFarmService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/pvFarm")
@RequiredArgsConstructor
public class PvFarmController {

    private final PvFarmService pvFarmService;

    @GetMapping("/getpvfarmList")
    public EwsResult<?> getPvFarmList(@RequestParam(value = "companyId",required = false) Integer companyId) {
        QueryWrapper<PvFarm> queryWrapper = new QueryWrapper<>();
        if(companyId != null){
            queryWrapper.eq("company_id", companyId);
        }
        List<PvFarm> res = pvFarmService.list(queryWrapper);
        return EwsResult.OK(res);
    }
}
