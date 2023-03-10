package org.reggie.exception;

/**
 * @author : 伪中二
 * @Date : 2023/2/19
 * @Description :用于处理系统异常，比如数据库宕机，环境出错
 */
public class SystemException extends RuntimeException {//继承运行时异常，遇到这个异常会自动抛出
    private Integer exceptionCode;

    public SystemException(String message, Integer exceptionCode) {
        super(message);
        this.exceptionCode = exceptionCode;
    }

    public SystemException(String message, Throwable cause, Integer exceptionCode) {
        super(message, cause);
        this.exceptionCode = exceptionCode;
    }

    public Integer getExceptionCode() {
        return exceptionCode;
    }
}
