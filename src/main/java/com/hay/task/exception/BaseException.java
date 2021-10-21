package com.hay.task.exception;

/**
 * @title: CookieException
 * @Author HuangYan
 * @Date: 2021/9/29 10:18
 * @Version 1.0
 * @Description: 基础异常
 */
public class BaseException extends RuntimeException {
    public BaseException() {
        super();
    }

    public BaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public BaseException(String message) {
        super(message);
    }

    public BaseException(Throwable cause) {
        super(cause);
    }
}
