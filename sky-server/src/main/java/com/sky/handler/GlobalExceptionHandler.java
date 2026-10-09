package com.sky.handler;

import com.sky.constant.MessageConstant;
import com.sky.exception.BaseException;
import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器，处理项目中抛出的业务异常。
 *
 * <p>Controller / Service 里直接 throw 业务异常即可，
 * 这里统一转成 Result 返回给前端，避免每个接口都写 try-catch。</p>
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获业务异常。
     *
     * <p>项目内所有业务异常都继承自 BaseException，
     * 因此只需拦截这一个父类型就能覆盖账号不存在、密码错误、账号被锁定等情况。</p>
     *
     * @param ex 业务异常对象，其 message 就是要返回给前端的提示语
     * @return 统一响应结果，code 为 0 表示失败
     */
    @ExceptionHandler
    public Result exceptionHandler(BaseException ex){
        log.error("异常信息：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    @ExceptionHandler
    public Result exceptionHandler(SQLIntegrityConstraintViolationException ex){
        log.error("异常信息：{}", ex.getMessage());
        String message = ex.getMessage();
      if (message.contains("Duplicate entry")){
        return Result.error(MessageConstant.DUPLICATE_ENTRY);
      }
      return Result.error(MessageConstant.UNKNOWN_ERROR);
    }
}
