package com.k2future.oauth2server.service;

import com.k2future.oauth2server.entity.UserDetail;

/**
 * @author West
 * @date create in 2020/1/7
 */
public interface UserDetailService {
    /**
     * 根据id查询用户详情 如果不存在则初始化一个
     * @param id 用户id
     * @return user
     */
    UserDetail getByIdAndCreateIfAbsent(Long id);
    /**
     * 根据id查询用户详情
     * @param id 用户id
     * @return user 不存在返回 null
     */
    UserDetail getById(Long id);

    /**
     * 修改用户详情
     * @param params params.id 不能为空
     */
    void updateById(UserDetail params);
}
