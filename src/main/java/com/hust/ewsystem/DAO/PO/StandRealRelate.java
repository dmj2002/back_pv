package com.hust.ewsystem.DAO.PO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class StandRealRelate {

    @TableId(type = IdType.AUTO)
    private Integer id; // 记录id

    private Integer standPointId; // 标准测点id

    private Integer realPointId; // 实测测点id
}
