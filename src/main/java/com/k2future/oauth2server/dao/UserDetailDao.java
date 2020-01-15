package com.k2future.oauth2server.dao;

import com.k2future.oauth2server.entity.UserDetail;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author West
 * @date create in 2020/1/7
 */
public interface UserDetailDao extends JpaRepository<UserDetail, Long> {
}
