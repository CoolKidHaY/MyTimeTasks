package com.hay.freenom.context;

/**
 * @title: AbstractBaseTask
 * @Author HuangYan
 * @Date: 2021/10/15 9:57
 * @Version 1.0
 * @Description: ADD YOUR DESCRIPTION
 */
public abstract class AbstractBaseTask<T> implements BaseTask<T>{
    @Override
    public String doLogin(String url, String username, String password) {
        return null;
    }

    @Override
    public abstract String getHtml(String url, String cookie);

    @Override
    public abstract T parseHtml(String body);
}
