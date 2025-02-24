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

import java.util.function.Supplier;

/**
 * Object 工具类
 *
 * @author warm
 * @since 2023/5/18 9:42
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

    /**
     * 判断字符串是否为true
     *
     * @param str
     * @return
     */
    public static boolean isStrTrue(String str) {
        return StringUtils.isNotEmpty(str) && "true".equals(str);
    }

    /**
     * 如果被检查对象为null， 返回默认值defaultValue；否则直接返回
     *
     * @param source               被检查对象
     * @param defaultValue         默认值
     * @param <T>                  对象类型
     * @throws NullPointerException {@code defaultValueSupplier == null} 时，抛出
     */
    public static <T> T defaultNull(T source, T defaultValue) {
        if (isNull(source)) {
            return defaultValue;
        }
        return source;
    }

    /**
     * 如果被检查对象为null， 返回默认值（由 defaultValueSupplier 提供）；否则直接返回
     *
     * @param source               被检查对象
     * @param defaultValueSupplier 默认值提供者
     * @param <T>                  对象类型
     * @throws NullPointerException {@code defaultValueSupplier == null} 时，抛出
     */
    public static <T> T defaultIfNull(T source, Supplier<? extends T> defaultValueSupplier) {
        if (isNull(source)) {
            return defaultValueSupplier.get();
        }
        return source;
    }
}
