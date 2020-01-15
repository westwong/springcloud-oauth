package com.k2future.oauth2server.util;


/**
 * @author West
 * @date create in 2019/9/2
 */
public class IdGenerator {
    private static final SnowFlake SNOW_FLAKE = new SnowFlake(1, 1);

    public static long nextId() {
        return SNOW_FLAKE.nextId();
    }
}
