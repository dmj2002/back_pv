package com.hust.ewsystem.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hust.ewsystem.DAO.DTO.QueryWarnDetailsDTO;
import com.hust.ewsystem.DAO.DTO.TrendDataDTO;
import com.hust.ewsystem.DAO.PO.CommonData;
import com.hust.ewsystem.DAO.PO.RealPoint;

import com.hust.ewsystem.common.constant.CommonConstant;
import com.hust.ewsystem.common.util.DateUtil;
import com.hust.ewsystem.mapper.CommonDataMapper;
import com.hust.ewsystem.mapper.RealPointMapper;
import com.hust.ewsystem.service.RealPointService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

import static com.hust.ewsystem.service.impl.ModelsServiceImpl.getTableName;
import static com.hust.ewsystem.service.impl.ModelsServiceImpl.getdivceName;


@Service
@Transactional
public class RealPointServiceImpl extends ServiceImpl<RealPointMapper, RealPoint> implements RealPointService {


    @Resource
    private CommonDataMapper commonDataMapper;

    @Override
    @DS("slave")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<TrendDataDTO> getRealPointValueList(List<Map<Integer, RealPoint>> pointLabels, QueryWarnDetailsDTO queryWarnDetailsDTO, Integer pvFarmId) {
        List<CommonData> valueList = new ArrayList<>();
        List<TrendDataDTO> result = new LinkedList<>();
        String startDate = DateUtil.dateTimeToDateString(queryWarnDetailsDTO.getStartDate(), CommonConstant.DATETIME_FORMAT_1);
        String endDate = DateUtil.dateTimeToDateString(queryWarnDetailsDTO.getEndDate(), CommonConstant.DATETIME_FORMAT_1);
        TrendDataDTO trendDataDTO;
        for (Map<Integer, RealPoint> point : pointLabels) {
            for (Map.Entry<Integer, RealPoint> entry : point.entrySet()) {
                String tableName = getTableName(entry.getValue().getPointType()) + "_" + getdivceName(entry.getValue().getPointType(), Collections.singletonList(entry.getValue()));
                String pointLabel = entry.getValue().getPointLabel().toLowerCase();
                List<String> pointLabelList = Collections.singletonList(pointLabel);
                List<Map<String, Object>> mapList = commonDataMapper.selectDataByTime(tableName, pointLabelList, startDate, endDate);
                for(Map<String, Object> map : mapList) {
                    CommonData commonData = new CommonData();
                    commonData.setDatetime(((Timestamp) map.get("datetime")).toLocalDateTime());
                    commonData.setValue((Double) map.get(pointLabel));
                    valueList.add(commonData);
                }
                trendDataDTO = new TrendDataDTO();
                trendDataDTO.setPointId(entry.getKey());
                trendDataDTO.setPointValue(valueList);
                result.add(trendDataDTO);
            }
        }
        return result;
    }
}
