package com.mashibing.serviceverificationcode.controller;


import com.imooc.grace.result.GraceJSONResult;
import com.imooc.grace.result.NumberCodeResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Leo
 * @version 1.0
 * @description: TODO
 * @date 2022/12/1 15:31
 */
@RestController
public class NumberCodeController {

    @RequestMapping("/numberCode/{size}")
    public GraceJSONResult numberCode(@PathVariable("size") int size){
        System.out.println("size:" + size);
        //生成验证码
        int numberCode = (int) ((Math.random() * 9 + 1) * (Math.pow(10, size - 1)));
        NumberCodeResponse response = new NumberCodeResponse();
        response.setNumberCode(numberCode);
        return GraceJSONResult.ok(response);
    }

}
