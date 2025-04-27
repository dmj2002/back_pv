package com.hust.ewsystem.DAO.VO;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;


@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class WarningsVO implements Serializable {

    private static final long serialVersionUID = -8957749259970679146L;

    /**
     * 预警 id
     */
    private Integer warningId;

    /**
     * 预警等级
     */
    private Integer warningLevel;

    /**
     * 预警状态
     */
    private Integer warningStatus;

    /**
     * 模型 id
     */
    private Integer modelId;

    /**
     * 预警开始时间
     */
    private LocalDateTime startTime;

    /**
     * 预警结束时间
     */
    private LocalDateTime endTime;

    /**
     * 处理人 id
     */
    private Integer handlerId;

    /**
     * 处理时间
     */
    private LocalDateTime handleTime;

    /**
     * 预警描述
     */
    private String warningDescription;

    /**
     * 是否转入工单
     */
    private Integer transferredToWorkOrder;

    /**
     * 任务 id
     */
    private Integer taskId;


    private Integer deviceId;


    private Integer deviceType;

    /**
     * 是否有效，0否1是,
     */
    private Integer valid;

    /**
     * 是否重复，0否1是,
     */
    private Integer repetition;

    /**
     * 系统分类
     */
    private String systemSort;
}
