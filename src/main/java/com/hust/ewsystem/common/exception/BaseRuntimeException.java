package com.hust.ewsystem.common.exception;

import org.springframework.core.NestedRuntimeException;


@SuppressWarnings("serial")
public abstract class BaseRuntimeException extends NestedRuntimeException {
	private String errorCode = null;
	
    public BaseRuntimeException(String msg) {
        super(msg);
    }
    
    public BaseRuntimeException(Throwable cause) {
        super((cause==null ? null : cause.toString()), cause);
    }

    public BaseRuntimeException(String msg, Throwable cause) {
        super(msg, cause);
    }

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
}
