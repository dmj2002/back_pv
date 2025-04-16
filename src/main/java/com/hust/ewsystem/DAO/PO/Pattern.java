package com.hust.ewsystem.DAO.PO;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Pattern implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId
    private Integer patternId; // 工况id

    private String patternName; // 工况名称

    private Integer patternState; // 工况状态

    private Integer patternPriority; // 工况优先级
}
