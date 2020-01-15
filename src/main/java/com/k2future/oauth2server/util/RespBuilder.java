package com.k2future.oauth2server.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

/**
 * json response builder
 */
public class RespBuilder {

    public static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        // 去掉各种@JsonSerialize注解的解析
        objectMapper.configure(MapperFeature.USE_ANNOTATIONS, false);
        // 只针对非空的值进行序列化
//        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
//        // 将类型序列化到属性json字符串中
//        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);

        //因为前端js的数字精度一般只到16位，所以long数字过大，会导致错误，所以把long转为string发送到前端
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
        simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);

        objectMapper.registerModule(simpleModule);
        objectMapper.registerModule(new JavaTimeModule());
    }

    public static final int SUCCESS = 200;
    public static final int UNAUTHORIZED = 401;

    public static final int  FORBIDDEN= 403;

    public static final int ERROR_NORMAL = 500;

    public static final String DATA_KEY = "message";
    public static final String ERROR_KEY = "error";
    public static final String STATUS_KEY = "status";


    public static Map<Object, Object> kv2Json(Object... kvs) {
        Map<Object, Object> map = newMapDefaultSuccess();
        if (kvs.length == 1) {
            map.put(DATA_KEY, kvs[0]);
        } else {
            Assert.isTrue((kvs.length & 1) == 0, "kvs must appear in pairs");
            HashMap<Object, Object> map2 = new HashMap<>();
            for (int i = 0; i < kvs.length; i = i + 2) {
                map2.put(kvs[i], kvs[i + 1]);
            }
            map.put(DATA_KEY, map2);
        }
        return map;
    }

    public static String toJsonString(Object o) {
        try {
            return objectMapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Object to jsonStr error!" + o);
        }
    }

    public static Map<Object, Object> errorJsonStr(String msg, int errorCode) {
        Map<Object, Object> map = newMapDefaultSuccess();
        map.put(STATUS_KEY, errorCode);
        map.put(ERROR_KEY, msg);
        return map;
    }

    public static Map<Object, Object> errorJsonStr(Exception e) {
        Map<Object, Object> map = newMapDefaultSuccess();
        map.put(STATUS_KEY, ERROR_NORMAL);
        map.put(ERROR_KEY, e.getMessage());
        return map;
    }

    public static Map<Object, Object> errorJsonStr(String msg) {
        return errorJsonStr(msg, ERROR_NORMAL);
    }


    private static Map<Object, Object> newMapDefaultSuccess() {
        HashMap<Object, Object> map = new HashMap<>();
        map.put(STATUS_KEY, SUCCESS);
        return map;
    }

    public static Map<Object, Object> succ() {
        return newMapDefaultSuccess();
    }
}
