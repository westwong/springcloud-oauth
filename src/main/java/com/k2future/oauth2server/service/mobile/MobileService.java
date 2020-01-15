package com.k2future.oauth2server.service.mobile;

import org.springframework.security.core.userdetails.UserDetails;

/**
 * @author West
 * @date create in 2019/11/5
 */
public interface MobileService {


    String MOBILE_CODE = "auth2:mobile_code:";

    long expire = 600; //默认过期时间

    /**
     * 验证电话号码
     * 如果不存在则创建账号
     * @param mobile
     * @param code
     * @return
     */
    UserDetails verifyMobileAndCreateIfNotPresent(String mobile, String code);
    /**
     * 验证电话号码
     * 如果不存在则创建账号
     * @param mobile
     * @param code
     * @return
     */
    boolean verifyMobile(String mobile, String code);

    /**
     * 生成验证码
     * @param mobile
     * @return
     */
    String  generatorCode(String mobile);

    /**
     * 获取当前验证码
     * @param mobile
     * @return
     */
    String currentCode(String mobile);

    /**
     * 保存验证码
     * @param key
     * @param code
     * @return
     */
    boolean saveCode(String key,String code);
}
