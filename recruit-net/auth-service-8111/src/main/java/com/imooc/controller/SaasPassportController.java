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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author Leo
 * @version 1.0
 * @description: 生成saas登陆二维码
 * @date 2022-12-06 18:22
 */
@RestController
@RequestMapping("saas")
@Slf4j
public class SaasPassportController extends BaseInfoProperties
{
    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private UsersService usersService;


    @PostMapping("getQRToken")
    public GraceJSONResult getQRToken()
    {
        //生成二维码唯一id
        String qRCodeId = UUID.randomUUID().toString();
        //存入redis
        redis.set(SAAS_PLATFORM_LOGIN_TOKEN + ":" + qRCodeId,qRCodeId,5*60);
        //在redis中标记改二维码未被读取
        redis.set(SAAS_PLATFORM_LOGIN_TOKEN_READ + ":" + qRCodeId,"0",5*60);
        //返回二维码token
        return GraceJSONResult.ok(qRCodeId);
    }


        @PostMapping("scanCode")
        public GraceJSONResult scanCode(String qrToken,HttpServletRequest servletRequest)
        {
            String qrTokenValue = redis.get(SAAS_PLATFORM_LOGIN_TOKEN + ":" + qrToken);

            if (StringUtils.isBlank(qrToken) || StringUtils.isBlank(qrTokenValue)
                || !qrToken.equalsIgnoreCase(qrTokenValue)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.FAILED);
        }
        //获取前端携带的额userInfo
        String appUserId = servletRequest.getHeader("appUserId");
        String appUserToken = servletRequest.getHeader("appUserToken");

        if (StringUtils.isBlank(appUserId) || StringUtils.isBlank(appUserToken)){
            return GraceJSONResult.errorCustom(ResponseStatusEnum.HR_TICKET_INVALID);
        }

        String userInfo = jwtUtils.checkJWT(appUserToken.split("@")[1]);

        if (StringUtils.isBlank(userInfo)){
            return GraceJSONResult.errorCustom(ResponseStatusEnum.HR_TICKET_INVALID);
        }
        //生成预登陆token并覆盖原来的token并返回
        String getSetToken = UUID.randomUUID().toString();

        redis.set(SAAS_PLATFORM_LOGIN_TOKEN + ":" + qrToken,getSetToken,5*60);
        //在redis中标记改二维码未被读取
        redis.set(SAAS_PLATFORM_LOGIN_TOKEN_READ + ":" + qrToken,
                "0," + getSetToken,5*60);

        return GraceJSONResult.ok(getSetToken);
    }



    //前端轮询请求该接口判断二维码是否已被扫
    @PostMapping("codeHasBeenRead")
    public GraceJSONResult getSetToken(StringUtils qrToken)
    {
        //从redis中取得坚定的额值
        String appraisal = redis.get(SAAS_PLATFORM_LOGIN_TOKEN_READ + ":" + qrToken);

        //包装成list返回
        List appraisalArr = new ArrayList<>();

        if (StringUtils.isNotBlank(appraisal)){
            String[] split = appraisal.split(",");
            if (split.length >= 2){
                appraisalArr.add(Integer.valueOf(split[0]));
                appraisalArr.add(split[2]);
            }else {
                appraisalArr.add(0);
            }
            return GraceJSONResult.ok(appraisalArr);
        }
        return GraceJSONResult.ok(appraisalArr);
    }

    /**
     * 确定登录请求
     * @param userId
     * @param qrToken
     * @param preToken
     * @return
     */
    @PostMapping("goQRLogin")
    public GraceJSONResult goQRLogin(String userId, String qrToken, String preToken)
    {
        String redisPreToken = redis.get(SAAS_PLATFORM_LOGIN_TOKEN + ":" + qrToken);

        if (StringUtils.isBlank(redisPreToken)
                || StringUtils.isBlank(preToken)
                || !redisPreToken.equalsIgnoreCase(preToken)){
            return GraceJSONResult.errorCustom(ResponseStatusEnum.HR_TICKET_INVALID);
        }

        Users userInfo = usersService.getById(userId);
        if (userInfo == null){
            return GraceJSONResult.errorCustom(ResponseStatusEnum.USER_NOT_EXIST_ERROR);
        }

        String userInfoJson = new Gson().toJson(userInfo);

        //因为h5在未登录时拿不到用户id所以将用户信息暂存到redis
        redis.set(REDIS_SAAS_USER_INFO + ":temp:" + preToken,userInfoJson,5*60);


        return GraceJSONResult.ok();
    }

    /**
     * 登陆hr端并保存用户信息
     * @param preToken
     * @return
     */
    @PostMapping("checkLogin")
    public GraceJSONResult checkLogin(String preToken)
    {
        if (StringUtils.isBlank(preToken)){
            return GraceJSONResult.error();
        }
        String userInfo = redis.get(REDIS_SAAS_USER_INFO + ":temp:" + preToken);
        if (userInfo == null){
            return GraceJSONResult.errorCustom(ResponseStatusEnum.HR_TICKET_INVALID);
        }
        redis.del(REDIS_SAAS_USER_INFO + ":temp:" + preToken);

        //生成用户token长期有效
        String userToken = jwtUtils.createJWTWithPrefix(userInfo, TOKEN_SAAS_PREFIX);
        //存入redis
        redis.set(REDIS_SAAS_USER_INFO + ":" + userToken,userInfo);
        //返回jwt
        return GraceJSONResult.ok(userToken);
    }

    /**
     * 前端请求用户信息
     * @param token
     * @return
     */
    @GetMapping("info")
    public GraceJSONResult info(String token)
    {
        if (StringUtils.isBlank(token)){
            return GraceJSONResult.errorCustom(ResponseStatusEnum.HR_TICKET_INVALID);
        }
        Users userInfo = new Gson().fromJson(token, Users.class);
        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(userInfo,usersVO);

        return GraceJSONResult.ok(usersVO);

    }

    @GetMapping("logout")
    public GraceJSONResult logout(String token){


        return GraceJSONResult.ok();
    }

}
