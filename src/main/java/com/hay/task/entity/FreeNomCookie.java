package com.hay.task.entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @title: FreeNomCookie
 * @Author HuangYan
 * @Date: 2021/9/26 17:12
 * @Version 1.0
 * @Description: 网址cookie对象
 */
public class FreeNomCookie {

    private final static Logger log = LoggerFactory.getLogger(FreeNomCookie.class);

    private static volatile String freeNomCookie;

    private static AtomicInteger version = new AtomicInteger(0);


    private FreeNomCookie() {
    }

    public static String getFreeNomCookie() {
        return freeNomCookie;
    }

    public static String setFreeNomCookie(String freeNomCookie1) {
        int i = version.get();
        if (version.compareAndSet(i, i+1)) {
            log.info("第{}次操作是更新Freenom网址的token", i+1);
            freeNomCookie = freeNomCookie1;
        }
        return freeNomCookie;
    }
}
