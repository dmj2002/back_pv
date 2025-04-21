package com.hust.ewsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hust.ewsystem.DAO.PO.Warnings;
import com.hust.ewsystem.DAO.VO.StandPointVO;
import org.apache.ibatis.annotations.Param;


public interface WarningsMapper extends BaseMapper<Warnings> {
    StandPointVO getStandPointByWarningId(@Param("warningId")Integer warningId);
}
