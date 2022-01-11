# 认证和授权

## 地址

- [github](https://github.com/westwong/springcloud-oauth)

## 简介

- 该组件采用Oauth 2.0,如果你不太了解什么是oauth2,还是推荐 阮一峰《[理解OAuth 2.0](http://www.ruanyifeng.com/blog/2014/05/oauth_2_0.html)》。

- 该组件涉及到的技术栈有： [Spring Security OAuth](https://spring.io/projects/spring-security-oauth) 、JPA 、Mysql、Redis 以及微服务的基本组件。

  ```xml
   <dependencies>
          <dependency>
              <groupId>org.springframework.boot</groupId>
              <artifactId>spring-boot-starter-data-jpa</artifactId>
          </dependency>
          <dependency>
              <groupId>org.springframework.boot</groupId>
              <artifactId>spring-boot-starter-web</artifactId>
          </dependency>
          <dependency>
              <groupId>org.springframework.cloud</groupId>
              <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
          </dependency>
          <dependency>
              <groupId>org.springframework.cloud</groupId>
              <artifactId>spring-cloud-starter-oauth2</artifactId>
          </dependency>
  
          <dependency>
              <groupId>org.springframework.boot</groupId>
              <artifactId>spring-boot-starter-data-redis</artifactId>
          </dependency>
  
          <dependency>
              <groupId>mysql</groupId>
              <artifactId>mysql-connector-java</artifactId>
              <scope>runtime</scope>
          </dependency>
          <dependency>
              <groupId>org.projectlombok</groupId>
              <artifactId>lombok</artifactId>
              <optional>true</optional>
          </dependency>
          <dependency>
              <groupId>org.springframework.boot</groupId>
              <artifactId>spring-boot-starter-test</artifactId>
              <scope>test</scope>
          </dependency>
  
          <dependency>
              <groupId>com.aliyun</groupId>
              <artifactId>aliyun-java-sdk-core</artifactId>
              <version>3.7.0</version>
          </dependency>
          <dependency>
              <groupId>com.aliyun</groupId>
              <artifactId>aliyun-java-sdk-ecs</artifactId>
              <version>4.11.0</version>
          </dependency>
  
          <dependency>
              <groupId>com.alibaba</groupId>
              <artifactId>fastjson</artifactId>
              <version>1.2.25</version>
          </dependency>
  
          <dependency>
              <groupId>org.apache.commons</groupId>
              <artifactId>commons-lang3</artifactId>
              <version>3.3.2</version>
              <scope>compile</scope>
          </dependency>
  
      </dependencies>
  ```

- 该组件支持 用户名密码模式、手机验证码登录，openId 登录 基本上能满足一般项目的需求。

  - 手机验证码登录，短信服务用的是[阿里云短信服务](https://help.aliyun.com/document_detail/101414.html)。

  - 微信openId 登录目前只是针对小程序。

    相关yml配置如下：

    ```properties
    ali:
      message:
        access-key: 123
        access-secret: 123
    
    wechat:
      mini-app:
        app-id: 123
        app-key: 123
    ```

- 常规配置就不细说了。对于第一次用相关组件的同学，还是建议您首先看看文档。

  关于客户端Client的配置：

  （我只粘贴了部分代码）

  ```java
  package com.k2future.oauth2server.config.security;
  /**
   *
   * @author  West
   * @date  create in 2019/05/07
   *
   **/
  @Configuration
  @EnableAuthorizationServer
  public class Oauth2Config extends AuthorizationServerConfigurerAdapter {
  
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
  
  }
  ```

  

  关于url权限过滤的的配置：

  （主要针对以前没有用过security的同学，其实和shiro差不多）

  （我只粘贴了部分代码）

  ```java
  package com.k2future.oauth2server.config.security;
  
  @Configuration
  @EnableResourceServer
  @EnableGlobalMethodSecurity(prePostEnabled = true)
  public class ResourceServerConfig extends ResourceServerConfigurerAdapter {
  
  
      @Override
      public void configure(HttpSecurity http) throws Exception {
          http.authorizeRequests()
                  .antMatchers(HttpMethod.OPTIONS).permitAll()
                  .antMatchers("/users/register",
                               "/mobile/**", 
                               "/sign/**").permitAll()
                  .anyRequest().authenticated() // 所有请求都需要安全验证
                  .and()
                  .csrf().disable()
                  .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
      }
  
  ```

- 最后还需要说明一下除了指定上面指定不需要权限的api，其他都接口都是需要权限的。token分为一下两种类型：

  - 用于获取token的 Authorization: 为basic类型，他的算法是 "clientId：clientSecret"的 base64 。如：Basic d2ViLWNsaWVudDpZdW5RdWU=Y2xpbmljLWNsaWVudDpZdW5RdWU=  

  - 第二种是Oauth2的授权模式：

    ```json
    {
        "access_token": "ce04de44-1da0-4ca0-8310-c26e3f6a4843",
        "token_type": "bearer",
        "refresh_token": "87520888-bb1a-437e-91b8-2e44f61af949",
        "expires_in": 606161,
        "scope": "web"
    }
    ```

    他的 Authorization:为beare, 用法是 （token_type access_token），如：bearer ce04de44-1da0-4ca0-8310-c26e3f6a4843

    因为方便，我把access_token的过期时间设置为15天。如果你觉得不方便可以修改过期时间，并在应用中使用刷新获取 refresh_token 获取 access_token 的方式。参考下面的api: **获取token**。

    

# API(仅供参考)

## 手机短信验证码获取token

<a id=手机短信验证码获取token> </a>

### 基本信息

**Path：** /mobile/token

**Method：** POST

**接口描述：**

<p>token 刷新 参考 接口&nbsp; 获取token(/oauth/token)</p>
### 请求参数

**Headers**

| 参数名称      | 参数值                            | 是否必须 | 示例                                                       | 备注                            |
| ------------- | --------------------------------- | -------- | ---------------------------------------------------------- | ------------------------------- |
| Content-Type  | application/x-www-form-urlencoded | 是       |                                                            |                                 |
| Authorization | Basic                             | 是       | Basic d2ViLWNsaWVudDpZdW5RdWU=Y2xpbmljLWNsaWVudDpZdW5RdWU= | "clientId:clientSecret"的base64 |

**Body**

| 参数名称 | 参数类型 | 是否必须 | 示例        | 备注         |
| -------- | -------- | -------- | ----------- | ------------ |
| mobile   | text     | 是       | 13312341234 | 必须是手机号 |
| code     | text     | 是       | 123456      | 六位数字     |



### 返回数据

<table>
  <thead class="ant-table-thead">
    <tr>
      <th key=name>名称</th><th key=type>类型</th><th key=required>是否必须</th><th key=default>默认值</th><th key=desc>备注</th><th key=sub>其他信息</th>
    </tr>
  </thead><tbody className="ant-table-tbody"><tr key=0-0><td key=0><span style="padding-left: 0px"><span style="color: #8c8a8a"></span> access_token</span></td><td key=1><span>string</span></td><td key=2>必须</td><td key=3></td><td key=4><span style="white-space: pre-wrap"></span></td><td key=5></td></tr><tr key=0-1><td key=0><span style="padding-left: 0px"><span style="color: #8c8a8a"></span> token_type</span></td><td key=1><span>string</span></td><td key=2>必须</td><td key=3></td><td key=4><span style="white-space: pre-wrap"></span></td><td key=5></td></tr><tr key=0-2><td key=0><span style="padding-left: 0px"><span style="color: #8c8a8a"></span> refresh_token</span></td><td key=1><span>string</span></td><td key=2>必须</td><td key=3></td><td key=4><span style="white-space: pre-wrap"></span></td><td key=5></td></tr><tr key=0-3><td key=0><span style="padding-left: 0px"><span style="color: #8c8a8a"></span> expires_in</span></td><td key=1><span>number</span></td><td key=2>必须</td><td key=3></td><td key=4><span style="white-space: pre-wrap"></span></td><td key=5></td></tr><tr key=0-4><td key=0><span style="padding-left: 0px"><span style="color: #8c8a8a"></span> scope</span></td><td key=1><span>string</span></td><td key=2>必须</td><td key=3></td><td key=4><span style="white-space: pre-wrap"></span></td><td key=5></td></tr>
               </tbody>
              </table>


## 签名获取token

<a id=签名获取token> </a>

### 基本信息

**Path：** /sign/token

**Method：** POST

**接口描述：**

<p>token 刷新 参考 接口&nbsp; 获取token(/oauth/token)</p>
### 请求参数

**Headers**

| 参数名称      | 参数值                            | 是否必须 | 示例                               | 备注                            |
| ------------- | --------------------------------- | -------- | ---------------------------------- | ------------------------------- |
| Content-Type  | application/x-www-form-urlencoded | 是       |                                    |                                 |
| Authorization | Basic                             | 是       | Basic Y2xpbmljLWNsaWVudDpZdW5RdWU= | "clientId:clientSecret"的base64 |

**Body**

| 参数名称 | 参数类型 | 是否必须 | 示例 | 备注                   |
| -------- | -------- | -------- | ---- | ---------------------- |
| sign     | text     | 是       |      | openId等第三方唯一标识 |



### 返回数据

<table>
  <thead class="ant-table-thead">
    <tr>
      <th key=name>名称</th><th key=type>类型</th><th key=required>是否必须</th><th key=default>默认值</th><th key=desc>备注</th><th key=sub>其他信息</th>
    </tr>
  </thead><tbody className="ant-table-tbody">
               </tbody>
              </table>


## 获取token

<a id=获取token> </a>

### 基本信息

**Path：** /oauth/token

**Method：** POST

**接口描述：**


### 请求参数

**Headers**

| 参数名称      | 参数值                            | 是否必须 | 示例                           | 备注                            |
| ------------- | --------------------------------- | -------- | ------------------------------ | ------------------------------- |
| Content-Type  | application/x-www-form-urlencoded | 是       |                                |                                 |
| Authorization | Basic                             | 是       | Basic d2ViLWNsaWVudDpZdW5RdWU= | "clientId:clientSecret"的base64 |

**Body**

| 参数名称      | 参数类型 | 是否必须 | 示例     | 备注                                  |
| ------------- | -------- | -------- | -------- | ------------------------------------- |
| username      | text     | 是       | qwe      | 账号 刷新时候不需要                   |
| password      | text     | 是       | 123456   | 密码 刷新时候不需要                   |
| grant_type    | text     | 是       | password | 授权方式  "refresh_token", "password" |
| refresh_token | text     | 否       |          | 刷新时需要                            |



### 返回数据

<table>
  <thead class="ant-table-thead">
    <tr>
      <th key=name>名称</th><th key=type>类型</th><th key=required>是否必须</th><th key=default>默认值</th><th key=desc>备注</th><th key=sub>其他信息</th>
    </tr>
  </thead><tbody className="ant-table-tbody"><tr key=0-0><td key=0><span style="padding-left: 0px"><span style="color: #8c8a8a"></span> access_token</span></td><td key=1><span>string</span></td><td key=2>必须</td><td key=3></td><td key=4><span style="white-space: pre-wrap">token</span></td><td key=5></td></tr><tr key=0-1><td key=0><span style="padding-left: 0px"><span style="color: #8c8a8a"></span> token_type</span></td><td key=1><span>string</span></td><td key=2>必须</td><td key=3></td><td key=4><span style="white-space: pre-wrap">类型</span></td><td key=5></td></tr><tr key=0-2><td key=0><span style="padding-left: 0px"><span style="color: #8c8a8a"></span> refresh_token</span></td><td key=1><span>string</span></td><td key=2>必须</td><td key=3></td><td key=4><span style="white-space: pre-wrap">用于刷新token的token</span></td><td key=5></td></tr><tr key=0-3><td key=0><span style="padding-left: 0px"><span style="color: #8c8a8a"></span> expires_in</span></td><td key=1><span>number</span></td><td key=2>必须</td><td key=3></td><td key=4><span style="white-space: pre-wrap">过期时间</span></td><td key=5></td></tr><tr key=0-4><td key=0><span style="padding-left: 0px"><span style="color: #8c8a8a"></span> scope</span></td><td key=1><span>string</span></td><td key=2>必须</td><td key=3></td><td key=4><span style="white-space: pre-wrap">作用范围</span></td><td key=5></td></tr>
               </tbody>
              </table>


## 获取短信验证码

<a id=获取短信验证码> </a>

### 基本信息

**Path：** /mobile/code/get

**Method：** POST

**接口描述：**

<p>获取6位短信验证码，有效时间10分钟</p>
### 请求参数

**Headers**

| 参数名称      | 参数值                            | 是否必须 | 示例                           | 备注                            |
| ------------- | --------------------------------- | -------- | ------------------------------ | ------------------------------- |
| Content-Type  | application/x-www-form-urlencoded | 是       |                                |                                 |
| Authorization | Basic                             | 是       | Basic d2ViLWNsaWVudDpZdW5RdWU= | "clientId:clientSecret"的base64 |

**Body**

| 参数名称 | 参数类型 | 是否必须 | 示例        | 备注         |
| -------- | -------- | -------- | ----------- | ------------ |
| mobile   | text     | 是       | 13312341234 | 必须是手机号 |



### 返回数据

<table>
  <thead class="ant-table-thead">
    <tr>
      <th key=name>名称</th><th key=type>类型</th><th key=required>是否必须</th><th key=default>默认值</th><th key=desc>备注</th><th key=sub>其他信息</th>
    </tr>
  </thead><tbody className="ant-table-tbody"><tr key=0-0><td key=0><span style="padding-left: 0px"><span style="color: #8c8a8a"></span> access_token</span></td><td key=1><span>string</span></td><td key=2>必须</td><td key=3></td><td key=4><span style="white-space: pre-wrap"></span></td><td key=5></td></tr><tr key=0-1><td key=0><span style="padding-left: 0px"><span style="color: #8c8a8a"></span> token_type</span></td><td key=1><span>string</span></td><td key=2>必须</td><td key=3></td><td key=4><span style="white-space: pre-wrap"></span></td><td key=5></td></tr><tr key=0-2><td key=0><span style="padding-left: 0px"><span style="color: #8c8a8a"></span> refresh_token</span></td><td key=1><span>string</span></td><td key=2>必须</td><td key=3></td><td key=4><span style="white-space: pre-wrap"></span></td><td key=5></td></tr><tr key=0-3><td key=0><span style="padding-left: 0px"><span style="color: #8c8a8a"></span> expires_in</span></td><td key=1><span>number</span></td><td key=2>必须</td><td key=3></td><td key=4><span style="white-space: pre-wrap"></span></td><td key=5></td></tr><tr key=0-4><td key=0><span style="padding-left: 0px"><span style="color: #8c8a8a"></span> scope</span></td><td key=1><span>string</span></td><td key=2>必须</td><td key=3></td><td key=4><span style="white-space: pre-wrap"></span></td><td key=5></td></tr>
               </tbody>
              </table>


## 验证短信验证码

<a id=验证短信验证码> </a>

### 基本信息

**Path：** /mobile/code/verify

**Method：** POST

**接口描述：**


### 请求参数

**Headers**

| 参数名称     | 参数值                            | 是否必须 | 示例 | 备注 |
| ------------ | --------------------------------- | -------- | ---- | ---- |
| Content-Type | application/x-www-form-urlencoded | 是       |      |      |

**Body**

| 参数名称 | 参数类型 | 是否必须 | 示例        | 备注         |
| -------- | -------- | -------- | ----------- | ------------ |
| mobile   | text     | 是       | 13312341234 | 必须是手机号 |
| code     | text     | 是       | 123456      | 六位数字     |



### 返回数据

<table>
  <thead class="ant-table-thead">
    <tr>
      <th key=name>名称</th><th key=type>类型</th><th key=required>是否必须</th><th key=default>默认值</th><th key=desc>备注</th><th key=sub>其他信息</th>
    </tr>
  </thead><tbody className="ant-table-tbody">
               </tbody>
              </table>



## 修改用户

<a id=修改用户> </a>

### 基本信息

**Path：** /users/update

**Method：** POST

**接口描述：**


### 请求参数

**Headers**

| 参数名称      | 参数值           | 是否必须 | 示例                                        | 备注 |
| ------------- | ---------------- | -------- | ------------------------------------------- | ---- |
| Content-Type  | application/json | 是       |                                             |      |
| Authorization | bearer           | 是       | bearer ce04de44-1da0-4ca0-8310-c26e3f6a4843 |      |

**Body**

<table>
  <thead class="ant-table-thead">
    <tr>
      <th key=name>名称</th><th key=type>类型</th><th key=required>是否必须</th><th key=default>默认值</th><th key=desc>备注</th><th key=sub>其他信息</th>
    </tr>
  </thead><tbody className="ant-table-tbody"><tr key=0-0><td key=0><span style="padding-left: 0px"><span style="color: #8c8a8a"></span> id</span></td><td key=1><span>number</span></td><td key=2>必须</td><td key=3></td><td key=4><span style="white-space: pre-wrap">用户id</span></td><td key=5></td></tr><tr key=0-1><td key=0><span style="padding-left: 0px"><span style="color: #8c8a8a"></span> phone</span></td><td key=1><span>string</span></td><td key=2>非必须</td><td key=3></td><td key=4><span style="white-space: pre-wrap">电话</span></td><td key=5></td></tr><tr key=0-2><td key=0><span style="padding-left: 0px"><span style="color: #8c8a8a"></span> openId</span></td><td key=1><span>string</span></td><td key=2>非必须</td><td key=3></td><td key=4><span style="white-space: pre-wrap">openId</span></td><td key=5></td></tr>
               </tbody>
              </table>


### 返回数据

<table>
  <thead class="ant-table-thead">
    <tr>
      <th key=name>名称</th><th key=type>类型</th><th key=required>是否必须</th><th key=default>默认值</th><th key=desc>备注</th><th key=sub>其他信息</th>
    </tr>
  </thead><tbody className="ant-table-tbody">
               </tbody>
              </table>


## 修改用户密码

<a id=修改用户密码> </a>

### 基本信息

**Path：** /users/pwd/update

**Method：** POST

**接口描述：**


### 请求参数

**Headers**

| 参数名称      | 参数值           | 是否必须 | 示例 | 备注         |
| ------------- | ---------------- | -------- | ---- | ------------ |
| Content-Type  | application/json | 是       |      |              |
| Authorization |                  | 是       |      | access_token |

**Body**

<table>
  <thead class="ant-table-thead">
    <tr>
      <th key=name>名称</th><th key=type>类型</th><th key=required>是否必须</th><th key=default>默认值</th><th key=desc>备注</th><th key=sub>其他信息</th>
    </tr>
  </thead><tbody className="ant-table-tbody"><tr key=0-0><td key=0><span style="padding-left: 0px"><span style="color: #8c8a8a"></span> userId</span></td><td key=1><span>number</span></td><td key=2>必须</td><td key=3></td><td key=4><span style="white-space: pre-wrap">用户id</span></td><td key=5></td></tr><tr key=0-1><td key=0><span style="padding-left: 0px"><span style="color: #8c8a8a"></span> oldPassword</span></td><td key=1><span>string</span></td><td key=2>必须</td><td key=3></td><td key=4><span style="white-space: pre-wrap"></span></td><td key=5></td></tr><tr key=0-2><td key=0><span style="padding-left: 0px"><span style="color: #8c8a8a"></span> newPassword</span></td><td key=1><span>string</span></td><td key=2>必须</td><td key=3></td><td key=4><span style="white-space: pre-wrap"></span></td><td key=5></td></tr>
               </tbody>
              </table>


### 返回数据

<table>
  <thead class="ant-table-thead">
    <tr>
      <th key=name>名称</th><th key=type>类型</th><th key=required>是否必须</th><th key=default>默认值</th><th key=desc>备注</th><th key=sub>其他信息</th>
    </tr>
  </thead><tbody className="ant-table-tbody"><tr key=0-0><td key=0><span style="padding-left: 0px"><span style="color: #8c8a8a"></span> error</span></td><td key=1><span>string</span></td><td key=2>非必须</td><td key=3></td><td key=4><span style="white-space: pre-wrap"></span></td><td key=5></td></tr><tr key=0-1><td key=0><span style="padding-left: 0px"><span style="color: #8c8a8a"></span> status</span></td><td key=1><span>number</span></td><td key=2>非必须</td><td key=3></td><td key=4><span style="white-space: pre-wrap"></span></td><td key=5></td></tr>
               </tbody>
              </table>


## 修改用户角色

<a id=修改用户角色> </a>

### 基本信息

**Path：** /users/role/update

**Method：** POST

**接口描述：**


### 请求参数

**Headers**

| 参数名称     | 参数值           | 是否必须 | 示例 | 备注 |
| ------------ | ---------------- | -------- | ---- | ---- |
| Content-Type | application/json | 是       |      |      |

**Body**

<table>
  <thead class="ant-table-thead">
    <tr>
      <th key=name>名称</th><th key=type>类型</th><th key=required>是否必须</th><th key=default>默认值</th><th key=desc>备注</th><th key=sub>其他信息</th>
    </tr>
  </thead><tbody className="ant-table-tbody"><tr key=0-0><td key=0><span style="padding-left: 0px"><span style="color: #8c8a8a"></span> id</span></td><td key=1><span>number</span></td><td key=2>必须</td><td key=3></td><td key=4><span style="white-space: pre-wrap">userId</span></td><td key=5></td></tr><tr key=0-1><td key=0><span style="padding-left: 0px"><span style="color: #8c8a8a"></span> roleList</span></td><td key=1><span>object []</span></td><td key=2>必须</td><td key=3></td><td key=4><span style="white-space: pre-wrap">删除传 ：[] (注意:不是null)</span></td><td key=5><p key=3><span style="font-weight: '700'">item 类型: </span><span>object</span></p></td></tr><tr key=0-1-0><td key=0><span style="padding-left: 20px"><span style="color: #8c8a8a">├─</span> id</span></td><td key=1><span>number</span></td><td key=2>必须</td><td key=3></td><td key=4><span style="white-space: pre-wrap">roleId</span></td><td key=5></td></tr>
               </tbody>
              </table>


### 返回数据

<table>
  <thead class="ant-table-thead">
    <tr>
      <th key=name>名称</th><th key=type>类型</th><th key=required>是否必须</th><th key=default>默认值</th><th key=desc>备注</th><th key=sub>其他信息</th>
    </tr>
  </thead><tbody className="ant-table-tbody"><tr key=0-0><td key=0><span style="padding-left: 0px"><span style="color: #8c8a8a"></span> status</span></td><td key=1><span>number</span></td><td key=2>非必须</td><td key=3></td><td key=4><span style="white-space: pre-wrap"></span></td><td key=5></td></tr>
               </tbody>
              </table>


## 修改用户详情

<a id=修改用户详情> </a>

### 基本信息

**Path：** /userDetail/update

**Method：** POST

**接口描述：**


### 请求参数

**Headers**

| 参数名称      | 参数值           | 是否必须 | 示例 | 备注 |
| ------------- | ---------------- | -------- | ---- | ---- |
| Content-Type  | application/json | 是       |      |      |
| Authorization |                  | 是       |      |      |

**Body**

<table>
  <thead class="ant-table-thead">
    <tr>
      <th key=name>名称</th><th key=type>类型</th><th key=required>是否必须</th><th key=default>默认值</th><th key=desc>备注</th><th key=sub>其他信息</th>
    </tr>
  </thead><tbody className="ant-table-tbody"><tr key=0-0><td key=0><span style="padding-left: 0px"><span style="color: #8c8a8a"></span> id</span></td><td key=1><span>number</span></td><td key=2>必须</td><td key=3></td><td key=4><span style="white-space: pre-wrap"></span></td><td key=5></td></tr><tr key=0-1><td key=0><span style="padding-left: 0px"><span style="color: #8c8a8a"></span> name</span></td><td key=1><span>string</span></td><td key=2>非必须</td><td key=3></td><td key=4><span style="white-space: pre-wrap"></span></td><td key=5></td></tr><tr key=0-2><td key=0><span style="padding-left: 0px"><span style="color: #8c8a8a"></span> age</span></td><td key=1><span>string</span></td><td key=2>非必须</td><td key=3></td><td key=4><span style="white-space: pre-wrap"></span></td><td key=5></td></tr><tr key=0-3><td key=0><span style="padding-left: 0px"><span style="color: #8c8a8a"></span> phone</span></td><td key=1><span>string</span></td><td key=2>非必须</td><td key=3></td><td key=4><span style="white-space: pre-wrap"></span></td><td key=5></td></tr><tr key=0-4><td key=0><span style="padding-left: 0px"><span style="color: #8c8a8a"></span> gender</span></td><td key=1><span>string</span></td><td key=2>非必须</td><td key=3></td><td key=4><span style="white-space: pre-wrap"></span></td><td key=5></td></tr><tr key=0-5><td key=0><span style="padding-left: 0px"><span style="color: #8c8a8a"></span> addr</span></td><td key=1><span>string</span></td><td key=2>非必须</td><td key=3></td><td key=4><span style="white-space: pre-wrap"></span></td><td key=5></td></tr><tr key=0-6><td key=0><span style="padding-left: 0px"><span style="color: #8c8a8a"></span> createTime</span></td><td key=1><span>string</span></td><td key=2>非必须</td><td key=3></td><td key=4><span style="white-space: pre-wrap">yyyy-MM-dd HH:mm:ss</span></td><td key=5></td></tr><tr key=0-7><td key=0><span style="padding-left: 0px"><span style="color: #8c8a8a"></span> nation</span></td><td key=1><span>string</span></td><td key=2>非必须</td><td key=3></td><td key=4><span style="white-space: pre-wrap"></span></td><td key=5></td></tr><tr key=0-8><td key=0><span style="padding-left: 0px"><span style="color: #8c8a8a"></span> instruction</span></td><td key=1><span>string</span></td><td key=2>非必须</td><td key=3></td><td key=4><span style="white-space: pre-wrap"></span></td><td key=5></td></tr><tr key=0-9><td key=0><span style="padding-left: 0px"><span style="color: #8c8a8a"></span> images</span></td><td key=1><span>string</span></td><td key=2>非必须</td><td key=3></td><td key=4><span style="white-space: pre-wrap"></span></td><td key=5></td></tr><tr key=0-10><td key=0><span style="padding-left: 0px"><span style="color: #8c8a8a"></span> birthday</span></td><td key=1><span>string</span></td><td key=2>非必须</td><td key=3></td><td key=4><span style="white-space: pre-wrap">yyyy-MM-dd</span></td><td key=5></td></tr>
               </tbody>
              </table>


### 返回数据

<table>
  <thead class="ant-table-thead">
    <tr>
      <th key=name>名称</th><th key=type>类型</th><th key=required>是否必须</th><th key=default>默认值</th><th key=desc>备注</th><th key=sub>其他信息</th>
    </tr>
  </thead><tbody className="ant-table-tbody"><tr key=0-0><td key=0><span style="padding-left: 0px"><span style="color: #8c8a8a"></span> status</span></td><td key=1><span>number</span></td><td key=2>非必须</td><td key=3></td><td key=4><span style="white-space: pre-wrap"></span></td><td key=5></td></tr><tr key=0-1><td key=0><span style="padding-left: 0px"><span style="color: #8c8a8a"></span> error</span></td><td key=1><span>string</span></td><td key=2>非必须</td><td key=3></td><td key=4><span style="white-space: pre-wrap"></span></td><td key=5></td></tr>
               </tbody>
              </table>


## 查询用户

<a id=查询用户> </a>

### 基本信息

**Path：** /users/find

**Method：** POST

**接口描述：**


### 请求参数

**Headers**

| 参数名称      | 参数值           | 是否必须 | 示例 | 备注 |
| ------------- | ---------------- | -------- | ---- | ---- |
| Content-Type  | application/json | 是       |      |      |
| Authorization |                  | 是       |      |      |

**Body**

<table>
  <thead class="ant-table-thead">
    <tr>
      <th key=name>名称</th><th key=type>类型</th><th key=required>是否必须</th><th key=default>默认值</th><th key=desc>备注</th><th key=sub>其他信息</th>
    </tr>
  </thead><tbody className="ant-table-tbody"><tr key=0-0><td key=0><span style="padding-left: 0px"><span style="color: #8c8a8a"></span> id</span></td><td key=1><span>number</span></td><td key=2>非必须</td><td key=3></td><td key=4><span style="white-space: pre-wrap">当传了id时 以id为主</span></td><td key=5></td></tr><tr key=0-1><td key=0><span style="padding-left: 0px"><span style="color: #8c8a8a"></span> username</span></td><td key=1><span>string</span></td><td key=2>非必须</td><td key=3></td><td key=4><span style="white-space: pre-wrap"></span></td><td key=5></td></tr>
               </tbody>
              </table>


### 返回数据

<table>
  <thead class="ant-table-thead">
    <tr>
      <th key=name>名称</th><th key=type>类型</th><th key=required>是否必须</th><th key=default>默认值</th><th key=desc>备注</th><th key=sub>其他信息</th>
    </tr>
  </thead><tbody className="ant-table-tbody">
               </tbody>
              </table>


## 注册用户

<a id=注册用户> </a>

### 基本信息

**Path：** /users/register

**Method：** POST

**接口描述：**


### 请求参数

**Headers**

| 参数名称     | 参数值           | 是否必须 | 示例 | 备注 |
| ------------ | ---------------- | -------- | ---- | ---- |
| Content-Type | application/json | 是       |      |      |

**Body**

<table>
  <thead class="ant-table-thead">
    <tr>
      <th key=name>名称</th><th key=type>类型</th><th key=required>是否必须</th><th key=default>默认值</th><th key=desc>备注</th><th key=sub>其他信息</th>
    </tr>
  </thead><tbody className="ant-table-tbody"><tr key=0-0><td key=0><span style="padding-left: 0px"><span style="color: #8c8a8a"></span> username</span></td><td key=1><span>string</span></td><td key=2>非必须</td><td key=3></td><td key=4><span style="white-space: pre-wrap"></span></td><td key=5></td></tr><tr key=0-1><td key=0><span style="padding-left: 0px"><span style="color: #8c8a8a"></span> password</span></td><td key=1><span>string</span></td><td key=2>非必须</td><td key=3></td><td key=4><span style="white-space: pre-wrap"></span></td><td key=5></td></tr>
               </tbody>
              </table>


### 返回数据

<table>
  <thead class="ant-table-thead">
    <tr>
      <th key=name>名称</th><th key=type>类型</th><th key=required>是否必须</th><th key=default>默认值</th><th key=desc>备注</th><th key=sub>其他信息</th>
    </tr>
  </thead><tbody className="ant-table-tbody"><tr key=0-0><td key=0><span style="padding-left: 0px"><span style="color: #8c8a8a"></span> error</span></td><td key=1><span>string</span></td><td key=2>非必须</td><td key=3></td><td key=4><span style="white-space: pre-wrap"></span></td><td key=5></td></tr><tr key=0-1><td key=0><span style="padding-left: 0px"><span style="color: #8c8a8a"></span> status</span></td><td key=1><span>number</span></td><td key=2>非必须</td><td key=3></td><td key=4><span style="white-space: pre-wrap"></span></td><td key=5></td></tr><tr key=0-2><td key=0><span style="padding-left: 0px"><span style="color: #8c8a8a"></span> message</span></td><td key=1><span>number</span></td><td key=2>必须</td><td key=3></td><td key=4><span style="white-space: pre-wrap">用户id</span></td><td key=5></td></tr>
               </tbody>
              </table>


## 添加用户

<a id=添加用户> </a>

### 基本信息

**Path：** /users/save

**Method：** POST

**接口描述：**


### 请求参数

**Headers**

| 参数名称      | 参数值           | 是否必须 | 示例 | 备注         |
| ------------- | ---------------- | -------- | ---- | ------------ |
| Content-Type  | application/json | 是       |      |              |
| Authorization |                  | 是       |      | access_token |

**Body**

<table>
  <thead class="ant-table-thead">
    <tr>
      <th key=name>名称</th><th key=type>类型</th><th key=required>是否必须</th><th key=default>默认值</th><th key=desc>备注</th><th key=sub>其他信息</th>
    </tr>
  </thead><tbody className="ant-table-tbody"><tr key=0-0><td key=0><span style="padding-left: 0px"><span style="color: #8c8a8a"></span> username</span></td><td key=1><span>string</span></td><td key=2>非必须</td><td key=3></td><td key=4><span style="white-space: pre-wrap"></span></td><td key=5></td></tr><tr key=0-1><td key=0><span style="padding-left: 0px"><span style="color: #8c8a8a"></span> password</span></td><td key=1><span>string</span></td><td key=2>非必须</td><td key=3></td><td key=4><span style="white-space: pre-wrap"></span></td><td key=5></td></tr><tr key=0-2><td key=0><span style="padding-left: 0px"><span style="color: #8c8a8a"></span> id</span></td><td key=1><span>number</span></td><td key=2>非必须</td><td key=3></td><td key=4><span style="white-space: pre-wrap"></span></td><td key=5></td></tr><tr key=0-3><td key=0><span style="padding-left: 0px"><span style="color: #8c8a8a"></span> roleList</span></td><td key=1><span>object []</span></td><td key=2>非必须</td><td key=3></td><td key=4><span style="white-space: pre-wrap"></span></td><td key=5><p key=3><span style="font-weight: '700'">item 类型: </span><span>object</span></p></td></tr><tr key=0-3-0><td key=0><span style="padding-left: 20px"><span style="color: #8c8a8a">├─</span> roleName</span></td><td key=1><span>string</span></td><td key=2>必须</td><td key=3></td><td key=4><span style="white-space: pre-wrap"></span></td><td key=5></td></tr>
               </tbody>
              </table>


### 返回数据

<table>
  <thead class="ant-table-thead">
    <tr>
      <th key=name>名称</th><th key=type>类型</th><th key=required>是否必须</th><th key=default>默认值</th><th key=desc>备注</th><th key=sub>其他信息</th>
    </tr>
  </thead><tbody className="ant-table-tbody"><tr key=0-0><td key=0><span style="padding-left: 0px"><span style="color: #8c8a8a"></span> error</span></td><td key=1><span>string</span></td><td key=2>非必须</td><td key=3></td><td key=4><span style="white-space: pre-wrap"></span></td><td key=5></td></tr><tr key=0-1><td key=0><span style="padding-left: 0px"><span style="color: #8c8a8a"></span> status</span></td><td key=1><span>number</span></td><td key=2>非必须</td><td key=3></td><td key=4><span style="white-space: pre-wrap"></span></td><td key=5></td></tr><tr key=0-2><td key=0><span style="padding-left: 0px"><span style="color: #8c8a8a"></span> message</span></td><td key=1><span>number</span></td><td key=2>必须</td><td key=3></td><td key=4><span style="white-space: pre-wrap">用户id</span></td><td key=5></td></tr>
               </tbody>
              </table>


## 获取当前用户详情

<a id=获取当前用户详情> </a>

### 基本信息

**Path：** /userDetail/current

**Method：** POST

**接口描述：**


### 请求参数

**Headers**

| 参数名称      | 参数值           | 是否必须 | 示例 | 备注 |
| ------------- | ---------------- | -------- | ---- | ---- |
| Content-Type  | application/json | 是       |      |      |
| Authorization |                  | 是       |      |      |

**Body**

<table>
  <thead class="ant-table-thead">
    <tr>
      <th key=name>名称</th><th key=type>类型</th><th key=required>是否必须</th><th key=default>默认值</th><th key=desc>备注</th><th key=sub>其他信息</th>
    </tr>
  </thead><tbody className="ant-table-tbody">
               </tbody>
              </table>


### 返回数据

<table>
  <thead class="ant-table-thead">
    <tr>
      <th key=name>名称</th><th key=type>类型</th><th key=required>是否必须</th><th key=default>默认值</th><th key=desc>备注</th><th key=sub>其他信息</th>
    </tr>
  </thead><tbody className="ant-table-tbody"><tr key=0-0><td key=0><span style="padding-left: 0px"><span style="color: #8c8a8a"></span> message</span></td><td key=1><span>object</span></td><td key=2>非必须</td><td key=3></td><td key=4><span style="white-space: pre-wrap"></span></td><td key=5></td></tr><tr key=0-0-0><td key=0><span style="padding-left: 20px"><span style="color: #8c8a8a">├─</span> id</span></td><td key=1><span>number</span></td><td key=2>非必须</td><td key=3></td><td key=4><span style="white-space: pre-wrap"></span></td><td key=5></td></tr><tr key=0-0-1><td key=0><span style="padding-left: 20px"><span style="color: #8c8a8a">├─</span> name</span></td><td key=1><span>string</span></td><td key=2>非必须</td><td key=3></td><td key=4><span style="white-space: pre-wrap"></span></td><td key=5></td></tr><tr key=0-0-2><td key=0><span style="padding-left: 20px"><span style="color: #8c8a8a">├─</span> age</span></td><td key=1><span>null</span></td><td key=2>非必须</td><td key=3></td><td key=4><span style="white-space: pre-wrap"></span></td><td key=5></td></tr><tr key=0-0-3><td key=0><span style="padding-left: 20px"><span style="color: #8c8a8a">├─</span> phone</span></td><td key=1><span>null</span></td><td key=2>非必须</td><td key=3></td><td key=4><span style="white-space: pre-wrap"></span></td><td key=5></td></tr><tr key=0-0-4><td key=0><span style="padding-left: 20px"><span style="color: #8c8a8a">├─</span> gender</span></td><td key=1><span>null</span></td><td key=2>非必须</td><td key=3></td><td key=4><span style="white-space: pre-wrap"></span></td><td key=5></td></tr><tr key=0-0-5><td key=0><span style="padding-left: 20px"><span style="color: #8c8a8a">├─</span> addr</span></td><td key=1><span>null</span></td><td key=2>非必须</td><td key=3></td><td key=4><span style="white-space: pre-wrap"></span></td><td key=5></td></tr><tr key=0-0-6><td key=0><span style="padding-left: 20px"><span style="color: #8c8a8a">├─</span> createTime</span></td><td key=1><span>string</span></td><td key=2>非必须</td><td key=3></td><td key=4><span style="white-space: pre-wrap"></span></td><td key=5></td></tr><tr key=0-0-7><td key=0><span style="padding-left: 20px"><span style="color: #8c8a8a">├─</span> nation</span></td><td key=1><span>null</span></td><td key=2>非必须</td><td key=3></td><td key=4><span style="white-space: pre-wrap"></span></td><td key=5></td></tr><tr key=0-0-8><td key=0><span style="padding-left: 20px"><span style="color: #8c8a8a">├─</span> instruction</span></td><td key=1><span>null</span></td><td key=2>非必须</td><td key=3></td><td key=4><span style="white-space: pre-wrap"></span></td><td key=5></td></tr><tr key=0-0-9><td key=0><span style="padding-left: 20px"><span style="color: #8c8a8a">├─</span> images</span></td><td key=1><span>null</span></td><td key=2>非必须</td><td key=3></td><td key=4><span style="white-space: pre-wrap"></span></td><td key=5></td></tr><tr key=0-0-10><td key=0><span style="padding-left: 20px"><span style="color: #8c8a8a">├─</span> birthday</span></td><td key=1><span>null</span></td><td key=2>非必须</td><td key=3></td><td key=4><span style="white-space: pre-wrap"></span></td><td key=5></td></tr><tr key=0-1><td key=0><span style="padding-left: 0px"><span style="color: #8c8a8a"></span> status</span></td><td key=1><span>number</span></td><td key=2>非必须</td><td key=3></td><td key=4><span style="white-space: pre-wrap"></span></td><td key=5></td></tr><tr key=0-2><td key=0><span style="padding-left: 0px"><span style="color: #8c8a8a"></span> error</span></td><td key=1><span>string</span></td><td key=2>非必须</td><td key=3></td><td key=4><span style="white-space: pre-wrap"></span></td><td key=5></td></tr>
               </tbody>
              </table>


## 获取用户详情

<a id=获取用户详情> </a>

### 基本信息

**Path：** /userDetail/get

**Method：** POST

**接口描述：**


### 请求参数

**Headers**

| 参数名称      | 参数值           | 是否必须 | 示例 | 备注 |
| ------------- | ---------------- | -------- | ---- | ---- |
| Content-Type  | application/json | 是       |      |      |
| Authorization |                  | 是       |      |      |

**Body**

<table>
  <thead class="ant-table-thead">
    <tr>
      <th key=name>名称</th><th key=type>类型</th><th key=required>是否必须</th><th key=default>默认值</th><th key=desc>备注</th><th key=sub>其他信息</th>
    </tr>
  </thead><tbody className="ant-table-tbody"><tr key=0-0><td key=0><span style="padding-left: 0px"><span style="color: #8c8a8a"></span> id</span></td><td key=1><span>number</span></td><td key=2>必须</td><td key=3></td><td key=4><span style="white-space: pre-wrap">当传了id时 以id为主</span></td><td key=5></td></tr>
               </tbody>
              </table>


### 返回数据

<table>
  <thead class="ant-table-thead">
    <tr>
      <th key=name>名称</th><th key=type>类型</th><th key=required>是否必须</th><th key=default>默认值</th><th key=desc>备注</th><th key=sub>其他信息</th>
    </tr>
  </thead><tbody className="ant-table-tbody"><tr key=0-0><td key=0><span style="padding-left: 0px"><span style="color: #8c8a8a"></span> message</span></td><td key=1><span>object</span></td><td key=2>非必须</td><td key=3></td><td key=4><span style="white-space: pre-wrap"></span></td><td key=5></td></tr><tr key=0-0-0><td key=0><span style="padding-left: 20px"><span style="color: #8c8a8a">├─</span> id</span></td><td key=1><span>number</span></td><td key=2>非必须</td><td key=3></td><td key=4><span style="white-space: pre-wrap"></span></td><td key=5></td></tr><tr key=0-0-1><td key=0><span style="padding-left: 20px"><span style="color: #8c8a8a">├─</span> name</span></td><td key=1><span>string</span></td><td key=2>非必须</td><td key=3></td><td key=4><span style="white-space: pre-wrap"></span></td><td key=5></td></tr><tr key=0-0-2><td key=0><span style="padding-left: 20px"><span style="color: #8c8a8a">├─</span> age</span></td><td key=1><span>null</span></td><td key=2>非必须</td><td key=3></td><td key=4><span style="white-space: pre-wrap"></span></td><td key=5></td></tr><tr key=0-0-3><td key=0><span style="padding-left: 20px"><span style="color: #8c8a8a">├─</span> phone</span></td><td key=1><span>null</span></td><td key=2>非必须</td><td key=3></td><td key=4><span style="white-space: pre-wrap"></span></td><td key=5></td></tr><tr key=0-0-4><td key=0><span style="padding-left: 20px"><span style="color: #8c8a8a">├─</span> gender</span></td><td key=1><span>null</span></td><td key=2>非必须</td><td key=3></td><td key=4><span style="white-space: pre-wrap"></span></td><td key=5></td></tr><tr key=0-0-5><td key=0><span style="padding-left: 20px"><span style="color: #8c8a8a">├─</span> addr</span></td><td key=1><span>null</span></td><td key=2>非必须</td><td key=3></td><td key=4><span style="white-space: pre-wrap"></span></td><td key=5></td></tr><tr key=0-0-6><td key=0><span style="padding-left: 20px"><span style="color: #8c8a8a">├─</span> createTime</span></td><td key=1><span>string</span></td><td key=2>非必须</td><td key=3></td><td key=4><span style="white-space: pre-wrap"></span></td><td key=5></td></tr><tr key=0-0-7><td key=0><span style="padding-left: 20px"><span style="color: #8c8a8a">├─</span> nation</span></td><td key=1><span>null</span></td><td key=2>非必须</td><td key=3></td><td key=4><span style="white-space: pre-wrap"></span></td><td key=5></td></tr><tr key=0-0-8><td key=0><span style="padding-left: 20px"><span style="color: #8c8a8a">├─</span> instruction</span></td><td key=1><span>null</span></td><td key=2>非必须</td><td key=3></td><td key=4><span style="white-space: pre-wrap"></span></td><td key=5></td></tr><tr key=0-0-9><td key=0><span style="padding-left: 20px"><span style="color: #8c8a8a">├─</span> images</span></td><td key=1><span>null</span></td><td key=2>非必须</td><td key=3></td><td key=4><span style="white-space: pre-wrap"></span></td><td key=5></td></tr><tr key=0-0-10><td key=0><span style="padding-left: 20px"><span style="color: #8c8a8a">├─</span> birthday</span></td><td key=1><span>null</span></td><td key=2>非必须</td><td key=3></td><td key=4><span style="white-space: pre-wrap"></span></td><td key=5></td></tr><tr key=0-1><td key=0><span style="padding-left: 0px"><span style="color: #8c8a8a"></span> status</span></td><td key=1><span>number</span></td><td key=2>非必须</td><td key=3></td><td key=4><span style="white-space: pre-wrap"></span></td><td key=5></td></tr><tr key=0-2><td key=0><span style="padding-left: 0px"><span style="color: #8c8a8a"></span> error</span></td><td key=1><span>string</span></td><td key=2>非必须</td><td key=3></td><td key=4><span style="white-space: pre-wrap"></span></td><td key=5></td></tr>
               </tbody>
              </table>


## 获取角色

<a id=获取角色> </a>

### 基本信息

**Path：** /roles/list

**Method：** POST

**接口描述：**


### 请求参数

**Headers**

| 参数名称      | 参数值           | 是否必须 | 示例 | 备注 |
| ------------- | ---------------- | -------- | ---- | ---- |
| Content-Type  | application/json | 是       |      |      |
| Authorization |                  | 是       |      |      |

**Body**

<table>
  <thead class="ant-table-thead">
    <tr>
      <th key=name>名称</th><th key=type>类型</th><th key=required>是否必须</th><th key=default>默认值</th><th key=desc>备注</th><th key=sub>其他信息</th>
    </tr>
  </thead><tbody className="ant-table-tbody">
               </tbody>
              </table>


### 返回数据

<table>
  <thead class="ant-table-thead">
    <tr>
      <th key=name>名称</th><th key=type>类型</th><th key=required>是否必须</th><th key=default>默认值</th><th key=desc>备注</th><th key=sub>其他信息</th>
    </tr>
  </thead><tbody className="ant-table-tbody"><tr key=0-0><td key=0><span style="padding-left: 0px"><span style="color: #8c8a8a"></span> message</span></td><td key=1><span>object []</span></td><td key=2>非必须</td><td key=3></td><td key=4><span style="white-space: pre-wrap"></span></td><td key=5><p key=3><span style="font-weight: '700'">item 类型: </span><span>object</span></p></td></tr><tr key=0-0-0><td key=0><span style="padding-left: 20px"><span style="color: #8c8a8a">├─</span> id</span></td><td key=1><span>number</span></td><td key=2>必须</td><td key=3></td><td key=4><span style="white-space: pre-wrap"></span></td><td key=5></td></tr><tr key=0-0-1><td key=0><span style="padding-left: 20px"><span style="color: #8c8a8a">├─</span> roleName</span></td><td key=1><span>string</span></td><td key=2>必须</td><td key=3></td><td key=4><span style="white-space: pre-wrap"></span></td><td key=5></td></tr><tr key=0-1><td key=0><span style="padding-left: 0px"><span style="color: #8c8a8a"></span> status</span></td><td key=1><span>number</span></td><td key=2>非必须</td><td key=3></td><td key=4><span style="white-space: pre-wrap"></span></td><td key=5></td></tr>
               </tbody>
              </table>

​            