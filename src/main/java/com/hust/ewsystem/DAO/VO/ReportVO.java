package com.hust.ewsystem.DAO.VO;

import com.hust.ewsystem.DAO.PO.Reports;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class ReportVO extends Reports {
    private String deviceName;

    public ReportVO() {
        super();
    }
}
