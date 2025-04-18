package com.hust.ewsystem.DAO.VO;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@AllArgsConstructor
public class ThresholdVO {

    @JSONField(name = "下限")
    private Double lowerLimit;

    @JSONField(name = "上限")
    private Double upperLimit;

    @JSONField(name = "范围")
    private List<Double> range;
}
