package com.hust.ewsystem.common.constant;

/**
 * 每个枚举值（如 SQL_ERROR, PARAM_ERROR 等）都有两个属性：
 * 状态码（code）：表示特定错误或状态的整数值。
 * 描述信息（desc）：提供该状态码的文本描述。
 */
public enum ResultCodeEnum {

    //系统级状态码
	SQL_ERROR(-1, "数据库执行失败"),
	PARAM_ERROR(-2, "参数校验失败"),

    CALL_COM_ERROR(1511,"调用COM组件失败"),

    CALL_DLL_INIT_ERROR(1512,"动态库初始化函数执行异常"),

    CALL_DLL_ERROR(1513,"调用动态库接口异常"),
    
    JACKSON_ERROR(1801, "jsckson解析失败");


    /**
     * 状态码
     */
    private int code;
    /**
     * 信息描述
     */
    private String desc;

    /**
     * 错误码枚举构造方法
     * @param code
     * @param desc
     */
    ResultCodeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 获取状态码
     * @return
     */
    public int getCode() {
        return code;
    }

    /**
     * 获取描述信息
     * @return
     */
    public String getDesc() {
        return desc;
    }
}
