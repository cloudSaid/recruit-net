package com.imooc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.imooc.api.Intercept.CurrentUserInterceptor;
import com.imooc.base.BaseInfoProperties;
import com.imooc.exceptions.GraceException;
import com.imooc.grace.result.ResponseStatusEnum;
import com.imooc.mapper.AdminMapper;
import com.imooc.pojo.Admin;
import com.imooc.pojo.bo.AdminBO;
import com.imooc.pojo.bo.CreateAdminBO;
import com.imooc.service.AdminService;
import com.imooc.utils.MD5Utils;
import com.imooc.utils.PagedGridResult;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * @author Leo
 * @version 1.0
 * @description: TODO
 * @date 2022-12-10 18:14
 */
@Service
public class AdminServiceImpl extends BaseInfoProperties implements AdminService {

    @Autowired
    private AdminMapper adminMapper;

    private final String USER_FACE1= "http://43.143.1.156:8889/group1/M00/00/00/CgAEAmNrKj6AU-cSAA7_C7k2oNQ011.png";


    /**
     * 创建admin账户
     * @param createAdminBO
     */
    @Override
    public void createAdmin(CreateAdminBO createAdminBO)
    {
        Admin admin = CurrentUserInterceptor.adminUser.get();
        if (admin == null) GraceException.display(ResponseStatusEnum.ADMIN_NOT_EXIST);

        Admin createAdmin = selectAdminInfoByUsername(createAdminBO.getUsername());

        if (createAdmin != null) GraceException.display(ResponseStatusEnum.ADMIN_USERNAME_EXIST_ERROR);

        Admin createAdminInfo = new Admin();
        BeanUtils.copyProperties(createAdminBO,createAdminInfo);
        String slat = UUID.randomUUID().toString();
        createAdminInfo.setPassword(MD5Utils.encrypt(createAdminInfo.getPassword(),slat));
        createAdminInfo.setSlat(slat);
        LocalDateTime now = LocalDateTime.now();
        createAdminInfo.setCreateTime(now);
        createAdminInfo.setUpdatedTime(now);
        createAdminInfo.setFace(USER_FACE1);

        adminMapper.insert(createAdminInfo);

    }

    @Override
    public PagedGridResult getAminList(String accountName, Integer page, Integer limit) {

        PageHelper.startPage(page,limit);

        List<Admin> adminInfoList = adminMapper.selectList(new QueryWrapper<Admin>()
                .eq("username", accountName));

        return setterPagedGrid(adminInfoList,page);
    }

    public Admin selectAdminInfoByUsername(String adminUsername){
        return adminMapper.selectOne(new QueryWrapper<Admin>()
                .eq("username", adminUsername));
    }
}
