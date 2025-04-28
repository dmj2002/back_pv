package com.hust.ewsystem.DAO.DTO;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;



@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class QueryWarnInfoDTO implements Serializable {

    private static final long serialVersionUID = 3612242938736654559L;


    @NotNull(message = "ID不能为空")
    private int pvFarmId;


    private Integer deviceId;


    private Integer deviceType;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime  endTime;

    /**
     * 预警描述
     */
    private String warningDescription;
}
