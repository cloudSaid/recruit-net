package com.imooc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.imooc.pojo.Admin;


/**
 * <p>
 * 运营管理系统的admin账户表，仅登录，不提供注册 服务类
 * </p>
 *
 * @author
 * @since
 */
public interface AdminService extends IService<Admin> {

    /**
     * admin 登录
     * @param adminBO
     * @return
     */
    //public boolean adminLogin(AdminBO adminBO);

    /**
     * 获得admin信息
     * @param adminBO
     * @return
     */
    //public Admin getAdminInfo(AdminBO adminBO);
}
