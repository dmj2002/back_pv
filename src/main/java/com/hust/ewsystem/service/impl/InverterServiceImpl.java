package com.hust.ewsystem.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hust.ewsystem.DAO.PO.Inverter;
import com.hust.ewsystem.mapper.InverterMapper;
import com.hust.ewsystem.service.InverterService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class InverterServiceImpl extends ServiceImpl<InverterMapper, Inverter> implements InverterService {

}
