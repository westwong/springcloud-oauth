package com.k2future.oauth2server.service;

import com.k2future.oauth2server.entity.Role;

import java.util.List;

/**
 * @author West
 * @date create in 2019/9/2
 */
public interface RoleService {

    List<Role> list();

    Role findByRoleNameAndServiceId(String roleName,String serviceId);
}
