package com.k2future.oauth2server.config.security;


import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author West
 * @date create in 2019/7/3
 */
public class MyPasswordEncoder implements PasswordEncoder {

    /**
     * 自己选择一个或则自定义一个编码器。
     */
    private final static PasswordEncoder ENCODER = new BCryptPasswordEncoder();

    @Override
    public String encode(CharSequence charSequence) {
        return ENCODER.encode(charSequence);
    }

    @Override
    public boolean matches(CharSequence charSequence, String s) {
        return ENCODER.matches(charSequence,s);
    }
}
