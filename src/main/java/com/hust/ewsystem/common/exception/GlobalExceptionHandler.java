package com.hust.ewsystem.common.exception;


import com.hust.ewsystem.common.constant.CommonEnum;
import com.hust.ewsystem.common.result.EwsResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.util.Objects;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<EwsResult<?>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
//        e.printStackTrace();
//        EwsResult<?> result = EwsResult.error(StringUtils.hasLength(e.getMessage()) ? e.getMessage() : "校验参数失败");
//        return new ResponseEntity<>(result, HttpStatus.UNAUTHORIZED); // 401 状态码
//    }
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<EwsResult<?>> handleConstraintViolationException(ConstraintViolationException e) {
        e.printStackTrace();
        EwsResult<?> result = EwsResult.error(StringUtils.hasLength(e.getMessage()) ? e.getMessage() : "校验参数失败");
        return new ResponseEntity<>(result, HttpStatus.UNAUTHORIZED); // 401 状态码
    }
    @ExceptionHandler(FileException.class)
    public ResponseEntity<EwsResult<?>> handleFileSaveException(FileException e) {
        e.printStackTrace();
        EwsResult<?> result = EwsResult.error(StringUtils.hasLength(e.getMessage()) ? e.getMessage() : "文件保存失败");
        return new ResponseEntity<>(result, HttpStatus.UNAUTHORIZED); // 401 状态码
    }
    @ExceptionHandler(CrudException.class)
    public ResponseEntity<EwsResult<?>> handleFileSaveException(CrudException e) {
        e.printStackTrace();
        EwsResult<?> result = EwsResult.error(StringUtils.hasLength(e.getMessage()) ? e.getMessage() : "数据库操作失败");
        return new ResponseEntity<>(result, HttpStatus.UNAUTHORIZED); // 401 状态码
    }
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<EwsResult<?>> handleMissingParams(MissingServletRequestParameterException e) {
        e.printStackTrace();
        EwsResult<?> result = EwsResult.error("缺少必需的参数: " +  e.getParameterName());
        return new ResponseEntity<>(result, HttpStatus.UNAUTHORIZED); // 401 状态码
    }

    @ExceptionHandler({EwsException.class})
    public EwsResult<?> handleEwsException(EwsException e) {
        log.error("EWS异常拦截：异常编码:{},异常原因:{}", e.getCode(), e.getMessage(), e);
        return EwsResult.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public EwsResult<?> handleMethodArgumentNotValidExceptionX(MethodArgumentNotValidException e) {
        log.error("参数校验失败：{}",  Objects.requireNonNull(e.getFieldError()).getDefaultMessage());
        return EwsResult.error(org.apache.commons.lang3.StringUtils.isBlank(Objects.requireNonNull(e.getFieldError()).getDefaultMessage()) ? "参数检验失败" : Objects.requireNonNull(e.getFieldError()).getDefaultMessage());
    }

    @ExceptionHandler(ParamCheckFailException.class)
    public EwsResult<?> exceptionHandler(ParamCheckFailException e) {
        String errorCode = org.apache.commons.lang3.StringUtils.isBlank(e.getErrorCode()) ?
                CommonEnum.PARAMETER_ERROR.getCode() : e.getErrorCode();
        String errorMsg = CommonEnum.getMsgByCode(errorCode);
        String expMessage = e.getMessage();
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(expMessage)) {
            errorMsg = org.apache.commons.lang3.StringUtils.isEmpty(errorMsg) ? expMessage : String.format("%s:%s", errorMsg, e.getMessage());
        }
        log.error("参数校验失败：{}", errorMsg, e);
        return EwsResult.error(Integer.parseInt(errorCode), errorMsg);
    }
}
