package com.warm.flow.core.utils;

import java.util.Map;

/**
 * @author warm
 * @description: map工具类
 * @date: 2023/5/18 9:46
 */
public class MapUtil {

    /**
     * * 判断一个Map是否为空
     *
     * @param map 要判断的Map
     * @return true：为空 false：非空
     */
    public static boolean isEmpty(Map<?, ?> map) {
        return ObjectUtil.isNull(map) || map.isEmpty();
    }

    /**
     * * 判断一个Map是否为空
     *
     * @param map 要判断的Map
     * @return true：非空 false：空
     */
    public static boolean isNotEmpty(Map<?, ?> map) {
        return !isEmpty(map);
    }

    /**
     * 如果Map是空，则返回默认值
     *
     * @param map        Map
     * @param defaultMap 默认值
     * @return 结果
     */
    public static <K, V> Map<K, V> emptyDefault(Map<K, V> map, Map<K, V> defaultMap) {
        return isEmpty(map) ? defaultMap : map;
    }
}
