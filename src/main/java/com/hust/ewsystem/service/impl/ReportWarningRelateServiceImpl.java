package com.hust.ewsystem.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hust.ewsystem.DAO.PO.ReportWarningRelate;
import com.hust.ewsystem.mapper.ReportWarningRelateMapper;
import com.hust.ewsystem.service.ReportWarningRelateService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @BelongsProject: back
 * @BelongsPackage: com.hust.ewsystem.service.impl
 * @Author: xdy
 * @CreateTime: 2025-01-08  10:59
 * @Description:
 * @Version: 1.0
 */
@Service
@Transactional
public class ReportWarningRelateServiceImpl extends ServiceImpl<ReportWarningRelateMapper, ReportWarningRelate> implements ReportWarningRelateService {
}
