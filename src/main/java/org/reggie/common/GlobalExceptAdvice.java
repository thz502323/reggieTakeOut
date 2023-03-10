package org.reggie.common;

import java.sql.SQLIntegrityConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.reggie.exception.BusinessException;
import org.reggie.exception.SystemException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author : 伪中二
 * @Date : 2023/2/17
 * @Description :处理异常
 */
@RestControllerAdvice//注解这个类是做异常处理的采用Rest风格，后面可以利用AOP思想处理类的异常
@Slf4j
public class GlobalExceptAdvice {

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)//注解这个方法是用来处理BusinessException异常类的
    Result<String> doSQLIntegrityConstraintViolationException(SQLIntegrityConstraintViolationException exception){
        log.error("出现错误: {}",exception.getMessage());
        return  Result.error("出错了");
    }

    @ExceptionHandler(BusinessException.class)//注解这个方法是用来处理BusinessException异常类的
    Result<String> doBusinessException(BusinessException exception){
        log.error("出现错误: {}",exception.getMessage());
        return  Result.error(exception.getMessage());
    }

    /**
     * 出现这个异常
     * 1.记录日志
     * 2.发信息给运维和开发人员
     * 3.返回安慰信息给用户
     */
    @ExceptionHandler(SystemException.class)//注解这个方法是用来处理SystemException异常类的
    Result<String> doSystemException(SystemException exception){
        log.error("出现错误: {}",exception.getMessage());
        return  Result.error("系统出现异常，处理中！");
    }

    @ExceptionHandler(Exception.class)//注解这个方法是用来处理其他未被预料的异常类
    Result<String> doException(Exception exception){
        log.error("出现错误: {}",exception.getMessage());
        return  Result.error("未知异常，请重试");
    }
}
