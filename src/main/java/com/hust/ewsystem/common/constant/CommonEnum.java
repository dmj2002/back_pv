package com.hust.ewsystem.common.constant;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public enum CommonEnum {

	
	SUCCESS("200", "调用成功"),
	PARAMETER_ERROR("401", "参数错误"),
	SECURITY_ERROR("402", "安全码错误"),
	METHOD_ERROR("403", "接口服务名称错误"),
	BUSINESS_CHECK_ERROR("405", "业务校验失败"),
	CHECK_ERROR("406", "查验失败"),
	SERVICE_ERROR("421", "服务不可用"),
	SYSTEME_RROR("500", "系统错误"),

	/**
	 * 无权限查询
	 */
	MID_NO_PREM_ERROR("9998", "无权限查询"),
	/**
	 * 系统内部错误
	 */
	MID_SYS_INTL_ERROR("9999", "系统内部错误"),
	;

	private static final Map<String, String> RESULT_MAP;

	static {
		Map<String, String> map = new HashMap<String, String>();
		for (CommonEnum e : CommonEnum.values()) {
			map.put(e.getCode(), e.getMsg());
		}
		RESULT_MAP = Collections.unmodifiableMap(map);
	}

	private String code;

	private String msg;

	private CommonEnum(String code, String msg) {
		this.code = code;
		this.msg = msg;
	}
	
	public static String getMsgByCode(String code) {
		return RESULT_MAP.get(code);
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

}
