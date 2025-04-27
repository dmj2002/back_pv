package com.hust.ewsystem.DAO.DTO;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class WarnStatusDTO implements Serializable {

    private static final long serialVersionUID = 4322558269725152286L;

    /**
     * 一级预警待处理数 预警状态为0 预警等级为1的数量
     */
    private int warningLevel1waitDone;

    /**
     * 二级预警待处理数 预警状态为0 预警等级为2的数量
     */
    private int warningLevel2waitDone;

    /**
     * 一级预警挂起数 预警状态为1的数量 预警等级为1的数量
     */
    private int warningLevel1waitHangUp;

    /**
     * 二级预警挂起数 预警状态为1的数量 预警等级为2的数量
     */
    private int warningLevel2waitHangUp;

    /**
     * 一级预警关闭待确认数 预警状态为3 预警等级为1的数量
     */
    private int warningLevel1waitCloseWait;

    /**
     * 二级预警关闭待确认数 预警状态为3 预警等级为2的数量
     */
    private int warningLevel2waitCloseWait;
}
