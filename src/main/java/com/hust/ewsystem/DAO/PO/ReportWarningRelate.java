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
public class ReportWarningRelate implements Serializable {


    private static final long serialVersionUID = -6151687601604427556L;

    @TableId(type = IdType.AUTO)
    private Integer id; // 记录id

    private Integer reportId; // 通知id

    private Integer warningId; // 预警id
}
