package com.hust.ewsystem.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hust.ewsystem.DAO.PO.PvFarm;
import com.hust.ewsystem.mapper.PvFarmMapper;
import com.hust.ewsystem.service.PvFarmService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PvFarmServiceImpl extends ServiceImpl<PvFarmMapper, PvFarm> implements PvFarmService {

}
