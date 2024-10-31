/*
 *    Copyright 2024-2025, Warm-Flow (290631660@qq.com).
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.dromara.warm.flow.core.utils;

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
