package com.hust.ewsystem.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hust.ewsystem.DAO.PO.StandRealRelate;
import com.hust.ewsystem.mapper.StandRealRelateMapper;
import com.hust.ewsystem.service.StandRealRelateService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @BelongsProject: back
 * @BelongsPackage: com.hust.ewsystem.service.impl
 * @Author: xdy
 * @CreateTime: 2024-12-19  17:31
 * @Description:
 * @Version: 1.0
 */
@Service
@Transactional
public class StandRealRelateServiceImpl extends ServiceImpl<StandRealRelateMapper, StandRealRelate> implements StandRealRelateService {
}
