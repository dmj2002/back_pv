package com.hust.ewsystem.DAO.DTO;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @BelongsProject: back
 * @BelongsPackage: com.hust.ewsystem.DTO
 * @Author: xdy
 * @CreateTime: 2025-01-07  10:54
 * @Description:
 * @Version: 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class GetWarningsCountDTO {

    private Integer warningLevel;
    private Integer deviceId;
    private Integer deviceType;
    private Integer modelId;
    private LocalDateTime endTime;
    private LocalDateTime startTime;
}
