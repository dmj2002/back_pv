package com.hust.ewsystem.DAO.VO;


import com.hust.ewsystem.DAO.PO.StandPoint;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class StandPointVO {

    private List<StandPoint> points;

    private Integer modelType;
}
