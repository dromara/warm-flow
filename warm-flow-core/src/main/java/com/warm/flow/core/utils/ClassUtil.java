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
package com.warm.flow.core.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Objects;

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

    /**
     * 通过反射实现对象克隆
     *
     * @param origin
     * @param <C>
     * @return
     */
    public static <C> C clone(C origin) {
        if (Objects.isNull(origin)) {
            return null;
        }
        try {
            // 获取对象的类信息
            Class<C> clazz = (Class<C>) origin.getClass();
            // 创建新的对象实例
            Constructor<C> constructors = clazz.getConstructor();
            // 创建一个对象
            C instance = constructors.newInstance();
            // 获取对象的所有字段
            Field[] fields = clazz.getDeclaredFields();
            // 遍历字段进行赋值
            for (Field field : fields) {
                // 设置可访问性
                makeAccessible(field);
                // 跳过静态字段和常量字段
                int modifiers = field.getModifiers();
                if (Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers)) {
                    continue;
                }
                // 获取字段的值，并设置到克隆对象中
                Object value = field.get(origin);
                field.set(instance, value);
            }

            return instance;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 让指定字段变为可访问
     *
     * @param field
     */
    public static void makeAccessible(Field field) {
        if ((!Modifier.isPublic(field.getModifiers())
                || !Modifier.isPublic(field.getDeclaringClass().getModifiers())
                || Modifier.isFinal(field.getModifiers()))
                && !field.isAccessible()) {
            field.setAccessible(true);
        }
    }
}
