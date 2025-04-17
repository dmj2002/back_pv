package com.hust.ewsystem.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hust.ewsystem.DAO.PO.ModelRealRelate;
import com.hust.ewsystem.mapper.ModelRealRelateMapper;
import com.hust.ewsystem.service.ModelRealRelateService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ModelRealRelateServiceImpl extends ServiceImpl<ModelRealRelateMapper,ModelRealRelate> implements ModelRealRelateService{

}
