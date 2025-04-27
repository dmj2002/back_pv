package com.hust.ewsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hust.ewsystem.DAO.DTO.PvFarmDTO;
import com.hust.ewsystem.DAO.DTO.QueryWaitDoneInfoDTO;
import com.hust.ewsystem.DAO.PO.PvFarm;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PvFarmMapper extends BaseMapper<PvFarm> {
    List<PvFarmDTO> getPvFarmsByCompanyId(@Param("param") QueryWaitDoneInfoDTO queryWaitDoneInfoDTO);
}
