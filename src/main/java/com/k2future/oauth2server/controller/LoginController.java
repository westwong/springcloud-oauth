package com.k2future.oauth2server.controller;

import com.k2future.oauth2server.config.provider.MobileAuthenticationProvider;
import com.k2future.oauth2server.config.provider.MobileAuthenticationToken;
import com.k2future.oauth2server.config.provider.SignAuthenticationProvider;
import com.k2future.oauth2server.service.wechat.WeChatUtils;
import com.k2future.oauth2server.util.Assert;

import org.apache.commons.codec.Charsets;
import org.bouncycastle.util.encoders.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.UnapprovedClientAuthenticationException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;

/**
 * 登录控制
 *
 * @author West
 * @date create in 2020/1/7
 */
@RestController
@Slf4j
public class LoginController {


    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ClientDetailsService clientDetailsService;
    @Autowired
    private AuthorizationServerTokenServices defaultAuthorizationServerTokenServices;
    @Autowired
    private MobileAuthenticationProvider mobileAuthenticationProvider;
    @Autowired
    private SignAuthenticationProvider signAuthenticationProvider;

    private final AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource = new WebAuthenticationDetailsSource();

    private ResponseEntity<OAuth2AccessToken> getResponse(OAuth2AccessToken accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Cache-Control", "no-store");
        headers.set("Pragma", "no-cache");
        headers.set("Content-Type", "application/json;charset=UTF-8");
        return new ResponseEntity<>(accessToken, headers, HttpStatus.OK);
    }

    /**
     * Decodes the header into a username and password.
     *
     * @throws BadCredentialsException if the Basic header is not present or is not valid
     *                                 Base64
     */
    private String[] extractAndDecodeHeader(String header) {

        byte[] base64Token = header.substring(6).getBytes(Charsets.UTF_8);
        byte[] decoded;
        try {
            decoded = Base64.decode(base64Token);
        } catch (IllegalArgumentException e) {
            throw new BadCredentialsException(
                    "Failed to decode basic authentication token");
        }

        String token = new String(decoded, Charsets.UTF_8);

        int delim = token.indexOf(":");

        if (delim == -1) {
            throw new BadCredentialsException("Invalid basic authentication token");
        }
        return new String[]{token.substring(0, delim), token.substring(delim + 1)};
    }


    /**
     * 手机号码和验证登录
     *
     * @param params  参数
     * @param request 请求
     * @return token
     */
    @PostMapping("/mobile/token")
    public ResponseEntity<OAuth2AccessToken> grantTypeMobile(@RequestParam Map<String, String> params, HttpServletRequest request) {
        String mobile = params.get("mobile");
        Assert.hasText(mobile, "电话号码为空");
        String code = params.get("code");
        Assert.hasText(code, "验证码为空");
        return this.login(params, request, mobile, code, "mobile", mobileAuthenticationProvider);
    }

    /**
     * 签名登录 openId
     *
     * @param params  参数
     * @param request 请求
     * @return token
     */
    @PostMapping("/sign/token")
    public ResponseEntity<OAuth2AccessToken> grantTypeSign(@RequestParam Map<String, String> params, HttpServletRequest request) {
        String sign = params.get("sign");
        Assert.hasText(sign, "签名为空");
        //其他平台 参照这个
        String openId = WeChatUtils.getOpenId(sign);
        return this.login(params, request, openId, "", "sign", signAuthenticationProvider);
    }

    /**
     * 登录逻辑
     *
     * @param params   参数
     * @param request  请求
     * @param username 用户名
     * @param password 密码
     * @param scope    范围
     * @param provider provider
     * @return token
     */
    private ResponseEntity<OAuth2AccessToken> login(Map<String, String> params, HttpServletRequest request, String username, String password, String scope, AuthenticationProvider provider) {

        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Basic ")) {
            throw new UnapprovedClientAuthenticationException("请求头中client信息为空");
        }
        String[] tokens = extractAndDecodeHeader(header);
        assert tokens.length == 2;
        String clientId = tokens[0];
        String clientSecret = tokens[1];

        params.put("clientId", clientId);
        params.put("clientSecret", clientSecret);

        ClientDetails clientDetails = clientDetailsService.loadClientByClientId(clientId);
        if (!passwordEncoder.matches(clientSecret, clientDetails.getClientSecret())) {
            throw new UnapprovedClientAuthenticationException("clientSecret error!");
        }
        TokenRequest tokenRequest = new TokenRequest(params, clientId, clientDetails.getScope(), scope);
        OAuth2Request oAuth2Request = tokenRequest.createOAuth2Request(clientDetails);

        MobileAuthenticationToken token = new MobileAuthenticationToken(username, password);
        token.setDetails(authenticationDetailsSource.buildDetails(request));
        Authentication authenticate = provider.authenticate(token);
        OAuth2Authentication oAuth2Authentication = new OAuth2Authentication(oAuth2Request, authenticate);
        OAuth2AccessToken oAuth2AccessToken = defaultAuthorizationServerTokenServices.createAccessToken(oAuth2Authentication);
        log.info("获取token 成功：{}", oAuth2AccessToken.getValue());
        return getResponse(oAuth2AccessToken);
    }
}
