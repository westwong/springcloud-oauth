package com.k2future.oauth2server.service;

import com.k2future.oauth2server.common.constant.UsernameType;
import com.k2future.oauth2server.entity.User;


/**
 * @author West
 * @date create in 2019/9/2
 */
public interface UserService {
    /**
     * 保存用户信息
     * @param params 参数
     * @return user
     */
    User save(User params);

    /**
     * 修改用户信息
     * @param params 参数
     * @return user
     */
    User update(User params);

    /**
     * 保存或者修改
     * @param params 参数
     * @return 用户信息
     */
    User saveOrUpdate(User params);

    /**
     * 验证用户密码是否争取
     * @param userId 用户id
     * @param password 用户密码
     * @return 正确返回 true
     */
    boolean verifyPassword(Long userId, String password);

    /**
     * 根据id或者用户名查询用户 以id为主
     * @param id 用户id
     * @param username 用户名
     * @return 用户
     */
    User findByIdOrUsername(Long id, String username);

    /**
     * 删除用户信息
     * @param id 用户id
     */
    void deleteById(Long id);

    /**
     * 根据用户名查询用户信息 如果没有就创建一个
     * @param username 用户名
     * @param type 用户名类型 {@link UsernameType}
     * @return 用户账号信息
     */
    User findByUsernameAndCreateIfAbsent(String username,String type);
}
