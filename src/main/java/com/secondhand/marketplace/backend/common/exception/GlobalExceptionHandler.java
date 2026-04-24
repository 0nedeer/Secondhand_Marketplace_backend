package com.secondhand.marketplace.backend.common.exception;

import com.secondhand.marketplace.backend.common.api.CommonResult;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public CommonResult<Void> handleBusinessException(BusinessException e) {
        log.warn("业务异常: {}", e.getMessage());
        Integer code = e.getCode();
        return CommonResult.error(code == null ? 500 : code, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public CommonResult<Void> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .findFirst()
                .orElse("参数校验失败");
        return CommonResult.error(400, message);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public CommonResult<Void> handleConstraintViolationException(ConstraintViolationException e) {
        String message = e.getConstraintViolations().stream()
                .findFirst()
                .map(ConstraintViolation::getMessage)
                .orElse("参数校验失败");
        return CommonResult.error(400, message);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public CommonResult<Void> handleNoResourceFoundException(NoResourceFoundException e) {
        log.warn("请求路径不存在: {}", e.getResourcePath());
        return CommonResult.error(404, "接口不存在，请检查请求路径和方法");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public CommonResult<Void> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        log.warn("数据完整性异常: {}", e.getMessage());
        return CommonResult.error(400, "提交的数据违反了数据库约束(如分类不存在或数据过长)，请检查后重试");
    }

    @ExceptionHandler(Exception.class)
    public CommonResult<Void> handleException(Exception e) {
        log.error("系统异常", e);
        return CommonResult.error("系统内部错误，请稍后重试");
    }
}
