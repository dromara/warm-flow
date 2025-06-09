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

import org.dromara.warm.flow.core.exception.FlowException;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class ReflectionUtil {

    /**
     * 扫描指定包路径下的所有接口，并获取每个接口的第一个直接实现类的对象
     *
     * @param interfacePath 接口路径
     * @param implementPath 实现类路径
     * @return 实现了指定接口的类的对象集合
     */
    public static Map<Class<?>, Object> scanAndInstance(String interfacePath, String implementPath) {
        Map<Class<?>, Object> instances = new HashMap<>();
        try {
            Set<Class<?>> interfaces = ClassUtil.findClasses(interfacePath);
            for (Class<?> interfaceClass : interfaces) {
                Set<Class<?>> implementSet = ClassUtil.findClasses(implementPath);
                Class<?> implementationClass = implementSet.stream().filter(clazz -> isDirectImplementation(clazz, interfaceClass)
                                && !clazz.isInterface() && !Modifier.isAbstract(clazz.getModifiers()))
                        .findFirst().orElse(null);

                if (implementationClass != null) {
                    Object instance = implementationClass.getDeclaredConstructor().newInstance();
                    instances.put(interfaceClass, instance);
                }
            }
        } catch (Exception e) {
            throw new FlowException("扫描异常");
        }

        return instances;
    }

    /**
     * 扫描指定包路径下的所有接口，并获取每个接口的第一个直接实现类的对象
     *
     * @param interfacePath 接口路径
     * @param implementPath 实现类路径
     * @return 实现了指定接口的类的对象集合
     */
    public static Map<Class<?>, Class<?>> scanAndClass(String interfacePath, String implementPath) {
        Map<Class<?>, Class<?>> instances = new HashMap<>();
        try {
            Set<Class<?>> interfaces = ClassUtil.findClasses(interfacePath);
            for (Class<?> interfaceClass : interfaces) {
                Set<Class<?>> implementSet = ClassUtil.findClasses(implementPath);
                implementSet.stream().filter(clazz -> isDirectImplementation(clazz, interfaceClass)
                                && !clazz.isInterface() && !Modifier.isAbstract(clazz.getModifiers()))
                        .findFirst().ifPresent(implementationClass -> instances.put(interfaceClass, implementationClass));

            }
        } catch (Exception e) {
            throw new FlowException("扫描异常");
        }


        return instances;
    }

    /**
     * 扫描指定包路径下的所有接口，并获取每个接口的第一个直接实现类的Supplier
     *
     * @param interfacePath 接口路径
     * @param implementPath 实现类路径
     * @return 实现了指定接口的类的Supplier集合
     */
    public static Map<Class<?>, Supplier<?>> scanAndSupplier(String interfacePath, String implementPath) {
        Map<Class<?>, Supplier<?>> suppliers = new HashMap<>();
        try {
            Set<Class<?>> interfaces = ClassUtil.findClasses(interfacePath);
            for (Class<?> interfaceClass : interfaces) {
                Set<Class<?>> implementSet = ClassUtil.findClasses(implementPath);
                implementSet.stream().filter(clazz -> isDirectImplementation(clazz, interfaceClass)
                                && !clazz.isInterface() && !Modifier.isAbstract(clazz.getModifiers()))
                        .findFirst().ifPresent(implementationClass -> suppliers.put(interfaceClass, () -> {
                            try {
                                return implementationClass.getDeclaredConstructor().newInstance();
                            } catch (Exception e) {
                                throw new FlowException("扫描异常");
                            }
                        }));

            }
        } catch (Exception e) {
            throw new FlowException("扫描异常");
        }

        return suppliers;
    }

    /**
     * 检查 clazz 是否直接实现了 interfaceClass
     *
     * @param clazz          类
     * @param interfaceClass 接口
     * @return 是否直接实现了接口
     */
    private static boolean isDirectImplementation(Class<?> clazz, Class<?> interfaceClass) {
        // 获取 clazz 直接实现的所有接口
        Class<?>[] interfaces = clazz.getInterfaces();
        for (Class<?> iface : interfaces) {
            if (iface.equals(interfaceClass)) {
                return true;
            }
        }
        return false;
    }

}
