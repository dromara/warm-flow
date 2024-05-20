package com.warm.flow.core.utils;

/**
 * @author warm
 * @description: Object 工具类
 * @date: 2023/5/18 9:42
 */
public class ObjectUtil {

    /**
     * 判断一个对象是否为空
     *
     * @param object Object
     * @return true：为空 false：非空
     */
    public static boolean isNull(Object object) {
        return object == null;
    }

    /**
     * 判断一个对象是否非空
     *
     * @param object Object
     * @return true：非空 false：空
     */
    public static boolean isNotNull(Object object) {
        return !isNull(object);
    }

    @SuppressWarnings("unchecked")
    public static <T> T cast(Object obj) {
        return (T) obj;
    }

    /**
     * 判断字符串是否为true
     *
     * @param str
     * @return
     */
    public static boolean isStrTrue(String str) {
        return StringUtils.isNotEmpty(str) && "true".equals(str);
    }
}
