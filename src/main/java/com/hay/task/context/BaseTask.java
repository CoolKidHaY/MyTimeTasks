package com.hay.task.context;

import com.hay.task.exception.BaseException;

/**
 * @title: BaseTask
 * @Author HuangYan
 * @Date: 2021/10/15 9:45
 * @Version 1.0
 * @Description: 获取数据接口
 */
public interface BaseTask<T> {

    /**
     * 获取数据
     * @return
     * @throws BaseException
     */
    T getData() throws BaseException;
    /**
     * 登陆
     * @param url
     * @param username
     * @param password
     * @return
     */
    String doLogin(String url, String username, String password) throws BaseException;

    /**
     * 获取html页面
     * @param url
     * @param cookie
     * @return
     */
    String getHtml(String url, String cookie) throws BaseException;

    /**
     * 解析html
     * @param body
     * @return
     */
    T parseHtml(String body) throws BaseException;
}