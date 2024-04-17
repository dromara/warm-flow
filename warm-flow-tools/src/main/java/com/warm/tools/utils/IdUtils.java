package com.warm.tools.utils;

/**
 * @author minliuhua
 * @description: 唯一id
 * @date: 2023/5/17 23:08
 */
public class IdUtils {

    private volatile static SnowFlake instance;

    public static String nextIdStr() {
        return nextId().toString();
    }

    public static String nextIdStr(long workerId, long datacenterId) {
        if (instance == null) {
            synchronized (SnowFlake.class) {
                if (instance == null) {
                    instance = new SnowFlake(workerId, datacenterId);
                }
            }
        }
        return nextId(workerId, datacenterId).toString();
    }

    public static Long nextId() {
        if (instance == null) {
            synchronized (SnowFlake.class) {
                if (instance == null) {
                    instance = new SnowFlake(1, 1);
                }
            }
        }
        return instance.nextId();
    }

    public static Long nextId(long workerId, long datacenterId) {
        if (instance == null) {
            synchronized (SnowFlake.class) {
                if (instance == null) {
                    instance = new SnowFlake(workerId, datacenterId);
                }
            }
        }
        return instance.nextId();
    }

}
