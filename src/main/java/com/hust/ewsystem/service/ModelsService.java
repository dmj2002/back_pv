package com.hust.ewsystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hust.ewsystem.DAO.DTO.ModelAddDTO;
import com.hust.ewsystem.DAO.DTO.ModelChangeDTO;
import com.hust.ewsystem.DAO.DTO.ThresholdDTO;
import com.hust.ewsystem.DAO.PO.Models;
import com.hust.ewsystem.common.result.EwsResult;

import java.util.List;
import java.util.Map;

public interface ModelsService extends IService<Models> {
    EwsResult<?> addModel(ModelAddDTO modelAddDTO);

    EwsResult<?> changeModel(ModelChangeDTO modelChangeDTO);

    EwsResult<?> deleteModel(List<Integer> modelIdList);

    EwsResult<?> trainModel(Map<String, Object> FileForm);

    EwsResult<?> predictModel(List<Integer> modelList);

    EwsResult<?> stopPredictModel(List<Integer> modelIdList);

    EwsResult<?> listModel(int page, int pageSize, Integer companyId, Integer pvFarmId, Integer inverterId, Integer combinerBoxId, Integer algorithmId);

    EwsResult<?> showThreshold(Integer modelId);

    EwsResult<?> changeThreshold(ThresholdDTO thresholdDTO);
}
