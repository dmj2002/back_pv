package com.hust.ewsystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hust.ewsystem.DAO.PO.Warnings;
import com.hust.ewsystem.common.result.EwsResult;


public interface WarningsService extends IService<Warnings> {

    EwsResult<?> getWarningList(int page, int pageSize, String startDate, String endDate, Integer warningLevel, Integer companyId, Integer pvFarmId, Integer inverterId, Integer combinerBoxId);
}
