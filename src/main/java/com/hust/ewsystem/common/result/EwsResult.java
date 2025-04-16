package com.hust.ewsystem.common.result;

import com.hust.ewsystem.common.constant.CommonConstant;
import java.io.Serializable;

/**
 *通用返回结果类
 */
public class EwsResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 返回处理消息
     */
    private String message = "";

    /**
     * 返回代码
     */
    private Integer code = 0;

    /**
     * 返回数据对象 data
     */
    private Object result;

    public EwsResult() {
    }

    public static<T> EwsResult<T> OK(T data) {
        EwsResult<T> r = new EwsResult<T>();
        r.setCode(CommonConstant.NUM_COMMON_200);
        r.setResult(data);
        return r;
    }

    public static<T> EwsResult<T> OK(String msg) {
        EwsResult<T> r = new EwsResult<T>();
        r.setCode(CommonConstant.NUM_COMMON_200);
        r.setMessage(msg);
        return r;
    }

    public static<T> EwsResult<T> OK(String msg, T data) {
        EwsResult<T> r = new EwsResult<T>();
        r.setCode(CommonConstant.NUM_COMMON_200);
        r.setMessage(msg);
        r.setResult(data);
        return r;
    }

    public static<T> EwsResult<T> error(String msg, T data) {
        EwsResult<T> r = new EwsResult<T>();
        r.setCode(CommonConstant.NUM_COMMON_500);
        r.setMessage(msg);
        r.setResult(data);
        return r;
    }

    public static<T> EwsResult<T> error(String msg) {
        return error(CommonConstant.NUM_COMMON_500, msg);
    }

    public static<T> EwsResult<T> error(int code, String msg) {
        EwsResult<T> r = new EwsResult<T>();
        r.setCode(code);
        r.setMessage(msg);
        return r;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}