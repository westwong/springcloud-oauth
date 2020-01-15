package com.k2future.oauth2server.dao;

import com.k2future.oauth2server.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author West
 * @date create in 2019/9/2
 */
public interface UserDao extends JpaRepository<User, Long> {

    User findByUsername(String username);

    boolean existsByUsername(String username);

}
