package com.k2future.oauth2server.config.provider;

import com.k2future.oauth2server.common.constant.UsernameType;
import com.k2future.oauth2server.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 *
 * 签名登陆校验
 * @author  West
 * @date  create in 2020/1/8
 *
 **/
@Component
public class SignAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private UserService userService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        MobileAuthenticationToken mobileAuthenticationToken = (MobileAuthenticationToken) authentication;
        UserDetails user = userService.findByUsernameAndCreateIfAbsent((String) mobileAuthenticationToken.getPrincipal(), UsernameType.OPEN_ID);
        MobileAuthenticationToken authenticationToken = new MobileAuthenticationToken(user, user.getAuthorities());
        authenticationToken.setDetails(mobileAuthenticationToken.getDetails());
        return authenticationToken;
    }


    @Override
    public boolean supports(Class<?> authentication) {
        return MobileAuthenticationToken.class.isAssignableFrom(authentication);
    }
}