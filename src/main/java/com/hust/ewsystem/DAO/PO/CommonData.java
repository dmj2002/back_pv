package com.hust.ewsystem.DAO.PO;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class CommonData implements Serializable {

    private static final long serialVersionUID = 1L;

    private LocalDateTime datetime;

    private Integer status;

    private Double value;
}
