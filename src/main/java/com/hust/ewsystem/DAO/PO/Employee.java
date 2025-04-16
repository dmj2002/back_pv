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
public class Employee implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Integer employeeId; // 员工id

    private String username; // 登录用的用户名

    private String password; // 登录用的密码

    private String employeeName; // 员工真实姓名

    private String employeeNumber; // 员工电话

    private Integer permissionLevel; // 权限等级

    private Integer companyId; // 公司id

    private String phoneNumber; // 手机号码

    private String emailAddress; // 邮箱

}
