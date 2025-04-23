package com.hust.ewsystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hust.ewsystem.DAO.DTO.QueryWarnDetailsDTO;
import com.hust.ewsystem.DAO.DTO.TrendDataDTO;
import com.hust.ewsystem.DAO.PO.RealPoint;

import java.util.List;
import java.util.Map;


public interface RealPointService extends IService<RealPoint> {

    List<TrendDataDTO> getRealPointValueList(List<Map<Integer, RealPoint>> relPointAndLableList, QueryWarnDetailsDTO queryWarnDetailsDTO);
}
