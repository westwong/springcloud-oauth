package com.k2future.oauth2server.config.json;

import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.SimpleDateFormatSerializer;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;

import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author West
 * @date create in 2019/9/3
 *
 * 用fastjson主要是因为支持国产但是bug确实有点多。
 * 所以弃用了 ，选择jackson ，但是fastjson 工具还蛮好用的
 * tips: bean 转 js0n --->fastjson
 * json 转 bean --->Gjson
 */
// @Configuration
public class JsonConfig {


    @Bean
    public HttpMessageConverters fastjsonHttpMessageConverter() {
        //定义一个转换消息的对象
        FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();

        //添加fastjson的配置信息
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setSerializerFeatures(SerializerFeature.DisableCircularReferenceDetect, SerializerFeature.PrettyFormat);
//        fastJsonConfig.setSerializerFeatures(SerializerFeature.WriteNullBooleanAsFalse,SerializerFeature.WriteNullStringAsEmpty,
//                SerializerFeature.WriteMapNullValue,SerializerFeature.WriteNullListAsEmpty);
        //设置编码

        //时间
        SerializeConfig config = new SerializeConfig();
        config.put(Date.class, new SimpleDateFormatSerializer("yyyy-MM-dd HH:mm:ss"));
        config.put(LocalDate.class, new SimpleDateFormatSerializer("yyyy-MM-dd"));
        config.put(LocalDateTime.class, new SimpleDateFormatSerializer("yyyy-MM-dd HH:mm:ss"));

        fastJsonConfig.setSerializeConfig(config);

        List<MediaType> fastMediaTypes = new ArrayList<>();
        fastMediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
        fastConverter.setSupportedMediaTypes(fastMediaTypes);
        //在转换器中添加配置信息
        fastConverter.setFastJsonConfig(fastJsonConfig);
        //使用fastjson 在Oauth2可能会有的bug
        fastJsonConfig.setSerializeFilters(new Oauth2SerializeFilter());
        HttpMessageConverter<?> converter = fastConverter;

        return new HttpMessageConverters(converter);

    }
}
