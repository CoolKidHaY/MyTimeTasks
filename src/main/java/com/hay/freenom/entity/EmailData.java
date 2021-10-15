package com.hay.freenom.entity;

import lombok.Data;

/**
 * @title: EmailBaseData
 * @Author HuangYan
 * @Date: 2021/10/14 17:16
 * @Version 1.0
 * @Description: 邮件数据
 */
@Data
public class EmailData<T> {
    private T data;
}
