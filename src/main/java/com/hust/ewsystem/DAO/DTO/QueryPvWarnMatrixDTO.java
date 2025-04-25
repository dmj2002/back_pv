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
public class QueryPvWarnMatrixDTO  {

    @JsonFormat(pattern = CommonConstant.DATETIME_FORMAT_1, timezone = CommonConstant.TIME_ZONE)
    private LocalDateTime startDate;

    @JsonFormat(pattern = CommonConstant.DATETIME_FORMAT_1, timezone = CommonConstant.TIME_ZONE)
    private LocalDateTime endDate;

    private Integer pvFarmId;
}
