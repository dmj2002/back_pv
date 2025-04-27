package com.hust.ewsystem.DAO.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hust.ewsystem.common.constant.CommonConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;



@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class QueryWaitDoneInfoDTO implements Serializable {

    private static final long serialVersionUID = 2590212178646975576L;

    /**
     * 公司编号
     */
    private int companyId;

    /**
     * 开始时间
     */
    @NotNull(message = "开始时间不能为空")
    @JsonFormat(pattern = CommonConstant.DATETIME_FORMAT_1, timezone = CommonConstant.TIME_ZONE)
    private LocalDateTime startDate;

    /**
     * 结束时间
     */
    @NotNull(message = "结束时间不能为空")
    @JsonFormat(pattern = CommonConstant.DATETIME_FORMAT_1, timezone = CommonConstant.TIME_ZONE)
    private LocalDateTime  endDate;

    /**
     * 信息类别 0全部 1一级预警 2二级预警 3通知
     */
    private int infoType;
}
