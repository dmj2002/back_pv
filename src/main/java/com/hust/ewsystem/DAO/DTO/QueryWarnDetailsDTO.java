package com.hust.ewsystem.DAO.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;


@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class QueryWarnDetailsDTO {



    private List<Integer> pointIdList;


    private Integer deviceId;


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endDate;
}
