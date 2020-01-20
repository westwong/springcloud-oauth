package com.k2future.oauth2server.controller;

import com.k2future.oauth2server.service.RoleService;
import com.k2future.oauth2server.util.Assert;
import com.k2future.oauth2server.util.Oauth2UserUtil;
import com.k2future.oauth2server.util.RespBuilder;
import com.k2future.oauth2server.entity.Role;
import com.k2future.oauth2server.entity.User;
import com.k2future.oauth2server.entity.UserPwd;
import com.k2future.oauth2server.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;

    @RequestMapping(value = "/current", method = RequestMethod.GET)
    public Principal getUser(Principal principal) {
        return principal;
    }


    @PostMapping("/role/update")
    @Transactional(rollbackFor = Exception.class)
    public Map<Object, Object> roleUpdate(@RequestBody User params) {
        params.setPassword(null);
        String clientId = Oauth2UserUtil.currClientId();
        List<Role> roleList = params.getRoleList();
        //支持用角色名修改角色
        if (roleList != null){
            roleList.forEach(role -> {
               if (role.getId() != null) {
                   return;
               }
                Role db = roleService.findByRoleNameAndServiceId(role.getRoleName(), clientId);
               if (db != null) {
                   role.setId(db.getId());
               }
            });
        }
        userService.update(params);
        return RespBuilder.succ();
    }

    @PostMapping("/pwd/update")
    @Transactional(rollbackFor = Exception.class)
    public Map<Object, Object> password(@RequestBody UserPwd params) {

        Assert.isTrue(userService.verifyPassword(params.userId, params.oldPassword), "password is incorrect");

        userService.update(new User().setId(params.userId).setPassword(params.newPassword));
        return RespBuilder.succ();
    }

    @PostMapping("/update")
    @Transactional(rollbackFor = Exception.class)
    public Map<Object, Object> update(@RequestBody User params) {
        params.setPassword(null);
        params.setRoleList(null);
        userService.update(params);
        return RespBuilder.succ();
    }

    @PostMapping("/save")
    @Transactional(rollbackFor = Exception.class)
    public Map<Object, Object> save(@RequestBody User params) {
        List<Role> roleList = params.getRoleList();
        if (roleList != null) {
            Set<Role> dbRoles = new HashSet<>(roleList.size());
            String clientId = Oauth2UserUtil.currClientId();
            for (Role role : roleList) {
                Role db = roleService.findByRoleNameAndServiceId(role.getRoleName(), clientId);
                Assert.notNull(db, "role isn't exist !");
                dbRoles.add(db);
            }
            params.setRoleList(new ArrayList<>(dbRoles));
        }
        User save = userService.save(params);
        return RespBuilder.kv2Json(save.getId());
    }

    @PostMapping("/register")
    @Transactional(rollbackFor = Exception.class)
    public Map<Object, Object> register(@RequestBody User params) {
        params. setRoleList(null);
        return this.save(params);
    }



    @PostMapping("/find")
    public Map<Object,Object> find(@RequestBody User user){
        User db = userService.findByIdOrUsername(user.getId(),user.getUsername());
        return RespBuilder.kv2Json(db);
    }
    @PostMapping("/delete")
    @Transactional(rollbackFor = Exception.class)
    public Map<Object,Object> delete(@RequestBody User user){
        userService.deleteById(user.getId());
        return RespBuilder.succ();
    }
}