package com.hust.ewsystem.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hust.ewsystem.DAO.PO.Algorithms;
import com.hust.ewsystem.mapper.AlgorithmsMapper;
import com.hust.ewsystem.service.AlgorithmsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AlgorithmsServiceImpl extends ServiceImpl<AlgorithmsMapper, Algorithms> implements AlgorithmsService {

}
