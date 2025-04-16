package com.hust.ewsystem.DAO.PO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Algorithms implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Integer algorithmId; // 算法id

    private String algorithmLabel; // 算法编号

    private String algorithmName; // 算法名称

    private String algorithmVersion; // 算法版本

    private String description; // 算法描述

    private Integer algorithmType; // 算法类型
}
