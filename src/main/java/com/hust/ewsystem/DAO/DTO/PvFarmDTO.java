package com.hust.ewsystem.DAO.DTO;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class PvFarmDTO {

    private Integer pvFarmId;

    private String pvFarmName;

    private Integer companyId;

    private String companyName;

    private List<InverterWaitDoneDTO> inverterWaitDoneInfo;
}
