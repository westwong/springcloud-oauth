package com.k2future.oauth2server.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

/**
 *
 * @author  West
 * @date  create in 2019/05/07
 *
 **/
@Configuration
@EnableAuthorizationServer
public class Oauth2Config extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RedisConnectionFactory connectionFactory;
    /**
     * 默认过期时间 十天
     */
    private static final int DEFAULT_VALIDITY_S = 3600 * 24 * 10;

    /**
     *   配置客户端信息
     * @param clients 客户端
     * @throws Exception
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        // 将客户端的信息存储在内存中
        clients.inMemory()
                .withClient("web-client")
                .secret(passwordEncoder.encode("password"))
                .authorizedGrantTypes("client_credentials", "refresh_token", "password", "mobile")
                .accessTokenValiditySeconds(DEFAULT_VALIDITY_S)
                .scopes("web")
                .and()
                .withClient("app-client")
                .secret(passwordEncoder.encode("password"))
                .authorizedGrantTypes("client_credentials", "refresh_token", "password", "mobile","sign")
                .accessTokenValiditySeconds(DEFAULT_VALIDITY_S)
                .scopes("app");

    }

    /**
     * 配置授权 token 的节点和 token 服务
     * @param endpoints config
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints
                .tokenStore(new RedisTokenStore(connectionFactory))
                // 开启密码验证，来源于 WebSecurityConfigurerAdapter
                .authenticationManager(authenticationManager)
//                .allowedTokenEndpointRequestMethods(HttpMethod.POST,HttpMethod.GET)
                // 读取验证用户的信息
                .userDetailsService(userService);
    }

    /**
     *  配置 token 节点的安全策略
     * @param security config
     * @throws Exception 异常
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        // 获取 token 的策略
        security.allowFormAuthenticationForClients().tokenKeyAccess("permitAll()")
                .checkTokenAccess("isAuthenticated()");
    }


}