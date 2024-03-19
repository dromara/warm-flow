package com.warm.flow.core.utils;

/**
 * 类工具类
 *
 * @author warm
 */
public class ClassUtil {
    /**
     * 通过包名获取Class对象
     */
    public static Class<?> getClazz(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
}
