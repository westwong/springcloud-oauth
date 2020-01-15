package com.k2future.oauth2server.service.mobile;

/**
 * @author West
 * @date create in 2019/11/5
 */
public interface SendMsgService {
    /**
     * 手机发送验证码
     * @param mobile 手机号码
     * @param code 验证码
     * @return 是否成功
     */
    boolean sendVerificationCode(String mobile,String code);
}
