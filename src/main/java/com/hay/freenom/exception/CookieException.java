package com.hay.freenom.exception;

/**
 * @title: CookieException
 * @Author HuangYan
 * @Date: 2021/9/29 10:26
 * @Version 1.0
 * @Description: Cookie失效异常
 */
public class CookieException extends BaseException {
    public CookieException() {
        super();
    }

    public CookieException(String message) {
        super(message);
    }
}
