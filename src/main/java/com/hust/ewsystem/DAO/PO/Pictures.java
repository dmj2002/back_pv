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
public class Pictures implements Serializable {

    private static final long serialVersionUID = -6151687601604427556L;
    @TableId(type = IdType.AUTO)
    private Integer id; // 记录id

    private String warningDescription; // 预警描述

    private String picName; // 图片名称

    private Integer algorithmId; // 算法id

    private Double threshold; // 阈值

    private Integer picType; // 图片类型

    private Integer flag; // 标志

    private Integer bias; // 偏移量

}
