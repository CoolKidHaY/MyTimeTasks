package com.hay.freenom.entity;

import lombok.Data;

import java.time.LocalDate;

/**
 * @title: Domain
 * @Author HuangYan
 * @Date: 2021/9/28 16:50
 * @Version 1.0
 * @Description: 域名对象
 */
@Data
public class Domain {
    private String name;
    private String status;

    /**
     * 注册的日期
     */
    private LocalDate registerDate;

    /**
     * 剩下的使用时间
     */
    private Integer expirationLastDay;

    /**
     * 过期的日期
     */
    private LocalDate expirationDate;

    /**
     * 续约状态（到期前14天才能开启续约）: 1=>能够续约 0=>不能续约
     */
    private Integer renewStatus;

    /**
     * 续约开启剩下天数
     */
    private Integer renewOpenDay;

    /**
     * 续约开启时间
     */
    private LocalDate renewDate;

    /**
     * 续约网址
     */
    private String renewUrl;
}
