package com.hust.ewsystem.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hust.ewsystem.DAO.PO.CommonData;
import com.hust.ewsystem.mapper.CommonDataMapper;
import com.hust.ewsystem.service.CommonDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class CommonDataServiceImpl extends ServiceImpl<CommonDataMapper, CommonData> implements CommonDataService {

    @Autowired
    private CommonDataMapper commonDataMapper;

    @Override
    @DS("slave")
    public List<CommonData> selectAllData(String tableName) {
        return commonDataMapper.selectAllData(tableName);
    }

    @Override
    @DS("slave")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<Map<String, Object>> selectDataByTime(String tableName, List<String>columns, String startTime, String endTime) {
        return commonDataMapper.selectDataByTime(tableName,columns,startTime, endTime);
    }
}
