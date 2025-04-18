package com.hust.ewsystem.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hust.ewsystem.DAO.DTO.ModelAddDTO;
import com.hust.ewsystem.DAO.DTO.ModelChangeDTO;
import com.hust.ewsystem.DAO.DTO.RealToStandMapping;
import com.hust.ewsystem.DAO.DTO.ThresholdDTO;
import com.hust.ewsystem.DAO.PO.*;
import com.hust.ewsystem.DAO.VO.ThresholdVO;
import com.hust.ewsystem.common.exception.CrudException;
import com.hust.ewsystem.common.exception.FileException;
import com.hust.ewsystem.common.result.EwsResult;
import com.hust.ewsystem.mapper.*;
import com.hust.ewsystem.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.exceptions.TooManyResultsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ModelsServiceImpl extends ServiceImpl<ModelsMapper, Models> implements ModelsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModelsServiceImpl.class);

    @Value("${algorithm.pythonFilePath}")
    public String pythonFilePath;

    private final AlgorithmsService algorithmsService;

    private final StandPointMapper standPointMapper;

    private final StandRealRelateMapper standRealRelateMapper;

    private final RealPointMapper realPointMapper;

    private final ModelRealRelateService modelRealRelateService;

    private final CommonDataService commonDataService;

    private final TasksMapper tasksMapper;

    private final WarningsService warningService;

    // 任务状态
    private final Map<String, ScheduledFuture<?>> taskMap = new ConcurrentHashMap<>();
    // 线程池
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(128);

    @Override
    public EwsResult<?> addModel(ModelAddDTO modelAddDTO) {
        Integer algorithmType = algorithmsService.getById(modelAddDTO.getAlgorithmId()).getAlgorithmType();
        List<Models> modelsList = new ArrayList<>();
        for(Integer deviceId : modelAddDTO.getDeviceList()){
            //先写传入的模型参数
            Models newModel = new Models();
            newModel.setModelName(modelAddDTO.getModelName() + "_" + deviceId)
                    .setAlgorithmId(modelAddDTO.getAlgorithmId())
                    .setModelParameters(modelAddDTO.getParams())
                    .setPatternId(modelAddDTO.getPatternId())
                    .setAlertInterval(modelAddDTO.getAlertInterval() != null ? modelAddDTO.getAlertInterval() : 10)
                    .setAlertWindowSize(modelAddDTO.getAlertWindowSize() != null ? modelAddDTO.getAlertWindowSize() : 60);
            //后端自己生成的模型参数
            newModel.setDeviceId(deviceId)
                    .setModelType(algorithmType)
                    .setModelVersion("V1.0")
                    .setModelStatus(0);
            modelsList.add(newModel);
        }
        boolean saveBatch1 = saveBatch(modelsList);
        if(!saveBatch1){
            throw new CrudException("模型批量保存失败");
        }
        for (Models models : modelsList) {
            //插入后才有modelId
            String modelLabel = "M" + String.format("%04d", models.getModelId());
            models.setModelLabel(modelLabel);
            File modelDir = new File(String.format("%s/%s", pythonFilePath, modelLabel));
            if (!modelDir.exists()) {
                if (!modelDir.mkdirs()) {
                    throw new FileException("创建文件目录失败");
                }
            }
        }
        boolean updateBatch = updateBatchById(modelsList);
        if(!updateBatch){
            throw new CrudException("模型批量保存失败");
        }
        List<String> standpointList = modelAddDTO.getPointList();
        Map<String, List<Integer>> standToRealIdMap = standToRealId(standpointList);
        List<ModelRealRelate> modelRealRelateList = new ArrayList<>();
        for (String standPoint : standpointList) {
            // 获取真实测点 ID 列表
            List<Integer> realPointIds = standToRealIdMap.getOrDefault(standPoint, new ArrayList<>());
            // 遍历模型列表
            for (Models models : modelsList) {
                // 遍历每个真实测点 ID
                Integer uniqueRealId = findUniqueRealId(realPointIds, models.getDeviceId(), models.getModelType());
                if (uniqueRealId == null) {
                    continue; // 如果找不到唯一的真实 ID，跳过
                }
                // 创建关联对象并添加到列表
                ModelRealRelate modelRealRelate = new ModelRealRelate();
                modelRealRelate.setModelId(models.getModelId())
                        .setRealPointId(uniqueRealId);
                modelRealRelateList.add(modelRealRelate);
            }
        }
        boolean saveBatch2 = modelRealRelateService.saveBatch(modelRealRelateList);
        if(!saveBatch2){
            throw new CrudException("模型关联批量保存失败");
        }
        List<Map<String,Object>> result = new ArrayList<>();
        for (Models models : modelsList) {
            Map<String,Object> map = new HashMap<>();
            map.put("modelId",models.getModelId());
            map.put("modellabel",models.getModelLabel());
            map.put("modelName",models.getModelName());
            map.put("modelVersion",models.getModelVersion());
            map.put("modelStatus",models.getModelStatus());
            map.put("modelType",models.getModelType());
            map.put("deviceId",models.getDeviceId());
            result.add(map);
        }
        return EwsResult.OK("新建模型成功",result);
    }

    @Override
    public EwsResult<?> changeModel(ModelChangeDTO modelChangeDTO) {
        List<Models> modelsList = new ArrayList<>();
        modelChangeDTO.getModelIds().forEach(modelId -> {
            Models model = getById(modelId);
            if(modelChangeDTO.getModelName() != null){
                String nameSuffix = model.getModelName().split("_")[1];
                model.setModelName(modelChangeDTO.getModelName() + "_" + nameSuffix);
            }
            if(modelChangeDTO.getAlertWindowSize() != null){
                model.setAlertWindowSize(modelChangeDTO.getAlertWindowSize());
            }
            if(modelChangeDTO.getAlertInterval() != null){
                model.setAlertInterval(modelChangeDTO.getAlertInterval());
            }
            if(modelChangeDTO.getParams().isNull()){
                model.setModelParameters(modelChangeDTO.getParams());
            }
            String[] versionParts = model.getModelVersion().split("\\.");
            int majorVersion = Integer.parseInt(versionParts[0].substring(1)); //去掉v
            int minorVersion = Integer.parseInt(versionParts[1]);
            if (minorVersion < 9) {
                minorVersion++;
            } else {
                majorVersion += 1;
                minorVersion = 0;
            }
            String newVersion = "V" + majorVersion + "." + minorVersion;
            //像版本号这种
            model.setModelVersion(newVersion);
            modelsList.add(model);
        });
        updateBatchById(modelsList);
        return EwsResult.OK("修改模型成功");
    }

    @Override
    public EwsResult<?> deleteModel(List<Integer> modelIdList) {
        //删除model_real_relate表
        boolean remove1 = modelRealRelateService.remove(
                new QueryWrapper<ModelRealRelate>().in("model_id", modelIdList)
        );
        if(!remove1){
            throw new CrudException("删除模型失败");
        }
        modelIdList.forEach(modelId -> {
            //删除模型文件夹
            String modelLabel = "M" + String.format("%04d", modelId);
            File modelDir = new File(String.format("%s/%s", pythonFilePath, modelLabel));
            if (modelDir.exists()) {
                deleteDirectory(modelDir);
            }
        });
        //删除model表
        boolean remove2 = removeByIds(modelIdList);
        if(!remove2){
            throw new CrudException("删除模型失败");
        }
        return EwsResult.OK("删除模型成功");
    }

    @Override
    public EwsResult<?> trainModel(Map<String, Object> FileForm) {
        List<Integer> modelIds =(List<Integer>) FileForm.get("modelIds");
        List<Map<String, Object>> timePeriods = (List<Map<String, Object>>)FileForm.get("timePeriods");
        //返回值
        List<Map<String,Object>> taskIdList = new ArrayList<>();
        //传入的每个模型测点不一定相同，所以需要分别处理
        for(Integer modelId : modelIds){
            //修改模型状态为训练中
            updateById(new Models().setModelStatus(1).setModelId(modelId));
            Models model = getById(modelId);
            String modelLabel = model.getModelLabel();
            Integer deviceId = model.getDeviceId();
            List<Integer> realpointId = modelRealRelateService.list(
                    new QueryWrapper<ModelRealRelate>().eq("model_id", modelId)
            ).stream().map(ModelRealRelate::getRealPointId).collect(Collectors.toList());
            //标准测点标签 -> 真实测点
            RealToStandMapping realToStandMapping = RealToStandLabel(realpointId);
            Map<Integer, List<String>> columnMapping = new HashMap<>();
            for(RealPoint realPoint: realToStandMapping.getRealToStandPointMap().values()){
                Integer type = realPoint.getPointType();
                columnMapping.putIfAbsent(type, new ArrayList<>());
                columnMapping.get(type).add(realPoint.getPointLabel());
            }
            //真实测点标签到标准测点标签
            Map<String, String> realLabelToStandLabelMap = realToStandMapping.getRealLabelToStandLabelMap();
            // 查询数据并提取 datetime和value列
            Map<LocalDateTime, Map<String, Object>> alignedData = new TreeMap<>();
            for (Map<String, Object> period : timePeriods) {
                String startTime = (String) period.get("startTime");
                String endTime = (String) period.get("endTime");
                for(Map.Entry<Integer, List<String>> entry : columnMapping.entrySet()){
                    Integer type = entry.getKey();
                    List<String> pointLabels = entry.getValue();
                    String tableName = getTableName(type) + "_" + deviceId;
                    List<Map<String ,Object>> data = commonDataService.selectDataByTime(tableName, pointLabels, startTime, endTime);
                    for (Map<String ,Object> record : data) {
                        LocalDateTime datetime = (LocalDateTime) record.get("datetime");
                        for(Map.Entry<String, Object> recordEntry : record.entrySet()){
                            String pointLabel = recordEntry.getKey();
                            if(pointLabel.equals("datetime"))continue;
                            // 真实测点标签 -> 标准测点标签
                            String standPointLabel = realLabelToStandLabelMap.get(pointLabel);
                            Double value = (Double)recordEntry.getValue();
                            // 将数据存储到 alignedData 中
                            alignedData.computeIfAbsent(datetime, k -> new HashMap<>()).put(standPointLabel, value);
                        }
                    }
                }
            }
            // 写入CSV文件
            toTrainCsv(alignedData, realLabelToStandLabelMap, modelLabel);
            Integer algorithmId = getById(modelId).getAlgorithmId();
            String algorithmLabel = algorithmsService.getById(algorithmId).getAlgorithmLabel();
            // 算法调用
            String taskId = train(algorithmLabel, modelLabel,modelId);

            Map<String,Object> map= new HashMap<>();
            map.put("modelId",modelId);
            map.put("taskId",taskId);
            taskIdList.add(map);
        }
        return EwsResult.OK(taskIdList);
    }

    @Override
    public EwsResult<?> predictModel(List<Integer> modelList) {
        for(Integer modelId : modelList) {
            //获取返回值
            Models model = getById(modelId);
            Integer deviceId = model.getDeviceId();
            Integer alertInterval = model.getAlertInterval();
            String modelLabel = model.getModelLabel();
            Integer algorithmId = model.getAlgorithmId();
            Integer alertWindowSize = model.getAlertWindowSize();
            String algorithmLabel = algorithmsService.getById(algorithmId).getAlgorithmLabel();
            //算法调用
            predict(alertInterval, modelLabel, algorithmLabel, modelId,alertWindowSize,deviceId);
            //修改模型状态为预测中
            UpdateWrapper<Models> modelsUpdateWrapper = new UpdateWrapper<>();
            modelsUpdateWrapper.eq("model_id", modelId).set("model_status", 3);
            update(modelsUpdateWrapper);
        }
        return EwsResult.OK("模型开始预测");
    }

    @Override
    public EwsResult<?> stopPredictModel(List<Integer> modelIdList) {
        List<Map<String, Object>> resultList = new ArrayList<>();
        for(Integer modelId : modelIdList){
            Map<String, Object> result = new HashMap<>();
            Models model = getById(modelId);
            //修改模型状态为已完成
            model.setModelStatus(2);
            updateById(model);
            String str;
            ScheduledFuture<?> scheduledTask = taskMap.get(model.getModelLabel() + "_predict");
            if(scheduledTask == null){
                str = "任务不存在";
            }
            else{
                boolean isCancelled = scheduledTask.cancel(true);
                taskMap.remove(model.getModelLabel() + "_predict");
                if (isCancelled) {
                    taskMap.remove(model.getModelLabel() + "_predict");
                    str =  "任务已终止";
                } else {
                    str = "任务终止失败";
                }
            }
            result.put("modelId", modelId);
            result.put("result", str);
            resultList.add(result);
        }
        return EwsResult.OK(resultList);
    }

    @Override
    public EwsResult<?> listModel(int page, int pageSize, Integer companyId, Integer pvFarmId, Integer inverterId, Integer combinerBoxId, Integer algorithmId) {
        return null;
    }

    @Override
    public EwsResult<?> showThreshold(Integer modelId) {
        String modelLabel = getById(modelId).getModelLabel();
        List<ThresholdVO> res = new ArrayList<>();
        try {
            String resultFilePath = pythonFilePath + "/" + modelLabel + "/model.json";
            // 强制使用 UTF-8 编码读取文件内容
            Path path = Paths.get(resultFilePath);
            if (Files.exists(path)) {
                StringBuilder contentBuilder = new StringBuilder();
                try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        contentBuilder.append(line);
                    }
                }
                String content = contentBuilder.toString();
                // 预处理：将 NaN 替换为 null
                content = content.replace("NaN","null");
                // 解析 JSON 内容
                res = JSON.parseObject(content, new TypeReference<List<ThresholdVO>>() {});
                return EwsResult.OK("获取阈值成功", res);
            }
            else{
                System.out.println("文件不存在: " + resultFilePath);
                return EwsResult.OK("文件不存在");
            }
        } catch (Exception e) {
            LOGGER.error("打开文件异常",e);
        }
        return EwsResult.OK("获取阈值失败");
    }

    @Override
    public EwsResult<?> changeThreshold(ThresholdDTO thresholdDTO) {
        Integer modelId = thresholdDTO.getModelId();
        String modelLabel = getById(modelId).getModelLabel();
        List<ThresholdVO> items = thresholdDTO.getItems();
        File resultFile = new File(pythonFilePath + modelLabel + "/model.json");
        // 尝试创建文件或覆盖已有文件
        try {
            if (resultFile.exists()) {
                boolean deleted = resultFile.delete();
                if (!deleted) {
                    return EwsResult.error("修改阈值失败"); // 如果无法删除文件，结束方法
                }
            }
            // 创建新文件
            boolean fileCreated = resultFile.createNewFile();
            if (fileCreated) {
                System.out.println("文件创建成功: " + resultFile.getAbsolutePath());
                //写入结果到文件
                writeFileContent(resultFile.getAbsoluteFile(), items);
            } else {
                return EwsResult.error("修改阈值失败");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return EwsResult.OK("修改阈值成功");
    }

    private void predict(Integer alertInterval, String modelLabel, String algorithmLabel, Integer modelId, Integer alertWindowSize,Integer deviceId) {
        Runnable task = () ->{
            try {
                prePredict(modelId,modelLabel,algorithmLabel,alertWindowSize,deviceId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        // 定期调度任务
        ScheduledFuture<?> scheduledTask =scheduler.scheduleWithFixedDelay(task, 0, alertInterval, TimeUnit.SECONDS);
        taskMap.put(modelLabel + "_predict", scheduledTask);
    }

    private void prePredict(Integer modelId, String modelLabel, String algorithmLabel, Integer alertWindowSize,Integer deviceId) {
        try {
            String taskLabel = UUID.randomUUID().toString();
            Tasks newtask = new Tasks();
            newtask.setModelId(modelId)
                    .setTaskType(1)
                    .setTaskLabel(taskLabel)
                    .setStartTime(LocalDateTime.now());
            tasksMapper.insert(newtask);
            Integer taskId= newtask.getTaskId();
            File taskDir = new File(pythonFilePath + "/task_logs/" + taskLabel);
            if (!taskDir.exists()) {
                if (!taskDir.mkdirs()) {
                    throw new FileException("创建任务目录失败");
                }
            }
            List<Integer> realpointId = modelRealRelateService.list(
                    new QueryWrapper<ModelRealRelate>().eq("model_id", modelId)
            ).stream().map(ModelRealRelate::getRealPointId).collect(Collectors.toList());
            RealToStandMapping realToStandMapping = RealToStandLabel(realpointId);
            Map<Integer, List<String>> columnMapping = new HashMap<>();
            for(RealPoint realPoint: realToStandMapping.getRealToStandPointMap().values()){
                Integer type = realPoint.getPointType();
                columnMapping.putIfAbsent(type, new ArrayList<>());
                columnMapping.get(type).add(realPoint.getPointLabel());
            }
            //真实测点标签到标准测点标签
            Map<String, String> realLabelToStandLabelMap = realToStandMapping.getRealLabelToStandLabelMap();
            Map<LocalDateTime, Map<String, Object>> alignedData = new TreeMap<>();
            // 获取当前时间
            LocalDateTime now = LocalDateTime.now();
            // 计算结束时间 (当前时间 - 10 分钟)
            LocalDateTime endTime = now.minusMinutes(10);
            // 计算开始时间 (当前时间 - 10 分钟 - window 秒)
            LocalDateTime startTime = now.minusMinutes(10).minusSeconds(alertWindowSize);
            // 定义时间格式
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            // 将 LocalDateTime 转换为 String 格式
            String startTimeStr = startTime.format(formatter);
            String endTimeStr = endTime.format(formatter);
            for(Map.Entry<Integer, List<String>> entry : columnMapping.entrySet()){
                Integer type = entry.getKey();
                List<String> pointLabels = entry.getValue();
                String tableName = getTableName(type) + "_" + deviceId;
                List<Map<String ,Object>> data = commonDataService.selectDataByTime(tableName, pointLabels, startTimeStr, endTimeStr);
                for (Map<String ,Object> record : data) {
                    LocalDateTime datetime = (LocalDateTime) record.get("datetime");
                    for(Map.Entry<String, Object> recordEntry : record.entrySet()){
                        String pointLabel = recordEntry.getKey();
                        if(pointLabel.equals("datetime"))continue;
                        // 真实测点标签 -> 标准测点标签
                        String standPointLabel = realLabelToStandLabelMap.get(pointLabel);
                        Double value = (Double)recordEntry.getValue();
                        // 将数据存储到 alignedData 中
                        alignedData.computeIfAbsent(datetime, k -> new HashMap<>()).put(standPointLabel, value);
                    }
                }
            }
            toPredictCsv(alignedData, realLabelToStandLabelMap, taskLabel);
            //准备setting.json
            File settingFile = new File(taskDir, "setting.json");
            JSONObject settings = new JSONObject();
            settings.put("modelPath", pythonFilePath + "/" + modelLabel);
            settings.put("trainDataPath", pythonFilePath + "/" + modelLabel + "/train.csv");
            settings.put("predictDataPath", pythonFilePath + "/task_logs/" + taskLabel + "/predict.csv");
            settings.put("resultDataPath", pythonFilePath + "/task_logs/" + taskLabel + "/result.json");
            settings.put("logPath", pythonFilePath + "/task_logs/" + taskLabel + "/" + taskLabel + ".log");
            // 写入 setting.json 文件
            try (FileWriter fileWriter = new FileWriter(settingFile)) {
                fileWriter.write(settings.toJSONString());
            } catch (IOException e) {
                throw new FileException("setting.json文件配置失败",e);
            }
            executePredict(pythonFilePath, algorithmLabel, taskLabel, modelId, taskId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void executePredict(String filepath, String algorithmLabel, String taskLabel,Integer modelId,Integer taskId) {
        Process process = null;
        boolean interrupted = false;
        try {
            // 准备命令
            List<String> command = new ArrayList<>();
            command.add("python");
            command.add(String.format("alg/%s/predict.py", algorithmLabel));
            command.add(String.format("task_logs/%s/setting.json", taskLabel));
            // 执行命令
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.directory(new File(filepath));
            processBuilder.command(command);
            processBuilder.redirectErrorStream(true);
            process = processBuilder.start();
            System.out.println("Started Python process for model: " +modelId+ " and task: " + taskLabel);
            StringBuilder outputString = null;
            //获取输入流
            InputStream inputStream = process.getInputStream();
            //转成字符输入流
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);

            int len = -1;
            char[] c = new char[2048];
            outputString = new StringBuilder();
            //读取进程输入流中的内容
            while ((len = inputStreamReader.read(c)) != -1) {
                String s = new String(c, 0, len);
                outputString.append(s);
            }
            LOGGER.debug("算法执行结果：{}", outputString);
            inputStream.close();
            inputStreamReader.close();
            // 等待进程完成
            process.waitFor();
            int exitValue = process.exitValue();
            if (exitValue == 0) {
                System.out.println("进程正常结束");
            } else {
                System.out.println("进程异常结束");
            }
        } catch (InterruptedException e) {
            interrupted = true;  // 记录中断状态
            process.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (process != null) {
                process.destroy();
            }
            if (!interrupted) {
                readAndSaveResults(filepath, taskLabel, modelId, taskId);
                LOGGER.info("Finished reading and saving results for task: " + taskLabel);
            }
        }
    }

    private void readAndSaveResults(String filepath, String taskLabel,Integer modelId,Integer taskId) {
        try {
            String resultFilePath = filepath + "/task_logs/" + taskLabel + "/result.json";

            // 强制使用 UTF-8 编码读取文件内容
            Path path = Paths.get(resultFilePath);              // 2025.1.21  判断文件是否存在
            if (Files.exists(path)) {
                StringBuilder contentBuilder = new StringBuilder();
                try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        contentBuilder.append(line);
                    }
                }
                String content = contentBuilder.toString();

                // 解析 JSON 内容
                JSONObject jsonObject = JSONObject.parseObject(content);
                JSONArray alertList = jsonObject.getJSONArray("alarm_list");
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

                List<JSONObject> alertJsonList = new ArrayList<>();
                for (int i = 0; i < alertList.size(); i++) {
                    alertJsonList.add(alertList.getJSONObject(i));
                }

                // 预警信息入库及合并
                processAlerts(alertJsonList, modelId, taskId, formatter);
            }
            else{
                System.out.println("文件不存在: " + resultFilePath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processAlerts(List<JSONObject> alertList, int modelId, int taskId,DateTimeFormatter formatter) {
        Iterator<JSONObject> iterator = alertList.iterator();
        while (iterator.hasNext()) {
            JSONObject alert = iterator.next();
            String alertInfo = alert.getString("alarm_info");
            if(alertInfo.contains("正常")){
                continue;
            }
            LocalDateTime startTime = LocalDateTime.parse(alert.getString("start_time"), formatter);
            LocalDateTime endTime = LocalDateTime.parse(alert.getString("end_time"), formatter);
            String warningLevelStr = alert.getString("warning_level");
            Integer warningLevel;
            if (warningLevelStr != null && !warningLevelStr.isEmpty()) {
                warningLevel = Integer.parseInt(warningLevelStr);  // 转换为整数
            } else {
                warningLevel = 0;  // 如果为空或 null，则返回默认值 0
            }
            // 保存到数据库
            Warnings warning = new Warnings();
            warning.setModelId(modelId);
            warning.setWarningDescription(alertInfo);
            warning.setStartTime(startTime);
            warning.setEndTime(endTime);
            warning.setTaskId(taskId);
            warning.setWarningStatus(0);
            warning.setWarningLevel(warningLevel);

            LambdaQueryWrapper<Warnings> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Warnings::getModelId,modelId)
                    .eq(Warnings::getWarningLevel,warningLevel)
                    .eq(Warnings::getWarningDescription,alertInfo)
                    .ge(Warnings::getEndTime,startTime)
                    .le(Warnings::getStartTime,startTime);
            try {
                Warnings one = warningService.getOne(queryWrapper);
                if(one == null) warningService.save(warning);
                else{
                    one.setEndTime(endTime);
                    one.setWarningStatus(0);
                    warningService.updateById(one);
                }
            } catch (TooManyResultsException e) {
                LOGGER.error("Too many results returned: ", e);
            }
        }
    }

    private String train(String algorithmLabel, String modelLabel, Integer modelId) {
        String taskLabel = UUID.randomUUID().toString();
        Tasks newtask = new Tasks();
        newtask.setModelId(modelId)
                .setTaskType(0)
                .setTaskLabel(taskLabel)
                .setStartTime(LocalDateTime.now());
        tasksMapper.insert(newtask);
        File taskDir = new File(pythonFilePath + "/task_logs/" + taskLabel);
        if (!taskDir.exists()) {
            if (!taskDir.mkdirs()) {
                throw new FileException("创建任务目录失败");
            }
        }
        //准备setting.json
        File settingFile = new File(taskDir, "setting.json");
        JSONObject settings = new JSONObject();
        settings.put("modelPath", pythonFilePath + "/" + modelLabel);
        settings.put("trainDataPath", pythonFilePath + "/" + modelLabel + "/train.csv");
        settings.put("predictDataPath", pythonFilePath + "/task_logs/" + taskLabel + "/predict.csv");
        settings.put("resultDataPath", pythonFilePath + "/task_logs/" + taskLabel + "/result.json");
        settings.put("logPath", pythonFilePath + "/task_logs/" + taskLabel + "/" + taskLabel + ".log");
        // 写入 setting.json 文件
        try (FileWriter fileWriter = new FileWriter(settingFile)) {
            fileWriter.write(settings.toJSONString());
        } catch (IOException e) {
            throw new FileException("setting.json文件配置失败",e);
        }
        Runnable task = () -> executeTrain(pythonFilePath, algorithmLabel, taskLabel, modelId);
        // 调度任务一次性执行
        ScheduledFuture<?> scheduledTask = scheduler.schedule(task, 0, TimeUnit.SECONDS);
        taskMap.put(modelLabel + "_train", scheduledTask);
        return taskLabel;
    }

    public void executeTrain(String filepath, String algorithmLabel, String taskLabel, Integer modelId) {
        Process process = null;
        try {
            // 准备命令
            List<String> command = new ArrayList<>();
            command.add("python3");
            command.add(String.format("alg/%s/train.py", algorithmLabel));
            command.add(String.format("task_logs/%s/setting.json", taskLabel));
            // 执行命令
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.directory(new File(filepath));
            processBuilder.command(command);
            processBuilder.redirectErrorStream(true);
            process = processBuilder.start();
            System.out.println("Started Python process for task: " + taskLabel);
            // 等待进程完成
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.out.println("Python process failed with exit code: " + exitCode);
                //修改模型状态为训练失败
                UpdateWrapper<Models> modelsUpdateWrapper = new UpdateWrapper<>();
                modelsUpdateWrapper.eq("model_id", modelId).set("model_status", 4);
                update(modelsUpdateWrapper);
            } else {
                System.out.println("Python process completed successfully for task: " + taskLabel);
                //修改模型状态为训练成功
                UpdateWrapper<Models> modelsUpdateWrapper = new UpdateWrapper<>();
                modelsUpdateWrapper.eq("model_id", modelId).set("model_status", 2);
                update(modelsUpdateWrapper);
            }
        } catch(InterruptedException e) {
            process.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Integer findUniqueRealId(List<Integer> realPointIds, Integer deviceId, Integer modelType) {
        return realPointMapper.selectOne(new QueryWrapper<RealPoint>()
                .in("point_id", realPointIds) // 在 realPointIds 中查找
                .eq("device_id", deviceId) // 设备 ID 匹配
                .eq("point_type", modelType) // 测点类型匹配
        ).getPointId();
    }

    //标准测点标签 -> 真实测点IDs
    public Map<String, List<Integer>> standToRealId(List<String> standpointList) {
        // 修改返回值类型为 Map<String, List<Integer>>
        Map<String, List<Integer>> standToRealPointMap = new HashMap<>();

        // 标准测点标签 -> 标准测点ID
        Map<String, Integer> standPointMap = standPointMapper.selectList(
                new QueryWrapper<StandPoint>().in("point_label", standpointList)
        ).stream().collect(Collectors.toMap(StandPoint::getPointLabel, StandPoint::getPointId));

        // 标准测点ID -> 真实测点ID（支持一对多）
        Map<Integer, List<Integer>> standToRealMap = standRealRelateMapper.selectList(
                new QueryWrapper<StandRealRelate>().in("stand_point_id", standPointMap.values())
        ).stream().collect(Collectors.groupingBy(
                StandRealRelate::getStandPointId, // 分组键为 stand_point_id
                Collectors.mapping(StandRealRelate::getRealPointId, Collectors.toList()) // 值为 real_point_id 列表
        ));
        // 将标准测点标签 -> 真实测点ID 映射到最终结果
        for (String standPointLabel : standpointList) {
            Integer standPointId = standPointMap.get(standPointLabel);
            List<Integer> realPointIds = standToRealMap.getOrDefault(standPointId, new ArrayList<>());
            standToRealPointMap.put(standPointLabel, realPointIds);
        }
        return standToRealPointMap;
    }
    //根据真实测点id查询标准测点标签到真实测点的映射
    public RealToStandMapping RealToStandLabel(List<Integer> realpointList){
        Map<String, RealPoint> realTostandPointMap = new HashMap<>();
        Map<String, String> realLabelToStandLabelMap = new HashMap<>();
        // 真实测点ID->标准测点ID
        Map<Integer, Integer> standToRealMap = standRealRelateMapper.selectList(
                new QueryWrapper<StandRealRelate>().in("real_point_id", realpointList)
        ).stream().collect(Collectors.toMap(StandRealRelate::getRealPointId, StandRealRelate::getStandPointId));
        //真实测点ID -> 真实测点
        Map<Integer,RealPoint> RealPointMap = realPointMapper.selectList(
                new QueryWrapper<RealPoint>().in("point_id", standToRealMap.keySet())
        ).stream().collect(Collectors.toMap(RealPoint::getPointId, realPoint -> realPoint));
        //标准测点ID -> 标准测点标签
        Map<Integer, String> standPointMap = standPointMapper.selectList(
                new QueryWrapper<StandPoint>().in("point_id", standToRealMap.values())
        ).stream().collect(Collectors.toMap(StandPoint::getPointId, StandPoint::getPointLabel));
        for (Integer realpointId : realpointList) {
            Integer standPointId = standToRealMap.get(realpointId);
            RealPoint realPoint = RealPointMap.get(realpointId);
            String standPointLabel = standPointMap.get(standPointId);
            realTostandPointMap.put(standPointLabel,realPoint);
            realLabelToStandLabelMap.put(realPoint.getPointLabel(), standPointLabel);
        }
        return new RealToStandMapping(realTostandPointMap, realLabelToStandLabelMap);
    }
    private static String getTableName(Integer type) {
        switch (type) {
            case 0:
                return "pvfarm";
            case 1:
                return "combiner_box";
            case 2:
                return "inverter";
            default:
                return "default_table";  // 如果type不匹配任何值，返回默认表名
        }
    }
    public void toTrainCsv(Map<LocalDateTime, Map<String, Object>> alignedData,Map<String, String> realToStandLabel,String modelLabel){
        // 创建目标目录（如果不存在）
        File modelDir = new File(String.format("%s/%s", pythonFilePath, modelLabel));
        if (!modelDir.exists()) {
            if (!modelDir.mkdirs()) {
                throw new FileException("创建文件目录失败");
            }
        }
        // 写入CSV文件
        try (FileWriter csvWriter = new FileWriter(String.format("%s/%s/train.csv", pythonFilePath, modelLabel))) {
            // 写入表头
            csvWriter.append("datetime");
            for (String standPoint : realToStandLabel.values()) {
                csvWriter.append(",").append(standPoint);
            }
            csvWriter.append("\n");
            // 写入数据
            for (Map.Entry<LocalDateTime, Map<String, Object>> entry : alignedData.entrySet()) {
                StringBuilder line = new StringBuilder(entry.getKey().toString());
                for (String standPoint : realToStandLabel.values()) {
                    Double value = (Double) entry.getValue().get(standPoint);
                    line.append(",").append(value);
                }
                csvWriter.append(line.toString()).append("\n");
            }
        } catch (IOException e) {
            throw new FileException("写入CSV文件失败", e);
        }
    }
    private void toPredictCsv(Map<LocalDateTime, Map<String, Object>> alignedData, Map<String, String> realLabelToStandLabelMap, String taskLabel) {
        // 创建目标目录（如果不存在）
        File modelDir = new File(String.format("%s/task_logs/%s", pythonFilePath,taskLabel));
        if (!modelDir.exists()) {
            if (!modelDir.mkdirs()) {
                throw new FileException("创建文件目录失败");
            }
        }
        // 写入 CSV 文件
        try (FileWriter csvWriter = new FileWriter(String.format("%s/task_logs/%s/predict.csv", pythonFilePath, taskLabel))) {
            // 写入表头
            csvWriter.append("datetime");
            for (String standPoint : realLabelToStandLabelMap.values()) {
                csvWriter.append(",").append(standPoint);
            }
            csvWriter.append("\n");
            // 写入数据
            for (Map.Entry<LocalDateTime, Map<String, Object>> entry : alignedData.entrySet()) {
                StringBuilder line = new StringBuilder(entry.getKey().toString());
                for (String standPoint : realLabelToStandLabelMap.values()) {
                    Double value = (Double) entry.getValue().get(standPoint);
                    line.append(",").append(value);
                }
                csvWriter.append(line.toString()).append("\n");
            }
        } catch (IOException e) {
            throw new FileException("写入CSV文件失败", e);
        }
    }
    private void deleteDirectory(File dir) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
        dir.delete();
    }
    public static void writeFileContent(File file, List<ThresholdVO> items) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            String content = JSON.toJSONString(items); // 转换为 JSON 字符串
            content = content.replace("null","NaN");
            writer.write(content);  // 写入内容
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
