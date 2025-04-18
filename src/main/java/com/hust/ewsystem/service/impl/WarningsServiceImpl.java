package com.hust.ewsystem.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hust.ewsystem.DAO.PO.Warnings;
import com.hust.ewsystem.mapper.WarningsMapper;
import com.hust.ewsystem.service.WarningsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
public class WarningsServiceImpl extends ServiceImpl<WarningsMapper, Warnings> implements WarningsService {
    // Implement any additional methods or logic specific to the WarningService here
}
