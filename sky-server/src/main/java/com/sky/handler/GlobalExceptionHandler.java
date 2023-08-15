package com.sky.handler;

import com.sky.constant.MessageConstant;
import com.sky.exception.BaseException;
import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器，处理项目中抛出的业务异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    /**
     * 捕获业务异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler (BaseException ex) {
        log.error ("异常信息：{}", ex.getMessage ());
        return Result.error (ex.getMessage ());
    }
    
    /**
     * 用戶名字段被設置爲唯一，重複時會抛出異常
     *
     * @param exception
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler (SQLIntegrityConstraintViolationException exception) {
        //  java.sql.SQLIntegrityConstraintViolationException: Duplicate entry '李四' for key 'employee.idx_username'
        String message = exception.getMessage ();
        if (message.contains ("Duplicate entry")) {
            // 確認是因爲用戶名重複導致的異常
            String[] split = message.split (" ");
            String username = split[2];
            String msg = username + MessageConstant.ALREADY_EXISTS;
            return Result.error (msg);
        } else {
            return Result.error (MessageConstant.UNKNOWN_ERROR);
        }
    }
    
}
