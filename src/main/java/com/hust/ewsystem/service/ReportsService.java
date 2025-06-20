package com.hust.ewsystem.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hust.ewsystem.DAO.DTO.QueryReportsDTO;
import com.hust.ewsystem.DAO.PO.Reports;
import com.hust.ewsystem.DAO.VO.ReportVO;

import javax.validation.Valid;


public interface ReportsService extends IService<Reports> {

    IPage<ReportVO> getReportList(@Valid QueryReportsDTO queryReportsDTO);
}
