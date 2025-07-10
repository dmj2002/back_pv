package com.hust.ewsystem.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hust.ewsystem.DAO.PO.StandPoint;
import com.hust.ewsystem.common.result.EwsResult;
import com.hust.ewsystem.mapper.StandPointMapper;
import com.hust.ewsystem.service.StandPointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/standpoint")
public class StandPointController {

    @Autowired
    private StandPointMapper standPointMapper;

    @Autowired
    private StandPointService standPointService;



    @PostMapping("/add")
    public EwsResult<?> addRealPoint(@RequestBody StandPoint standPoint) {
        int inserted = standPointMapper.insert(standPoint);
        if(inserted != 0){
            Map<String,Object> result = new HashMap<>();
            result.put("pointId", standPoint.getPointId());
            result.put("pointLabel", standPoint.getPointLabel());
            result.put("pointDescription", standPoint.getPointDescription());
            result.put("pointType", standPoint.getPointType());
            result.put("pointUnit", standPoint.getPointUnit());
            return EwsResult.OK("测点添加成功", result);
        }
        else{
            return EwsResult.error("测点添加失败");
        }
    }
    @GetMapping("/get/{pointId}")
    public EwsResult<?> getRealPoint(@PathVariable Integer pointId){
        StandPoint standPoint = standPointMapper.selectById(pointId);
        return Objects.isNull(standPoint) ? EwsResult.error("未找到该测点") : EwsResult.OK("测点查找成功",standPoint);
    }
    @GetMapping("/list")
    public EwsResult<?> listRealPoint(@RequestParam(value = "page") int page,
                                      @RequestParam(value = "page_size") int pageSize){
        Map<String,Object> response = new HashMap<>();
        Page<StandPoint> modelsPage = new Page<>(page, pageSize);
        QueryWrapper<StandPoint> queryWrapper = new QueryWrapper<>();


        Page<StandPoint> page1 = standPointService.page(modelsPage, queryWrapper);



        response.put("total_count",page1.getTotal());
        response.put("page",page1.getCurrent());
        response.put("page_size",page1.getSize());
        response.put("total_pages",page1.getPages());
        response.put("standPointList",page1.getRecords());
        return EwsResult.OK("查询成功", response);
    }
}
