package com.k2future.oauth2server.config.provider;

import com.k2future.oauth2server.service.mobile.MobileService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * 手机号登录校验逻辑
 */
@Component
public class MobileAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private MobileService mobileService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        MobileAuthenticationToken mobileAuthenticationToken = (MobileAuthenticationToken) authentication;
        UserDetails user = mobileService.verifyMobileAndCreateIfNotPresent((String) mobileAuthenticationToken.getPrincipal(),mobileAuthenticationToken.getCode());
        if (user == null) {
            throw new InternalAuthenticationServiceException("手机号不存在:" + mobileAuthenticationToken.getPrincipal());
        }
        MobileAuthenticationToken authenticationToken = new MobileAuthenticationToken(user, user.getAuthorities());
        authenticationToken.setDetails(mobileAuthenticationToken.getDetails());
        return authenticationToken;
    }


    @Override
    public boolean supports(Class<?> authentication) {
        return MobileAuthenticationToken.class.isAssignableFrom(authentication);
    }
}