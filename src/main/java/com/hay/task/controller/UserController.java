package com.hay.task.controller;

import com.hay.task.entity.FreeNomCookie;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

/**
 * @title: UserController
 * @Author HuangYan
 * @Date: 2021/10/12 15:01
 * @Version 1.0
 * @Description: ADD YOUR DESCRIPTION
 */
@RestController
public class UserController {

    @GetMapping("/user")
    public String test(){
        String freeNomCookie = FreeNomCookie.getFreeNomCookie();
        return LocalDateTime.now().toString().replace("T", " ") + "，Cookie:" + freeNomCookie;
    }

}
