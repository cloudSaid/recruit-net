package com.imooc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.imooc.api.Intercept.InterceptorConfigs.InterceptorConfig;
import com.imooc.pojo.Admin;
import com.imooc.pojo.bo.AdminBO;
import com.imooc.pojo.bo.CreateAdminBO;
import com.imooc.utils.PagedGridResult;


/**
 * <p>
 * 运营管理系统的admin账户表，仅登录，不提供注册 服务类
 * </p>
 *
 * @author Leo
 * @since
 */
public interface AdminService {

    /**
     * admin 注册
     * @param
     * @return
     */

    public void createAdmin(CreateAdminBO createAdminBO);


    /**
     * admin账户列表分页
     * @param accountName
     * @param page
     * @param limit
     * @return
     */
    public PagedGridResult getAminList(String accountName, Integer page, Integer limit);

}
