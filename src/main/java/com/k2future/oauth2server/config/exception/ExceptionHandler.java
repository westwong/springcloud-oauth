package com.k2future.oauth2server.config.exception;



import com.k2future.oauth2server.util.RespBuilder;

import org.apache.commons.codec.Charsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class ExceptionHandler {

    public static final Logger logger = LoggerFactory.getLogger(ExceptionHandler.class);

    @org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)
    @ResponseBody
    public Map<Object, Object> handleException(Exception e, HttpServletRequest request) {
        logger.error("unhandled Exception", e);
        logger.info("path {} ", request.getServletPath());
        logger.info("payload {} ", new String(((ContentCachingRequestWrapper) request).getContentAsByteArray(), Charsets.UTF_8));
        return RespBuilder.errorJsonStr("A fatal error has occurred");
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    public Map<Object, Object> IllegalArgumentException(IllegalArgumentException e) {
        logger.error("ill {}", e.getMessage());
        return RespBuilder.errorJsonStr(e.getMessage());
    }

    /**
     * secured method ex
     */
    @org.springframework.web.bind.annotation.ExceptionHandler(AccessDeniedException.class)
    @ResponseBody
    public Map<Object, Object> AccessDeniedException(AccessDeniedException e) {
        return RespBuilder.errorJsonStr("Unauthorized", RespBuilder.UNAUTHORIZED);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseBody
    public Map<Object, Object> MissingServletRequestParameterException(MissingServletRequestParameterException e) {
        return RespBuilder.errorJsonStr(e.getMessage());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(BindException.class)
    @ResponseBody
    public Map<Object, Object> BindException(BindException e) {
        logger.info("BindException", e);
        return RespBuilder.errorJsonStr("wrong request param");
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseBody
    public Map<Object, Object> HttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        return RespBuilder.errorJsonStr(e.getMessage());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseBody
    public Map<Object, Object> HttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e) {
        return RespBuilder.errorJsonStr(e.getMessage());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    public Map<Object, Object> HttpMessageNotReadableException(HttpMessageNotReadableException e) {
        logger.error("{}", e.getMessage());
        return RespBuilder.errorJsonStr("need json body or check your time style is " +
                "yyyy-MM-dd HH:mm:ss or yyyy-MM-dd ");
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public Map<Object, Object> MethodArgumentNotValidException(MethodArgumentNotValidException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        assert fieldError != null;
        return RespBuilder.errorJsonStr(fieldError.getField() + " " + fieldError.getRejectedValue() + "?");
    }


    /**
     * we should choose the right path, not just the easy path
     */
    @org.springframework.web.bind.annotation.ExceptionHandler(NullPointerException.class)
    @ResponseBody
    public Map<Object, Object> NullPointerException(NullPointerException e) {
        logger.error("NullPointerException ", e);
        return RespBuilder.errorJsonStr("参照文档检查, 必传参数不为空且值有效");
    }


}
