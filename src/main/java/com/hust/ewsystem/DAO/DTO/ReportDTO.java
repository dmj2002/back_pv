package com.hust.ewsystem.DAO.DTO;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ReportDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer reportId;  // 报告id

    private Integer reportStatus;  // 状态

    private String reportText;  // 通知文本

}
