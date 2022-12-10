package com.imooc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.imooc.pojo.Admin;
import com.imooc.pojo.bo.AdminBO;
import com.imooc.pojo.bo.CreateAdminBO;


/**
 * <p>
 * 运营管理系统的admin账户表，仅登录，不提供注册 服务类
 * </p>
 *
 * @author Leo
 * @since
 */
public interface AdminService extends IService<Admin> {

    /**
     * admin 注册
     * @param
     * @return
     */

    public void createAdmin(CreateAdminBO createAdminBO);

}
