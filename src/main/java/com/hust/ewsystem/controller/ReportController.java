package com.hust.ewsystem.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;

import com.hust.ewsystem.DAO.DTO.QueryReportsDTO;
import com.hust.ewsystem.DAO.DTO.ReportDTO;
import com.hust.ewsystem.DAO.PO.ReportWarningRelate;
import com.hust.ewsystem.DAO.PO.Reports;
import com.hust.ewsystem.DAO.PO.Warnings;
import com.hust.ewsystem.DAO.VO.ReportVO;
import com.hust.ewsystem.common.result.EwsResult;

import com.hust.ewsystem.mapper.ReportsMapper;
import com.hust.ewsystem.service.ReportWarningRelateService;
import com.hust.ewsystem.service.ReportsService;
import com.hust.ewsystem.service.WarningsService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/report")
public class ReportController {
    
    @Resource
    private ReportsMapper reportsMapper;

    @Resource
    private ReportsService reportsService;

    @Resource
    private ReportWarningRelateService reportWarningRelateService;

    @Resource
    private WarningsService warningService;


    @PostMapping("/operate")
    public EwsResult<?> operationReport(@RequestBody ReportDTO reportDTO){
        Reports newReport = Reports.builder()
                .reportId(reportDTO.getReportId())
                .status(reportDTO.getReportStatus())
                .build();
        // 仅在 reportText 不为 null 时，才设置该字段
        if (reportDTO.getReportText() != null) {
            newReport.setReportText(reportDTO.getReportText());
        }
        int res = reportsMapper.updateById(newReport);
        // 如果通知状态为已处理，则将对应的预警状态设置为已处理
        if(reportDTO.getReportStatus() == 2){
            List<Integer> warning_ids = reportWarningRelateService.list(new QueryWrapper<ReportWarningRelate>().eq("report_id", reportDTO.getReportId())).stream().map(ReportWarningRelate::getWarningId).collect(Collectors.toList());
            List<Warnings> warnings = warningService.list(new QueryWrapper<Warnings>().in("warning_id", warning_ids));
            warnings.forEach(warning -> warning.setWarningStatus(4));
            warningService.updateBatchById(warnings);
        }
        return res == 1 ? EwsResult.OK("操作成功") : EwsResult.error("操作失败");
    }
    @GetMapping("/deleteReport")
    public EwsResult<?> deleteReport(@RequestParam("reportId") Integer reportId){
        List<Integer> warning_ids = reportWarningRelateService.list(new QueryWrapper<ReportWarningRelate>().eq("report_id", reportId)).stream().map(ReportWarningRelate::getWarningId).collect(Collectors.toList());
        boolean res = warningService.list(new QueryWrapper<Warnings>().in("warning_id", warning_ids)).stream().allMatch(
                warning -> warning.getWarningStatus() == 4 || warning.getWarningStatus() == 3
        );
        if(res){
            Reports report = reportsMapper.selectById(reportId);
            report.setStatus(2);
            reportsMapper.updateById(report);
            return EwsResult.OK("通知办结成功");
        }
        return EwsResult.error("通知办结失败");
    }


    @RequestMapping(value = "/getReportList",method = RequestMethod.POST)
    public EwsResult<IPage<ReportVO>>  getReportList(@Valid @RequestBody QueryReportsDTO queryReportsDTO){
        IPage<ReportVO> reportList = reportsService.getReportList(queryReportsDTO);
        return EwsResult.OK(reportList);
    }
}
