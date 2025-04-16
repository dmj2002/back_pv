package com.hust.ewsystem.DAO.PO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class AlgorithmStandRelate {

    @TableId(type = IdType.AUTO)
    private Integer id; // 记录id

    private Integer algorithmId; // 算法id

    private Integer standPointId; // 标准测点id
}
