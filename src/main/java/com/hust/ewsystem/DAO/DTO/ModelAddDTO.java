package com.hust.ewsystem.DAO.DTO;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ModelAddDTO {

    private String modelName;

    private Integer pvFarmId;

    private Integer isAll;

    private Integer deviceType;

    private List<Integer> deviceList;

    private Integer algorithmId;

    private Integer alertInterval;

    private Integer alertWindowSize;

    private Integer patternId;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private JsonNode params;

    private List<String> pointList;  // 标准测点标签


}
