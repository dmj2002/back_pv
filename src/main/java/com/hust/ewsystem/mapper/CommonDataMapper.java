package com.hust.ewsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hust.ewsystem.DAO.PO.CommonData;
import org.apache.ibatis.annotations.Param;
import org.springframework.scheduling.support.SimpleTriggerContext;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface CommonDataMapper extends BaseMapper<CommonData> {

    List<CommonData> selectAllData(@Param("tableName")String tableName);

    List<Map<String ,Object>>  selectDataByTime(@Param("tableName")String tableName, @Param("columns") List<String>columns, @Param("startTime") String startTime, @Param("endTime")String endTime);
}
