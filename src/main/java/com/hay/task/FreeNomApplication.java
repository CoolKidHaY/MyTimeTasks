package com.hay.task;

import com.alibaba.nacos.api.config.ConfigType;
import com.alibaba.nacos.spring.context.annotation.config.NacosPropertySource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @title: FreeNomApplication
 * @Author HuangYan
 * @Date: 2021/9/26 16:53
 * @Version 1.0
 * @Description: ADD YOUR DESCRIPTION
 */
@NacosPropertySource(dataId = "Freenom-dev.yml",groupId = "myProject", autoRefreshed = true, type = ConfigType.YAML)
@SpringBootApplication
@EnableScheduling
public class FreeNomApplication {

    public static void main(String[] args) {
        SpringApplication.run(FreeNomApplication.class, args);
    }
}
