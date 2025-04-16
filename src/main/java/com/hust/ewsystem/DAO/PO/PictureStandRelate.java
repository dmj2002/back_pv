package com.hust.ewsystem.DAO.PO;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Builder
public class PictureStandRelate implements Serializable {

    private static final long serialVersionUID = -6151687601604427556L;

    @TableId(type = IdType.AUTO)
    private Integer id; // 记录id

    private Integer pictureId; // 图片id

    private Integer standPointId; // 标准测点id
}
