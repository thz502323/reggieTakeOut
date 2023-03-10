package org.reggie.exception;

/**
 * @author : 伪中二
 * @Date : 2023/2/19
 * @Description :用于处理业务异常，比如用户非法传值
 */
public class BusinessException extends RuntimeException {//继承运行时异常，遇到这个异常会自动抛出
    private Integer exceptionCode;

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Integer exceptionCode) {
        super(message);
        this.exceptionCode = exceptionCode;
    }

    public BusinessException(String message, Throwable cause, Integer exceptionCode) {
        super(message, cause);
        this.exceptionCode = exceptionCode;
    }

    public Integer getExceptionCode() {
        return exceptionCode;
    }
}
