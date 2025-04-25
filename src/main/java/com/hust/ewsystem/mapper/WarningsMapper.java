package com.hust.ewsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hust.ewsystem.DAO.DTO.DeviceDTO;
import com.hust.ewsystem.DAO.PO.Warnings;
import com.hust.ewsystem.DAO.VO.StandPointVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface WarningsMapper extends BaseMapper<Warnings> {
    StandPointVO getStandPointByWarningId(@Param("warningId")Integer warningId);

    List<DeviceDTO> getDeviceByWarningIdList(@Param("records") List<Integer> warningId);
}
