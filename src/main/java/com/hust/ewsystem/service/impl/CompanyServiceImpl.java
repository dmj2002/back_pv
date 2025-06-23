package com.hust.ewsystem.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hust.ewsystem.DAO.PO.Company;
import com.hust.ewsystem.mapper.CompanyMapper;
import com.hust.ewsystem.service.CompanyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Author piiaJet
 * @Create 2025/6/1915:59
 */

@Service
@Transactional
public class CompanyServiceImpl extends ServiceImpl<CompanyMapper, Company> implements CompanyService {
}
