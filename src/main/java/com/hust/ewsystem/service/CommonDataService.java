package com.hust.ewsystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hust.ewsystem.DAO.PO.CommonData;

import java.util.List;
import java.util.Map;

public interface CommonDataService extends IService<CommonData> {
    List<CommonData> selectAllData(String tableName);

    List<Map<String ,Object>> selectDataByTime(String tableName, List<String>columns, String startTime, String endTime);
}
