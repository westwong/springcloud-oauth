package com.k2future.oauth2server.util;


import com.k2future.oauth2server.entity.User;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import java.util.ArrayList;
import java.util.List;

/**
 * @author West
 * @date create in 2019/9/16
 */
public class Oauth2UserUtil {



    private static OAuth2Authentication currSecurityUser() {
            OAuth2Authentication authentication = (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();
        return  authentication;
    }

    public static Long currUserId(){
        try {
            User user = (User)currSecurityUser().getUserAuthentication().getPrincipal();
            return user.getId();
        } catch (Exception e) {
            Assert.throwE("please login");
        }
        return  null;
    }

    public static boolean currUserHasRole(String role) {
        role = "ROLE_" + role;
        for (GrantedAuthority authority : currSecurityUser().getAuthorities()) {
            if (authority.getAuthority().equals(role)) {
                return true;
            }
        }
        return false;
    }
    public  static List<String> currRoles() {
        List<String> roles = new ArrayList<>(3);
        for (GrantedAuthority authority : currSecurityUser().getAuthorities()) {
            String role = authority.getAuthority();
            roles.add(role.replaceAll("ROLE_",""));
        }
        return roles;
    }

    public static List<String> currScopes(){
        OAuth2Authentication oAuth2Authentication = currSecurityUser();
        oAuth2Authentication.getOAuth2Request().getClientId();
       return new ArrayList<>(oAuth2Authentication.getOAuth2Request().getScope());
    }
    public static String currClientId(){
        OAuth2Authentication oAuth2Authentication = currSecurityUser();
        return oAuth2Authentication.getOAuth2Request().getClientId();
    }

}
