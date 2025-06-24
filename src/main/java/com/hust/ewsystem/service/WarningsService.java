package com.hust.ewsystem.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hust.ewsystem.DAO.DTO.*;
import com.hust.ewsystem.DAO.PO.Warnings;
import com.hust.ewsystem.DAO.VO.WarningsVO;
import com.hust.ewsystem.common.result.EwsResult;

import javax.validation.Valid;
import java.util.List;


public interface WarningsService extends IService<Warnings> {

    EwsResult<?> getWarningList(int page, int pageSize, String startDate, String endDate, Integer warningLevel, Integer companyId, Integer pvFarmId, Integer inverterId, Integer combinerBoxId);

    EwsResult<?> getWarningNowList(int page, int pageSize, Integer warningLevel, Integer companyId, Integer pvFarmId, Integer inverterId, Integer combinerBoxId);

    EwsResult<?> getWarningTrendData(QueryWarnDetailsDTO queryWarnDetailsDTO);

    EwsResult<?> queryTurbineWarnMatrix(QueryPvWarnMatrixDTO queryPvWarnMatrixDTO);

    List<DeviceDTO> getDeviceByWarningIdList(List<Integer> warningId);

    IPage<WarningsVO> getWarnInfo(QueryWarnDTO queryWarnDTO);

    List<WarningsVO> getWarnDesc(QueryWarnInfoDTO queryWarnInfoDTO);

    List<WarningsVO> getWarnInfo(QueryWarnInfoDTO queryWarnInfoDTO);

    List<Warnings> getWarnInfoListByReportId(List<Integer> warnIdList);
}
