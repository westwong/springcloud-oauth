package com.k2future.oauth2server.service;

import com.k2future.oauth2server.dao.RoleDao;
import com.k2future.oauth2server.entity.Role;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author West
 * @date create in 2019/9/3
 */
@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleDao roleDao;

    @Override
    public List<Role> list() {
        return roleDao.findAll();
    }

    @Override
    public Role findByRoleNameAndServiceId(String roleName, String serviceId) {
        return roleDao.findByRoleNameAndServiceId(roleName,serviceId);
    }
}
