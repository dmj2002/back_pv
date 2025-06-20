package com.hust.ewsystem.controller;

import com.hust.ewsystem.DAO.DTO.ModelAddDTO;
import com.hust.ewsystem.DAO.DTO.ModelChangeDTO;
import com.hust.ewsystem.DAO.DTO.ThresholdDTO;
import com.hust.ewsystem.common.result.EwsResult;
import com.hust.ewsystem.service.ModelsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/test")
    public EwsResult<?> testModel(@RequestBody Map<String, Object> FileForm){
        return modelsService.testModel(FileForm);
    }

    @PostMapping("/stopPredict")
    public EwsResult<?> stopPredictModel(@RequestBody List<Integer> modelIdList) {
        return modelsService.stopPredictModel(modelIdList);
    }

    @GetMapping("/list")
    public EwsResult<?> listModel(@RequestParam(value = "page") int page,
                                  @RequestParam(value = "pageSize") int pageSize,
                                  @RequestParam(value = "companyId", required = false) Integer companyId,
                                  @RequestParam(value = "pvFarmId", required = false) Integer pvFarmId,
                                  @RequestParam(value = "inverterId", required = false) Integer inverterId,
                                  @RequestParam(value = "combinerBoxId", required = false) Integer combinerBoxId,
                                  @RequestParam(value = "algorithmId", required = false) Integer algorithmId
                                  ){

        return modelsService.listModel(page, pageSize, companyId, pvFarmId, inverterId, combinerBoxId, algorithmId);
    }

    @GetMapping("/showThreshold")
    public EwsResult<?> showThreshold(@RequestParam("modelId") Integer modelId){
        return modelsService.showThreshold(modelId);
    }

    @PostMapping("/changeThreshold")
    public EwsResult<?> changeThreshold(@RequestBody ThresholdDTO thresholdDTO){
        return modelsService.changeThreshold(thresholdDTO);
    }
}
