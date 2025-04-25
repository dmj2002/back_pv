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
public class QueryReportsDTO implements Serializable {
    private static final long serialVersionUID = 4934623373871679934L;

    @NotNull(message = "分页大小不能为空")
    private Integer pageSize;


    @NotNull(message = "页码不能为空")
    private Integer pageNo;


    private Integer inverterId;


    private Integer combinerBoxId;


    private Integer pvFarmId;

    /**
     * 开始时间
     */
    @NotNull(message = "开始时间不能为空")
    @JsonFormat(pattern = CommonConstant.DATETIME_FORMAT_1, timezone = CommonConstant.TIME_ZONE)
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @NotNull(message = "结束时间不能为空")
    @JsonFormat(pattern = CommonConstant.DATETIME_FORMAT_1, timezone = CommonConstant.TIME_ZONE)
    private LocalDateTime endTime;
}
