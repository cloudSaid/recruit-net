package com.imooc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.imooc.pojo.Users;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author
 * @since
 */
public interface UsersService extends IService<Users> {

    /**
     * 判断用户是否存在，如果存在则返回用户信息，否则null
     * @param mobile
     * @return
     */
     Users queryMobileIsExist(String mobile);

    /**
     * 创建用户信息，并且返回用户对象
     * @param mobile
     * @return
     */
    Users createUsers(String mobile);
}
