package com.hust.ewsystem.controller;

import com.hust.ewsystem.DAO.DTO.PvFarmDTO;
import com.hust.ewsystem.DAO.DTO.QueryWaitDoneInfoDTO;
import com.hust.ewsystem.common.result.EwsResult;
import com.hust.ewsystem.service.PvFarmService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping("/waitdone")
@RequiredArgsConstructor
public class WaitDoneController {

    private final PvFarmService pvFarmService;


    @RequestMapping(value = "/getWaitDoneInfo",method = RequestMethod.POST)
    public EwsResult<?> getWarnList(@Valid @RequestBody QueryWaitDoneInfoDTO queryWaitDoneInfoDTO){
        List<PvFarmDTO> windFarmsByCompanyId = pvFarmService.getPvFarmsByCompanyId(queryWaitDoneInfoDTO);
        return EwsResult.OK(windFarmsByCompanyId);
    }
}
