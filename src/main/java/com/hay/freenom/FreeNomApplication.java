package com.hay.freenom;

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
@SpringBootApplication
@EnableScheduling
public class FreeNomApplication {

    public static void main(String[] args) {
        SpringApplication.run(FreeNomApplication.class, args);
    }
}
