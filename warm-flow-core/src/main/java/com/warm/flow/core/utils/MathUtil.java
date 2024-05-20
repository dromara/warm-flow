package com.warm.flow.core.utils;

import java.math.BigDecimal;

/**
 * 数字工具类
 *
 * @author warm
 */
public class MathUtil {

    private MathUtil() {
    }

    /**
     * 判断是否为数字
     *
     * @param str 字符串
     */
    public static boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) { // 检查字符串是否为空
            return false;
        }
        try {
            Double.parseDouble(str.trim()); // 使用Double.parseDouble()方法尝试将字符串转换为double
            return true; // 转换成功，字符串是数字
        } catch (NumberFormatException e) {
            return false; // 转换失败，字符串不是数字
        }
    }

    /**
     * 判断数字大小
     *
     * @param n1 字符串
     * @param n2 字符串
     */
    public static int determineSize(String n1, String n2) {
        BigDecimal a = new BigDecimal(n1);
        BigDecimal b = new BigDecimal(n2);
        return a.compareTo(b);
    }

}
