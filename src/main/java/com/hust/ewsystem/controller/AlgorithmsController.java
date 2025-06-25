package com.hust.ewsystem.controller;


import com.hust.ewsystem.DAO.DTO.AlgForm;
import com.hust.ewsystem.DAO.PO.Algorithms;
import com.hust.ewsystem.common.exception.FileException;
import com.hust.ewsystem.common.result.EwsResult;
import com.hust.ewsystem.service.AlgorithmsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/algorithms")
@RequiredArgsConstructor
public class AlgorithmsController {

    private final AlgorithmsService algorithmsService;

    @Value("${algorithm.pythonFilePath}")
    public String pythonFilePath;

    @PostMapping("/add")
    @Transactional
    public EwsResult<?> addAlgorithm(@ModelAttribute @Validated AlgForm algForm) {
        Algorithms algorithms = algForm.getAlgorithms();
        MultipartFile trainFile = algForm.getTrain();
        MultipartFile predictFile = algForm.getPredict();
        // 额外检查文件是否为空
        if (trainFile.isEmpty()) {
            throw new FileException("训练文件不能为空");
        }
        if (predictFile.isEmpty()) {
            throw new FileException("预测文件不能为空");
        }
        boolean save = algorithmsService.save(algorithms);
        if(!save) {
            return EwsResult.error("新建算法失败");
        }
        Integer algorithmId = algorithms.getAlgorithmId();
        algorithms.setAlgorithmLabel("A" + String.format("%04d", algorithmId));
        boolean update = algorithmsService.updateById(algorithms);
        if(!update) {
            return EwsResult.error("新建算法失败");
        }
        // 定义文件保存路径
        String algorithmDir = pythonFilePath + "/alg/A" + String.format("%04d", algorithmId);
        File dir = new File(algorithmDir);
        if (!dir.exists()) {
             // 创建目录
            if (!dir.mkdirs()) {
                throw new FileException("创建文件目录失败");
            }
        }
        // 保存文件
        try {
            trainFile.transferTo(new File(dir, "train.py"));
            predictFile.transferTo(new File(dir, "predict.py"));
        } catch (IOException e) {
            throw new FileException("文件保存失败: " + e.getMessage());
        }
        Map<String, Object> result = new HashMap<>();
        result.put("algorithmId", algorithmId);
        return EwsResult.OK("新建算法成功", result);
    }
    @DeleteMapping("/delete/{algorithmId}")
    public EwsResult<?> deleteAlgoithm(@PathVariable Integer algorithmId) {
        boolean remove = algorithmsService.removeById(algorithmId);
        if(!remove) {
            return EwsResult.error("删除算法失败");
        }
        String algorithmDir = pythonFilePath + "/alg/A" + String.format("%04d", algorithmId);
        File dir = new File(algorithmDir);
        if (dir.exists()) {
            // 删除目录
            if (!dir.delete()) {
                throw new FileException("删除文件目录失败");
            }
            return EwsResult.OK("删除算法成功");
        }
        else return EwsResult.error("删除算法失败");
    }
    @PutMapping("/update")
    public EwsResult<?> updateAlgorithm(@RequestBody @Validated Algorithms algorithms) {
        boolean update = algorithmsService.updateById(algorithms);
        if(update) {
            return EwsResult.OK("更新算法成功");
        }
        return EwsResult.error("更新算法失败");
    }
    @GetMapping("/get/{algorithmId}")
    public EwsResult<?> getAlgorithm(@PathVariable Integer algorithmId) {
        Algorithms algorithm = algorithmsService.getById(algorithmId);
        return Objects.isNull(algorithm) ? EwsResult.error("未找到该算法") : EwsResult.OK(algorithm);
    }
    @GetMapping("/list")
    public EwsResult<List<Algorithms>> listAlgorithm() {
        List<Algorithms> algorithm = algorithmsService.list();
        return EwsResult.OK(algorithm);
    }
}
