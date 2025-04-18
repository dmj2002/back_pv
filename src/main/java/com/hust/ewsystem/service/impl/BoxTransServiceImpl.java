package com.hust.ewsystem.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hust.ewsystem.DAO.PO.BoxTrans;
import com.hust.ewsystem.mapper.BoxTransMapper;
import com.hust.ewsystem.service.BoxTransService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
public class BoxTransServiceImpl extends ServiceImpl<BoxTransMapper, BoxTrans> implements BoxTransService {
}
