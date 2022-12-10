package com.imooc.controller;

import com.google.gson.Gson;
import com.imooc.api.Intercept.CurrentUserInterceptor;
import com.imooc.base.BaseInfoProperties;
import com.imooc.grace.result.GraceJSONResult;
import com.imooc.grace.result.ResponseStatusEnum;
import com.imooc.pojo.Admin;
import com.imooc.pojo.Users;
import com.imooc.pojo.ar.AdminAR;
import com.imooc.pojo.bo.AdminBO;
import com.imooc.pojo.bo.RegistLoginBO;
import com.imooc.pojo.vo.AdminVO;
import com.imooc.pojo.vo.UsersVO;
import com.imooc.service.AdminService;
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

/**
 * @author Leo
 * @version 1.0
 * @description: admin端用户登录
 * @date 2022-12-06 18:22
 */
@RestController
@RequestMapping("admin")
@Slf4j
public class AdminController extends BaseInfoProperties
{
    @Autowired
    private AdminService adminService;

    @Autowired
    private JWTUtils jwtUtils;

    @PostMapping("login")
    public GraceJSONResult login(@Validated @RequestBody AdminBO adminBO)
    {

        boolean isCorrect = adminService.adminLogin(adminBO);
        //验证不通过
        if (!isCorrect){
        return GraceJSONResult.errorCustom(ResponseStatusEnum.ADMIN_LOGIN_ERROR);
        }
        //获取admin信息
        Admin adminInfo = adminService.getAdminInfo(adminBO);

        AdminVO adminBo = new AdminVO();
        BeanUtils.copyProperties(adminInfo,adminBo);

        //生成长久token
        String adminToken = jwtUtils.createJWTWithPrefix(new Gson().toJson(adminBo), TOKEN_ADMIN_PREFIX);

        return GraceJSONResult.ok(adminToken);

    }

    @PostMapping("logout")
    public GraceJSONResult logout()
    {
        return GraceJSONResult.ok();
    }


    @GetMapping("info")
    public GraceJSONResult getAdminInfo()
    {
        Admin admin = CurrentUserInterceptor.adminUser.get();

        AdminVO adminVO = new AdminVO();

        BeanUtils.copyProperties(admin,adminVO);

        return GraceJSONResult.ok(adminVO);

    }


}
