package com.hust.ewsystem.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hust.ewsystem.DAO.PO.StandPoint;
import com.hust.ewsystem.mapper.StandPointMapper;
import com.hust.ewsystem.service.StandPointService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class StandPointServiceImpl extends ServiceImpl<StandPointMapper, StandPoint> implements StandPointService {
}
