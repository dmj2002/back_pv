package com.hust.ewsystem.DAO.DTO;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class FarmDTO {

    private Integer pvFarmId;

    private String pvFarmName;

    private Integer inverterId;

    private String inverterName;

    private Integer combinerBoxId;

    private String combinerBoxName;
}
