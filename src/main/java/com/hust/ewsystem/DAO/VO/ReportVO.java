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

    /**
     * 员工查姓名
     */
    private String employeeName;

    public ReportVO() {
        super();
    }
}
