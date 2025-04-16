package com.hust.ewsystem.DAO.PO;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Models implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Integer modelId;  // 模型id

    private String modelName;  // 模型名称

    private String modelLabel;  // 模型标签

    private Integer algorithmId;  // 算法id

    private String modelVersion;  // 模型版本

    @TableField(typeHandler = JacksonTypeHandler.class)
    private JsonNode modelParameters;  // 模型参数

    private Integer modelStatus;  // 模型状态

    private Integer creatorId;  // 创建者id

    private Integer deviceId;  // 风机id

    private Integer modelType;  // 模型类型

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;  // 创建时间

    private Integer lastActivatedId;  // 最后使用id

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime lastActivatedTime;  // 最后使用时间

    private Integer patternId;  // 工况id

    private Integer alertInterval;  // 每隔多少秒跑一次算法文件

    private Integer alertWindowSize;  // 算法文件窗口大小
}
