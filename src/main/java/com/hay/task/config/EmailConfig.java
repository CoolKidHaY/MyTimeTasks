package com.hay.task.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @title: EmailConfig
 * @Author HuangYan
 * @Date: 2021/9/29 14:05
 * @Version 1.0
 * @Description: 邮件配置
 */
@Configuration
@ConfigurationProperties(prefix = "freenom.email")
@Data
public class EmailConfig {
    private String subject;
    private String from;
    private String to;
    private String[] cc;
}
