package com.imooc.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Leo
 * @version 1.0
 * @description: TODO
 * @date 2022-12-06 18:03
 */
@RestController
@RequestMapping("/a")
public class AuthController {

    @RequestMapping("/authTest")
    public String authTest(){
        return "auth runing";
    }

}
