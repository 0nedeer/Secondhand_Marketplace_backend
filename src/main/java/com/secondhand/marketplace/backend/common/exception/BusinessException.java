package com.secondhand.marketplace.backend.common.exception;

import lombok.Getter;

//业务异常类
 /* 用于处理业务逻辑中的异常情况（如：用户名已存在、密码错误等）*/

@Getter
public class BusinessException extends RuntimeException {

    private Integer code;  // 错误码
    private String message; // 错误信息

    public BusinessException(String message) {
        super(message);
        this.code = 500;
        this.message = message;
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.code = 500;
        this.message = message;
    }

    public BusinessException(Integer code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
    }
}