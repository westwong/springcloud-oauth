package com.k2future.oauth2server.service.mobile;

import com.k2future.oauth2server.util.Assert;
import com.k2future.oauth2server.common.constant.UsernameType;
import com.k2future.oauth2server.config.redis.RedisUtil;
import com.k2future.oauth2server.service.UserService;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

/**
 * @author West
 * @date create in 2019/11/5
 *
 *  短信登录 验证相关
 *
 */
@Service("mobileService")
public class MobileServiceImpl implements MobileService {

    @Autowired
    private UserService userService;
    @Autowired
    private RedisUtil redisUtil;

    @Override
    public UserDetails verifyMobileAndCreateIfNotPresent(String mobile, String code) {

        boolean verify = verifyMobile(mobile, code);
        Assert.isTrue(verify,"验证码错误！");
        return userService.findByUsernameAndCreateIfAbsent(mobile, UsernameType.PHONE);
    }

    @Override
    public boolean verifyMobile(String mobile, String code) {
        Assert.hasText(code,"验证码为空");
        Assert.hasText(mobile,"手机好为空");

        //获取当前code
        String dbCode = currentCode(mobile);

        if (dbCode == null){
            return false;
        }
        if (!StringUtils.equals(dbCode,code)){
            return false;
        }
        return true;
    }

    @Override
    public String currentCode(String mobile) {
        Object o = redisUtil.get(MOBILE_CODE + mobile);
        if (o == null){
            return null;
        }
        return o.toString();
    }

    @Override
    public synchronized String generatorCode(String mobile) {
        Assert.hasText(mobile,"电话号码为空");
        Assert.isTrue(mobile.length() > 10,"请输入11位手机号");
        String code = (int) ((Math.random() * 9 + 1) * 100000)+"";
        saveCode(mobile,code);
        return code;
    }

    @Override
    public boolean saveCode(String key, String code) {
        return redisUtil.set(MOBILE_CODE + key,code,expire);
    }
}
