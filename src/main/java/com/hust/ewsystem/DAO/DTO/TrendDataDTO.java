package com.hust.ewsystem.DAO.DTO;

import com.hust.ewsystem.DAO.PO.CommonData;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @BelongsProject: back
 * @BelongsPackage: com.hust.ewsystem.DTO
 * @Author: xdy
 * @CreateTime: 2024-11-26  14:24
 * @Description:
 * @Version: 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class TrendDataDTO implements Serializable {

    private static final long serialVersionUID = -8458941268861711389L;

    /**
     * 测点ID
     */
    private Integer pointId;

    /**
     * 测点数据
     */
    private List<CommonData> pointValue;
}
