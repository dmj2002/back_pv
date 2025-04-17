package com.hust.ewsystem.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hust.ewsystem.DAO.PO.CombinerBox;
import com.hust.ewsystem.mapper.CombinerBoxMapper;
import com.hust.ewsystem.service.CombinerBoxService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CombinerBoxServiceImpl extends ServiceImpl<CombinerBoxMapper, CombinerBox> implements CombinerBoxService {

}
