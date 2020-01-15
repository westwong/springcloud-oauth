package com.k2future.oauth2server.util;

/**
 * @author West
 * @date create in 2019/9/4
 */
public class Assert extends org.springframework.util.Assert {

    public static void notTrue(boolean expression, String message) {
        if (expression) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 抛出一个运行时异常
     */
    public static void throwE(String message) {
        throw new IllegalArgumentException(message);
    }
}
