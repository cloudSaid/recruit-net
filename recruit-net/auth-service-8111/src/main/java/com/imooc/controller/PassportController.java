package com.imooc.controller;

import com.google.gson.Gson;
import com.imooc.base.BaseInfoProperties;
import com.imooc.grace.result.GraceJSONResult;
import com.imooc.grace.result.ResponseStatusEnum;
import com.imooc.pojo.Users;
import com.imooc.pojo.bo.RegistLoginBO;
import com.imooc.pojo.vo.UsersVO;
import com.imooc.service.UsersService;
import com.imooc.utils.IPUtil;
import com.imooc.utils.JWTUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

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

        @Autowired
        private JWTUtils jwtUtils;

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
            userInfo = usersService.createUsers(mobile);
        }

        /*String uToken = TOKEN_USER_PREFIX + SYMBOL_DOT + UUID.randomUUID().toString();
        redis.set(MOBILE_SMSCODE + ":" + userInfo.getId(),uToken);*/

        String userInfoJson = new Gson().toJson(userInfo);
        //创建jwt保存到redis并返回给前端
        String userToken = jwtUtils.createJWTWithPrefix(userInfoJson, Long.valueOf(1000000), TOKEN_USER_PREFIX);



        redis.del(MOBILE_SMSCODE + ":" + mobile);

        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(userInfo,usersVO);
        usersVO.setUserToken(userToken);

        return GraceJSONResult.ok(usersVO);

    }

    @PostMapping("logout")
    public GraceJSONResult login(@RequestParam String userId,
                                 HttpServletRequest servletRequest) {

        redis.del(MOBILE_SMSCODE + ":" + userId);

        return GraceJSONResult.ok();
    }


}
