package com.hust.ewsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hust.ewsystem.DAO.DTO.*;
import com.hust.ewsystem.DAO.PO.Warnings;
import com.hust.ewsystem.DAO.VO.DeviceVO;
import com.hust.ewsystem.DAO.VO.StandPointVO;
import com.hust.ewsystem.DAO.VO.WarningsVO;
import org.apache.ibatis.annotations.Param;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;


public interface WarningsMapper extends BaseMapper<Warnings> {
    StandPointVO getStandPointByWarningId(@Param("warningId")Integer warningId);

    DeviceVO getDeviceInfoByWarningId(@Param("warningId")Integer warningId);

    List<DeviceDTO> getDeviceByWarningIdList(@Param("records") List<Integer> warningId);

    int getWarningsCount(@Param("param") GetWarningsCountDTO getWarningsCountDTO);

    WarnStatusDTO getCount(@Param("modelId") Integer modelId, @Param("startTime")LocalDateTime startTime,@Param("endTime")LocalDateTime endTime);

    IPage<WarningsVO> selectWarningsPage(@Param("param") QueryWarnDTO queryWarnDTO, @Param("page") Page<Warnings> page);

    List<WarningsVO> selectWarningsDesc(@Param("param") QueryWarnInfoDTO queryWarnInfoDTO);

    List<WarningsVO> selectWarningsNoPage(@Param("param") QueryWarnInfoDTO queryWarnInfoDTO);
}
