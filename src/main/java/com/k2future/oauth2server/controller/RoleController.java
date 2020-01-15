package com.k2future.oauth2server.controller;

import com.k2future.oauth2server.service.RoleService;
import com.k2future.oauth2server.util.RespBuilder;
import com.k2future.oauth2server.entity.Role;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author West
 * @date create in 2019/9/3
 */
@RestController
@RequestMapping("/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;


    @PostMapping("/list")
    @PreAuthorize("hasRole('admin')")
    public Map<Object, Object> list() {
        List<Role> list = roleService.list();
        return RespBuilder.kv2Json(list);
    }


}
