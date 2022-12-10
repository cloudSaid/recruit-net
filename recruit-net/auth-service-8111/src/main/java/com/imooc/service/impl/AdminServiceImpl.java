package com.imooc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.imooc.mapper.AdminMapper;
import com.imooc.pojo.Admin;
import com.imooc.pojo.bo.AdminBO;
import com.imooc.service.AdminService;
import com.imooc.utils.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Leo
 * @version 1.0
 * @description: TODO
 * @date 2022-12-10 18:14
 */
@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper,Admin> implements AdminService {

    @Autowired
    private AdminMapper adminMapper;


    /**
     * admin用户登陆
     * @param adminBO
     * @return
     */
    @Override
    public boolean adminLogin(AdminBO adminBO) {

        Admin adminInfo = selectAdminInfoByUsername(adminBO);

        if (adminInfo == null){
            return false;
        }

        String slat = adminInfo.getSlat();
        String encrypt = MD5Utils.encrypt(adminBO.getPassword(), slat);
        if (encrypt.equalsIgnoreCase(adminInfo.getPassword())){
            return true;
        }

        return false;
    }

    /**
     * 查询admin用户登录信息
     * @param adminBO
     * @return
     */
    @Override
    public Admin getAdminInfo(AdminBO adminBO) {
        return selectAdminInfoByUsername(adminBO);
    }


    public Admin selectAdminInfoByUsername(AdminBO adminBO){
        return adminMapper.selectOne(new QueryWrapper<Admin>()
                .eq("username", adminBO.getUsername())
        );
    }



}
