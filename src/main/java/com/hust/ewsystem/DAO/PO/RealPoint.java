package com.hust.ewsystem.DAO.PO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class RealPoint implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Integer pointId;

    private String pointLabel;

    private String pointDescription;

    private String pointUnit;

    private Integer pointType;

    private Integer deviceId;

    private Integer pvFarmId;

}
