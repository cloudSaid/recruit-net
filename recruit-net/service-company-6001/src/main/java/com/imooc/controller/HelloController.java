package com.imooc.controller;

import com.imooc.api.Intercept.CurrentUserInterceptor;
import com.imooc.pojo.Users;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("c")
public class HelloController {

    @GetMapping("hello")
    public Object hello() {

        ThreadLocal objectThreadLocal = new ThreadLocal<>();
        System.out.println(objectThreadLocal.get());


        return "Hello CompanyService~~~";
    }

}
