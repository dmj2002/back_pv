package com.hust.ewsystem.common.exception;


public class ParamCheckFailException extends BaseRuntimeException {

	private static final long serialVersionUID = -3850015449550438380L;
	
	private String errorCode = null;
	
    
    public ParamCheckFailException(String errorMsg) {
        super(errorMsg);
    }
    
	/**
	 * 校验失败构造方法
	 * @param errorCode
	 * @param errorMsg
	 */
	public ParamCheckFailException(String errorCode, String errorMsg) {
		super(errorMsg);
		this.errorCode = errorCode;
	}

    /**
	 * 错误编码
	 * @return
	 */
	public String getErrorCode(){
		return errorCode;
	}
}
