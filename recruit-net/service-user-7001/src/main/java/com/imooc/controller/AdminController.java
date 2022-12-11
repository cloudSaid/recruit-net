package com.imooc.controller;

import com.google.gson.Gson;
import com.imooc.api.Intercept.CurrentUserInterceptor;
import com.imooc.base.BaseInfoProperties;
import com.imooc.grace.result.GraceJSONResult;
import com.imooc.grace.result.ResponseStatusEnum;
import com.imooc.pojo.Admin;
import com.imooc.pojo.bo.AdminBO;
import com.imooc.pojo.bo.CreateAdminBO;
import com.imooc.pojo.vo.AdminVO;
import com.imooc.service.AdminService;
import com.imooc.utils.JWTUtils;
import com.imooc.utils.PagedGridResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author Leo
 * @version 1.0
 * @description: admin端用户登录
 * @date 2022-12-06 18:22
 */
@RestController
@RequestMapping("admininfo")
@Slf4j
public class AdminController extends BaseInfoProperties
{
    @Autowired
    private AdminService adminService;



    @PostMapping("create")
    public GraceJSONResult login(@Validated @RequestBody CreateAdminBO adminBO)
    {
        adminService.createAdmin(adminBO);

        return GraceJSONResult.ok();
    }

    @PostMapping("list")
    public PagedGridResult list(String accountName,Integer page,Integer limit){

        if (page == null) page = 1;
        if (limit == null) limit = 10;

        return adminService.getAminList(accountName, page, limit);
    }



}
