package com.hust.ewsystem.DAO.PO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Tasks {

    @TableId(type = IdType.AUTO)
    private Long taskId;

    private String taskLabel;

    private Integer taskType;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String errorMessage;

    private Integer modelId;
}
