package com.hust.ewsystem.controller;

import com.hust.ewsystem.DAO.PO.Company;
import com.hust.ewsystem.common.result.EwsResult;
import com.hust.ewsystem.service.CompanyService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author piiaJet
 * @Create 2025/6/1915:55
 */
@RestController
@RequestMapping("/company")
public class CompanyController {
    @Resource
    private CompanyService companyService;
    @GetMapping("/list")
    public EwsResult<List<Company>> listCompany() {
        List<Company> company = companyService.list();
        return EwsResult.OK(company);
    }
}
