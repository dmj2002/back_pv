package com.hust.ewsystem.DAO.VO;

import com.hust.ewsystem.DAO.PO.BoxTrans;
import com.hust.ewsystem.DAO.PO.CombinerBox;
import com.hust.ewsystem.DAO.PO.Inverter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class DeviceGetVO {

    private BoxTrans boxTrans;

    private List<InnerDeviceInfo> innerDeviceInfoList;

    @Data

    public static class InnerDeviceInfo {
       private Inverter inverter;
       private List<CombinerBox> combinerBoxList;
    }

}
