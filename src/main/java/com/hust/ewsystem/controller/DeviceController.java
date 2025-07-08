package com.hust.ewsystem.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hust.ewsystem.DAO.DTO.FarmDTO;
import com.hust.ewsystem.DAO.PO.*;
import com.hust.ewsystem.DAO.VO.DeviceGetVO;
import com.hust.ewsystem.DAO.VO.DeviceVO;
import com.hust.ewsystem.DAO.VO.StandPointVO;
import com.hust.ewsystem.common.result.EwsResult;
import com.hust.ewsystem.mapper.WarningsMapper;
import com.hust.ewsystem.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/device")
@RequiredArgsConstructor
public class DeviceController {

    private final PvFarmService pvFarmService;

    private final BoxTransService boxTransService;

    private final InverterService inverterService;

    private final CombinerBoxService combinerBoxService;

    private final WarningsMapper warningsMapper;

    private final RealPointService realPointService;

    private final StandRealRelateService standRealRelateService;


    @GetMapping("/list")
    public EwsResult<?> deviceList(@RequestParam(value = "pvFarmId") Integer pvFarmId) {
        Integer pvFarmType = pvFarmService.getById(pvFarmId).getPvFarmType();
        List<DeviceGetVO> deviceGetVOList = new ArrayList<>();
        switch (pvFarmType) {
            case 0:
                List<BoxTrans> boxTransList1 = boxTransService.list(new QueryWrapper<BoxTrans>().eq("pv_farm_id", pvFarmId));
                for(BoxTrans boxTrans : boxTransList1) {
                    DeviceGetVO deviceGetVO = new DeviceGetVO();
                    deviceGetVO.setBoxTrans(boxTrans);
                    List<DeviceGetVO.InnerDeviceInfo> innerDeviceInfoList = new ArrayList<>();
                    List<Inverter> inverters = inverterService.list(new QueryWrapper<Inverter>().eq("box_id", boxTrans.getId()));
                    for(Inverter inverter : inverters) {
                        DeviceGetVO.InnerDeviceInfo innerDeviceInfo = new DeviceGetVO.InnerDeviceInfo();
                        innerDeviceInfo.setInverter(inverter);
                        List<CombinerBox> combinerBoxes = combinerBoxService.list(new QueryWrapper<CombinerBox>().eq("inverter_id", inverter.getId()).eq("box_id", boxTrans.getId()));
                        innerDeviceInfo.setCombinerBoxList(combinerBoxes);
                        innerDeviceInfoList.add(innerDeviceInfo);
                    }
                    deviceGetVO.setInnerDeviceInfoList(innerDeviceInfoList);
                    deviceGetVOList.add(deviceGetVO);
                }
                return EwsResult.OK("查询成功",deviceGetVOList);
            case 1:
                List<BoxTrans> boxTransList2 = boxTransService.list(new QueryWrapper<BoxTrans>().eq("pv_farm_id", pvFarmId));
                for(BoxTrans boxTrans : boxTransList2) {
                    DeviceGetVO deviceGetVO = new DeviceGetVO();
                    deviceGetVO.setBoxTrans(boxTrans);
                    List<DeviceGetVO.InnerDeviceInfo> innerDeviceInfoList = new ArrayList<>();
                    List<Inverter> inverters = inverterService.list(new QueryWrapper<Inverter>().eq("box_id", boxTrans.getId()));
                    for(Inverter inverter : inverters) {
                        DeviceGetVO.InnerDeviceInfo innerDeviceInfo = new DeviceGetVO.InnerDeviceInfo();
                        innerDeviceInfo.setInverter(inverter);
                        innerDeviceInfoList.add(innerDeviceInfo);
                    }
                    deviceGetVO.setInnerDeviceInfoList(innerDeviceInfoList);
                    deviceGetVOList.add(deviceGetVO);
                }
                return EwsResult.OK("查询成功",deviceGetVOList);
            default:
                return EwsResult.error("不支持的电站类型");
        }
    }
//    @GetMapping("/getDeviceInfo")
//    public EwsResult<?> getDeviceInfo(@RequestParam(value = "warningId") Integer warningId){
//        StandPointVO standPointByWarningId = warningsMapper.getStandPointByWarningId(warningId);
//        if (standPointByWarningId == null) {
//            return EwsResult.error("查询失败");
//        }
//        Integer modelType = standPointByWarningId.getModelType();
//        List<StandPoint> filterPointList = standPointByWarningId.getPoints().stream().filter(point -> (modelType == 0 && point.getPointType() == 0) || (modelType != 0 && (point.getPointType() == modelType || point.getPointType() == 0))).collect(Collectors.toList());
//
//        return EwsResult.OK("查询成功", filterPointList);
//    }
    @GetMapping("/getDeviceInfo")
    public EwsResult<?> getDeviceInfo(@RequestParam(value = "warningId") Integer warningId){
        DeviceVO deviceInfoByWarningId = warningsMapper.getDeviceInfoByWarningId(warningId);
        if (deviceInfoByWarningId == null) {
            return EwsResult.error("查询失败");
        }
        Integer deviceId = deviceInfoByWarningId.getDeviceId();
        Integer deviceType = deviceInfoByWarningId.getDeviceType();
        Integer pvFarmId = null;
        if(deviceType == 1){
            //获取汇流箱id
            Integer boxId = combinerBoxService.getById(deviceId).getBoxId();
            pvFarmId = boxTransService.getById(boxId).getPvFarmId();
        }else if(deviceType == 2){
            //获取逆变器id
            Integer boxId = inverterService.getById(deviceId).getBoxId();
            pvFarmId = boxTransService.getById(boxId).getPvFarmId();
        }
        Map<RealPoint, Integer> realPointStandPointMap = new HashMap<>();
        realPointService.list(new QueryWrapper<RealPoint>().eq("pv_farm_id", pvFarmId).in("point_type", deviceType, 0)).stream().forEach(
                realPoint -> {
                    Integer standPointId = standRealRelateService.getOne(new QueryWrapper<StandRealRelate>().eq("real_point_id", realPoint.getPointId())).getStandPointId();
                    realPointStandPointMap.put(realPoint, standPointId);
                }
        );
        return EwsResult.OK("查询成功", realPointStandPointMap);
    }
    @GetMapping("/getDeviceName")
    public EwsResult<?> getDeviceName(@RequestParam(value = "deviceId") Integer deviceId,
                                      @RequestParam(value = "deviceType") Integer deviceType) {
        if(deviceType == 0) {
            String pvFarmName = pvFarmService.getById(deviceId).getPvFarmName();
            return EwsResult.OK("查询成功", pvFarmName);
        }else if(deviceType == 1) {
            String combinerBoxName = combinerBoxService.getById(deviceId).getCombinerBoxName();
            return EwsResult.OK("查询成功", combinerBoxName);
        }else if(deviceType == 2) {
            String inverterName = inverterService.getById(deviceId).getInverterName();
            return EwsResult.OK("查询成功", inverterName);
        }else{
            return EwsResult.error("查询失败,不包含次设备类型");
        }
    }
    @GetMapping("/getfarmInfo")
    public EwsResult<FarmDTO>getfarmInfo(@RequestParam(value = "deviceId") Integer deviceId,
                                         @RequestParam(value = "deviceType") Integer deviceType){
        FarmDTO farmDTO = new FarmDTO();
        if(deviceType == 0) {
            Integer pvFarmId = pvFarmService.getById(deviceId).getId();
            String pvFarmName = pvFarmService.getById(pvFarmId).getPvFarmName();
            farmDTO.setPvFarmId(pvFarmId);
            farmDTO.setPvFarmName(pvFarmName);
            return EwsResult.OK("查询成功", farmDTO);
        }
        else if(deviceType == 1){
            CombinerBox combinerBox = combinerBoxService.getById(deviceId);
            BoxTrans boxTrans = boxTransService.getById(combinerBox.getBoxId());
            String pvFarmName = pvFarmService.getById(boxTrans.getPvFarmId()).getPvFarmName();
            farmDTO.setPvFarmId(boxTrans.getPvFarmId());
            farmDTO.setPvFarmName(pvFarmName);
            farmDTO.setCombinerBoxId(combinerBox.getId());
            farmDTO.setCombinerBoxName(combinerBox.getCombinerBoxName());
            return EwsResult.OK("查询成功", farmDTO);
        }
        else if(deviceType == 2){
            Inverter inverter = inverterService.getById(deviceId);
            BoxTrans boxTrans = boxTransService.getById(inverter.getBoxId());
            String pvFarmName = pvFarmService.getById(boxTrans.getPvFarmId()).getPvFarmName();
            farmDTO.setPvFarmId(boxTrans.getPvFarmId());
            farmDTO.setPvFarmName(pvFarmName);
            farmDTO.setInverterId(inverter.getId());
            farmDTO.setInverterName(inverter.getInverterName());
            return EwsResult.OK("查询成功", farmDTO);
        }else{
            return EwsResult.error("查询失败,不包含次设备类型");
        }
    }
}
