package com.k2future.oauth2server.dao;

import com.k2future.oauth2server.entity.Role;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author West
 * @date create in 2019/9/2
 */
public interface RoleDao extends JpaRepository<Role, Long> {

    Role findByRoleNameAndServiceId(String roleName, String serviceId);
}
