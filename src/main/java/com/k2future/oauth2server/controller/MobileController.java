package com.k2future.oauth2server.controller;

import com.k2future.oauth2server.service.mobile.MobileService;
import com.k2future.oauth2server.service.mobile.SendMsgService;
import com.k2future.oauth2server.util.Assert;
import com.k2future.oauth2server.util.RespBuilder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;

/**
 * 手机验证相关
 *
 * @author West
 * @date create in 2019/11/4
 */
@RestController
@Slf4j
public class MobileController {

    @Autowired
    private MobileService mobileService;
    @Autowired
    private SendMsgService sendMsgService;

    @PostMapping("/mobile/code/verify")
    public Map<Object,Object> getMobileCode(@RequestParam String mobile,@RequestParam String code){
        boolean verifyMobile = mobileService.verifyMobile(mobile, code);
        Assert.isTrue(verifyMobile,"验证码错误！");
        return RespBuilder.succ();
    }
    @PostMapping("/mobile/code/get")
    public Map<Object,Object> getMobileCode(@RequestParam String mobile){
        //原来的
        String code = mobileService.currentCode(mobile);
        //如果不存在则重新生成 如果存在则使用以前的 只是刷新验证码时间
        if (code == null){
            code = mobileService.generatorCode(mobile);
        }else{
            mobileService.saveCode(mobile,code);
        }
        sendMsgService.sendVerificationCode(mobile,code);
        return RespBuilder.succ();
    }

}
