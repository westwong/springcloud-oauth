package com.k2future.oauth2server.service.mobile;

import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.k2future.oauth2server.util.Assert;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 短信发送 使用阿里云短信服务
 * https://help.aliyun.com/document_detail/101414.html
 *
 * @author West
 * @date create in 2019/11/5
 */
@Service("sendMsgService")
public class SendMsgServiceImpl implements SendMsgService {

    /**
     * 阿里云  appKey
     */
    @Value("${ali.message.access-key}")
    private   String accessKey = "";
    /**
     * 阿里云 appSecret
     */
    @Value("${ali.message.access-secret}")
    private  String accessSecret = "";
    /**
     * 模版id
     */
    private static final String TEMPLATE_CODE = "SMS_176880275";
    /**
     * 签名
     */
    private static final String SIGN_NAME = "K2";
    private static final String OK = "OK";


    @Override
    public boolean sendVerificationCode(String mobile, String code) {
        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKey, accessSecret);
        IAcsClient client = new DefaultAcsClient(profile);

        CommonRequest request = new CommonRequest();
        request.setMethod(MethodType.POST);
        request.setDomain("dysmsapi.aliyuncs.com");
        request.setVersion("2017-05-25");
        request.setAction("SendSms");
        request.putQueryParameter("RegionId", "cn-chengdu");
        request.putQueryParameter("PhoneNumbers", mobile);
        request.putQueryParameter("SignName", SIGN_NAME);
        request.putQueryParameter("TemplateCode", TEMPLATE_CODE);
        request.putQueryParameter("TemplateParam", "{code:" + code + "}");
        try {
            CommonResponse response = client.getCommonResponse(request);
            String data = response.getData();
            JSONObject jsonObject = JSONObject.parseObject(data);
            Assert.isTrue(OK.equals(jsonObject.getString("Code")), jsonObject.getString("Message"));
            return true;
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }
        return false;
    }
}
