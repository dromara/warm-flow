package com.monkey.tools.utils;

/**
 * @description:  唯一id
 * @author minliuhua
 * @date: 2023/5/17 23:08
 */
public class IdUtils {

	private volatile static SnowFlake instance;

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
