package com.hust.ewsystem.DAO.DTO;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;


@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ModelsDTO {

    /**
     * 模型id
     */
    private Integer modelId;

    /**
     * 模型名称
     */
    private String modelName;

    /**
     * 一级预警数
     */
    private int warningLevel1Sum;

    /**
     * 二级预警数
     */
    private int warningLevel2Sum;
}
