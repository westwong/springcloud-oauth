package com.k2future.oauth2server.controller;

import com.k2future.oauth2server.util.Assert;
import com.k2future.oauth2server.util.BeanUtils;
import com.k2future.oauth2server.util.Oauth2UserUtil;
import com.k2future.oauth2server.util.RespBuilder;
import com.k2future.oauth2server.entity.UserDetail;
import com.k2future.oauth2server.service.UserDetailService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;

/**
 * @author West
 * @date create in 2020/1/7
 */
@RestController
@Slf4j
public class UserDetailController {

    @Autowired
    private UserDetailService userDetailService;

    /**
     * 获取用户详情
     * @param dto id
     * @return 用户详情
     */
    @PostMapping("/userDetail/get")
    public Map<Object,Object> get(@RequestBody UserDetail dto){
        UserDetail user = userDetailService.getById(dto.getId());
        Assert.notNull(user,"id invalid");
       return RespBuilder.kv2Json(user);
    }
    /**
     * 获取当前用户详情
     * @return 用户详情
     */
    @PostMapping("/userDetail/current")
    public Map<Object,Object> current(){
        UserDetail user = userDetailService.getByIdAndCreateIfAbsent(Oauth2UserUtil.currUserId());
       return RespBuilder.kv2Json(user);
    }

    /**
     * 修改用户详情
     * @param dto userDetail
     * @return 修改成功
     */
    @PostMapping("/userDetail/update")
    public Map<Object,Object> update(@RequestBody UserDetail dto){
        Assert.notNull(dto.getId(),"id null ?");
        UserDetail db = userDetailService.getById(dto.getId());
        Assert.notNull(db,"id invalid !");
        BeanUtils.copyBeanIgnoreNull(dto,db);
        userDetailService.updateById(db);
        return RespBuilder.succ();
    }
}
