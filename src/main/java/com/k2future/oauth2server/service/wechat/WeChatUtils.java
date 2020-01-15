package com.k2future.oauth2server.service.wechat;

import com.alibaba.fastjson.JSON;
import com.k2future.oauth2server.entity.WeChatMessage;

import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * @author West
 * @date create in 2019/11/7
 */
public class WeChatUtils {

    /**
     * Api key
     */
    private static final String APP_ID = "";
    /**
     * Api key
     */
    private static final String APP_KEY = "";
    /**
     * 地址解析成坐标
     */
    private static final String AUTH_API = "https://api.weixin.qq.com/sns/jscode2session?appid={appId}&secret={appKey}&js_code={code}&grant_type=authorization_code";

    /**
     * 获取openId
     * @param code 参见文档
     * @return openId
     */
    public static String getOpenId(String code) {
        return getMessage(code).getOpenId();
    }

    /**
     * 获取微信认证信息
     * @param code 参见文档
     *  https://developers.weixin.qq.com/miniprogram/dev/framework/open-ability/login.html
     * @return message
     */
    public static WeChatMessage getMessage(String code) {

        Map<String, String> params = new HashMap<>(10);
        params.put("appId", APP_ID);
        params.put("appKey", APP_KEY);
        params.put("code",code);
        RestTemplate restTemplate = new RestTemplate();
        String msg = restTemplate.getForObject(AUTH_API,String.class, params);
        WeChatMessage message = JSON.parseObject(msg, WeChatMessage.class);
        // 腾讯
        if (message.success()) {
            return message;
        } else {
            throw new IllegalArgumentException("获取openId失败");
        }
    }

    public static void main(String[] args) {
        String openID = getOpenId("033nD0hY0wVevX1a1oiY0K4NgY0nD0hF");
        System.out.println(openID);
    }

}