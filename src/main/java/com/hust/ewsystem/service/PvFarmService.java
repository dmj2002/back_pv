package com.hust.ewsystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hust.ewsystem.DAO.DTO.PvFarmDTO;
import com.hust.ewsystem.DAO.DTO.QueryWaitDoneInfoDTO;
import com.hust.ewsystem.DAO.PO.PvFarm;

import javax.validation.Valid;
import java.util.List;

public interface PvFarmService extends IService<PvFarm> {
    List<PvFarmDTO> getPvFarmsByCompanyId(@Valid QueryWaitDoneInfoDTO queryWaitDoneInfoDTO);
}
