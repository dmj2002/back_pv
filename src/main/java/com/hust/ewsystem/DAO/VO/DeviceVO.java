package com.hust.ewsystem.DAO.VO;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class DeviceVO {

    private Integer deviceId;

    private Integer deviceType;
}
