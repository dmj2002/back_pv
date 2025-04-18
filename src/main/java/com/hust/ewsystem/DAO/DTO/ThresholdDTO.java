package com.hust.ewsystem.DAO.DTO;

import com.hust.ewsystem.DAO.VO.ThresholdVO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ThresholdDTO {

    private Integer modelId;

    private List<ThresholdVO> items;
}
