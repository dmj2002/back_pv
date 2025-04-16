package com.hust.ewsystem.common.exception;


/**
 * 异常类
 */

public class EwsException extends RuntimeException {

    public EwsException(Throwable cause) {
        super(cause);
    }

    /**
     * 异常错误码
     */
    private int code;
    

    public EwsException(String message, int code) {
        super(message);
        this.code = code;
    }

    public EwsException(Throwable cause, int code) {
        super(cause);
        this.code = code;
    }

    public EwsException(String message, Throwable cause, int code) {
        super(message, cause);
        this.code = code;
    }

    /**
     * 异常信息
     * @param message 异常信息
     */
    public EwsException(String message) {
        super(message);
    }
    /**
     * 异常编码+异常信息
     * @param code  异常编码
     * @param message 异常信息
     */
    public EwsException(int code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * 异常信息+堆栈
     * @param message 异常信息
     * @param cause 堆栈
     */
    public EwsException(String message, Throwable cause) {
        super(message, cause);
    }

    public EwsException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    /**
     * 获取异常错误码
     * @return 异常错误码
     */
    public int getCode() {
        return this.code;
    }
    /**
     * 赋值异常错误码
     * @param code 异常错误码
     */
    public void setCode(int code) {
        this.code = code;
    }
}
