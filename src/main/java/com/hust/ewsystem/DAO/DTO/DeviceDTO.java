package com.hust.ewsystem.DAO.DTO;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class DeviceDTO {

    private Integer deviceId;  // 设备id

    private Integer deviceType;  // 设备类型
}
