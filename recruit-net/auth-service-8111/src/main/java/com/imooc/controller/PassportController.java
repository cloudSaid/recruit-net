package com.imooc.controller;

import com.imooc.base.BaseInfoProperties;
import com.imooc.grace.result.GraceJSONResult;
import com.imooc.grace.result.ResponseStatusEnum;
import com.imooc.pojo.Users;
import com.imooc.pojo.bo.RegistLoginBO;
import com.imooc.service.UsersService;
import com.imooc.utils.IPUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Leo
 * @version 1.0
 * @description: 发送短信服务
 * @date 2022-12-06 18:22
 */
@RestController
@RequestMapping("passport")
@Slf4j
public class PassportController extends BaseInfoProperties
{
        @Autowired
        private UsersService usersService;

    /**
     * 发送短信服务
     * @param mobile
     * @param servletRequest
     * @return
     */
    @GetMapping("getSMSCode")
    public GraceJSONResult getSMSCode(String mobile, HttpServletRequest servletRequest)
    {

        if (StringUtils.isBlank(mobile)){
            return GraceJSONResult.error();
        }
        //限制用户ip在60秒内只能发送一次
        String ip = IPUtil.getRequestIp(servletRequest);
        redis.setnx60s(MOBILE_SMSCODE + ":" + ip,mobile);

        log.info("手机号为" + mobile);
        //调用verificationCode获取验证码
        String numberCode = (int) ((Math.random() * 9 + 1) * 100000) + "";
        //调用第三方接口发送短信服务
        log.info("阿里云短信服务发送验证码" + numberCode);
        //存入redis
        redis.set(MOBILE_SMSCODE + ":" + mobile,numberCode,30*60);
        return GraceJSONResult.ok();
    }

    //用户登陆or注册
    @PostMapping("login")
    public GraceJSONResult login(@Validated @RequestBody RegistLoginBO registLoginBO,
                                 HttpServletRequest servletRequest) {

        String mobile = registLoginBO.getMobile();
        String smsCode = registLoginBO.getSmsCode();

        String redisSmsCode = redis.get(MOBILE_SMSCODE + ":" + mobile);
        if (StringUtils.isBlank(redisSmsCode) || StringUtils.isBlank(smsCode) || !smsCode.equalsIgnoreCase(redisSmsCode)){
            return GraceJSONResult.errorCustom(ResponseStatusEnum.SMS_CODE_ERROR);
        }

        Users userInfo = usersService.queryMobileIsExist(mobile);
        //为空则注册
        if (userInfo == null){
            usersService.createUsers(mobile);
        }

        redis.del(MOBILE_SMSCODE + ":" + mobile);

        return GraceJSONResult.ok(userInfo);

    }

}