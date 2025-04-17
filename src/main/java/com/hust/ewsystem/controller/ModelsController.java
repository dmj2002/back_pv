package com.hust.ewsystem.controller;

import com.hust.ewsystem.DAO.DTO.ModelAddDTO;
import com.hust.ewsystem.DAO.DTO.ModelChangeDTO;
import com.hust.ewsystem.common.result.EwsResult;
import com.hust.ewsystem.service.CombinerBoxService;
import com.hust.ewsystem.service.InverterService;
import com.hust.ewsystem.service.ModelsService;
import com.hust.ewsystem.service.PvFarmService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/model")
@RequiredArgsConstructor
public class ModelsController {

    private final ModelsService modelsService;

    @PostMapping("/add")
    public EwsResult<?> addModel(@RequestBody ModelAddDTO modelAddDTO) {
        return modelsService.addModel(modelAddDTO);
    }

    @PostMapping("/change")
    public EwsResult<?> changeModel(@RequestBody ModelChangeDTO modelChangeDTO) {
        return modelsService.changeModel(modelChangeDTO);
    }

    @DeleteMapping("/delete")
    public EwsResult<?> deleteModel(@RequestBody List<Integer> modelIdList) {
        return modelsService.deleteModel(modelIdList);
    }

    @PostMapping("/train")
    public EwsResult<?> trainModel(@RequestBody Map<String, Object> FileForm) {
        return modelsService.trainModel(FileForm);
    }

    @PostMapping("/predict")
    public EwsResult<?> predictModel(@RequestBody List<Integer> modelList) {
        return modelsService.predictModel(modelList);
    }

    @PostMapping("/stopPredict")
    public EwsResult<?> stopPredictModel(@RequestBody List<Integer> modelIdList) {
        return modelsService.stopPredictModel(modelIdList);
    }
}
