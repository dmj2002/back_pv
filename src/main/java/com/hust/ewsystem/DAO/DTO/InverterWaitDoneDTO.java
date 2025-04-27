package com.hust.ewsystem.DAO.DTO;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class InverterWaitDoneDTO {

    private Integer deviceId;

    private String deviceName;

    /**
     * 一级预警待处理数 预警状态为0 预警等级为1的数量
     */
    private int warningLevel1waitDoneSum;

    /**
     * 二级预警待处理数 预警状态为0 预警等级为2的数量
     */
    private int warningLevel2waitDoneSum;

    /**
     * 一级预警挂起数 预警状态为1的数量 预警等级为1的数量
     */
    private int warningLevel1waitHangUpSum;

    /**
     * 二级预警挂起数 预警状态为1的数量 预警等级为2的数量
     */
    private int warningLevel2waitHangUpSum;

    /**
     * 一级预警关闭待确认数 预警状态为3 预警等级为1的数量
     */
    private int warningLevel1waitCloseWaitSum;

    /**
     * 二级预警关闭待确认数 预警状态为3 预警等级为2的数量
     */
    private int warningLevel2waitCloseWaitSum;

    /**
     * 通知总数
     */
    private int reportSum;

    /**
     * 模型中预警数量和通知数量
     */
    private List<ModelsDTO> modelList;


    /**
     * 逆变器下汇流箱中预警数量和通知数量
     */
    private List<CombinerBoxWaitDoneDTO> combinerBoxWaitDoneInfo; ;
}
