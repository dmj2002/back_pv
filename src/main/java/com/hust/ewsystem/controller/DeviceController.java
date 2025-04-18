package com.hust.ewsystem.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hust.ewsystem.DAO.PO.BoxTrans;
import com.hust.ewsystem.DAO.PO.CombinerBox;
import com.hust.ewsystem.DAO.PO.Inverter;
import com.hust.ewsystem.DAO.VO.DeviceGetVO;
import com.hust.ewsystem.common.result.EwsResult;
import com.hust.ewsystem.service.BoxTransService;
import com.hust.ewsystem.service.CombinerBoxService;
import com.hust.ewsystem.service.InverterService;
import com.hust.ewsystem.service.PvFarmService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/device")
@RequiredArgsConstructor
public class DeviceController {

    private final PvFarmService pvFarmService;

    private final BoxTransService boxTransService;

    private final InverterService inverterService;

    private final CombinerBoxService combinerBoxService;

    @GetMapping("/list")
    public EwsResult<?> turbineList(@RequestParam(value = "pvFarmId") Integer pvFarmId) {
        Integer pvFarmType = pvFarmService.getById(pvFarmId).getPvFarmType();
        List<DeviceGetVO> deviceGetVOList = new ArrayList<>();
        switch (pvFarmType) {
            case 0:
                List<BoxTrans> boxTransList1 = boxTransService.list(new QueryWrapper<BoxTrans>().eq("pv_farm_id", pvFarmId));
                for(BoxTrans boxTrans : boxTransList1) {
                    DeviceGetVO deviceGetVO = new DeviceGetVO();
                    deviceGetVO.setBoxTrans(boxTrans);
                    List<DeviceGetVO.InnerDeviceInfo> innerDeviceInfoList = new ArrayList<>();
                    List<Inverter> inverters = inverterService.list(new QueryWrapper<Inverter>().eq("box_id", boxTrans.getBoxId()));
                    for(Inverter inverter : inverters) {
                        DeviceGetVO.InnerDeviceInfo innerDeviceInfo = new DeviceGetVO.InnerDeviceInfo();
                        innerDeviceInfo.setInverter(inverter);
                        List<CombinerBox> combinerBoxes = combinerBoxService.list(new QueryWrapper<CombinerBox>().eq("inverter_id", inverter.getId()).eq("box_id", boxTrans.getBoxId()));
                        innerDeviceInfo.setCombinerBoxList(combinerBoxes);
                        innerDeviceInfoList.add(innerDeviceInfo);
                    }
                    deviceGetVOList.add(deviceGetVO);
                }
                return EwsResult.OK("查询成功",deviceGetVOList);
            case 1:
                List<BoxTrans> boxTransList2 = boxTransService.list(new QueryWrapper<BoxTrans>().eq("pv_farm_id", pvFarmId));
                for(BoxTrans boxTrans : boxTransList2) {
                    DeviceGetVO deviceGetVO = new DeviceGetVO();
                    deviceGetVO.setBoxTrans(boxTrans);
                    List<DeviceGetVO.InnerDeviceInfo> innerDeviceInfoList = new ArrayList<>();
                    List<Inverter> inverters = inverterService.list(new QueryWrapper<Inverter>().eq("box_id", boxTrans.getBoxId()));
                    for(Inverter inverter : inverters) {
                        DeviceGetVO.InnerDeviceInfo innerDeviceInfo = new DeviceGetVO.InnerDeviceInfo();
                        innerDeviceInfo.setInverter(inverter);
                        innerDeviceInfoList.add(innerDeviceInfo);
                    }
                    deviceGetVOList.add(deviceGetVO);
                }
            default:
                return EwsResult.error("不支持的电站类型");
        }
    }
}
