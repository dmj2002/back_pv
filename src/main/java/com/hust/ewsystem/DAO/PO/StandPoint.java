package com.hust.ewsystem.DAO.PO;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class StandPoint implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId
    private Integer pointId; // 测点id

    private String pointLabel; // 测点标签

    private String pointDescription; // 测点描述

    private String pointUnit; // 测点单位

    private Integer pointType; // 测点类型

}
