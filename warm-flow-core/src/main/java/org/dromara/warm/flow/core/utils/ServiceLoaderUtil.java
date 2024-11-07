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

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.*;

/**
 * SPI机制中的服务加载工具类
 *
 * @author warm
 */
public class ServiceLoaderUtil {

    /**
     * 。加载第一个可用服务，如果用户定义了多个接口实现类，只获取第一个不报错的服务
     *
     * @param <T>   接口类型
     * @param clazz 服务接口
     * @return 第一个服务接口实现对象，无实现返回{@code null}
     */
    public static <T> T loadFirst(Class<T> clazz) {
        final Iterator<T> iterator = load(clazz).iterator();
        while (iterator.hasNext()) {
            try {
                return iterator.next();
            } catch (ServiceConfigurationError ignore) {

            }
        }
        return null;
    }

    /**
     * 加载服务 并已list列表返回
     *
     * @param <T>   接口类型
     * @param clazz 服务接口
     * @return 服务接口实现列表
     * @since 5.4.2
     */
    public static <T> List<T> loadList(Class<T> clazz) {
        List<T> list = new ArrayList<>();
        ServiceLoader<T> serviceLoader = load(clazz);
        Iterator<T> serviceIterator = serviceLoader.iterator();
        while (serviceIterator.hasNext()) {
            try {
                list.add(serviceIterator.next());
            } catch (ServiceConfigurationError ignore) {

            }
        }
        return list;
    }

    /**
     * 加载服务
     *
     * @param <T>   接口类型
     * @param clazz 服务接口
     * @return 服务接口实现列表
     */
    public static <T> ServiceLoader<T> load(Class<T> clazz) {
        return load(clazz, null);
    }

    /**
     * 加载服务
     *
     * @param <T>    接口类型
     * @param clazz  服务接口
     * @param loader {@link ClassLoader}
     * @return 服务接口实现列表
     */
    public static <T> ServiceLoader<T> load(Class<T> clazz, ClassLoader loader) {
        return ServiceLoader.load(clazz, ObjectUtil.defaultIfNull(loader, ServiceLoaderUtil::getClassLoader));
    }

    /**
     * 获取{@link ClassLoader}<br>
     * 获取顺序如下：<br>
     *
     * <pre>
     * 1、获取当前线程的ContextClassLoader
     * 2、获取当前类对应的ClassLoader
     * 3、获取系统ClassLoader（{@link ClassLoader#getSystemClassLoader()}）
     * </pre>
     *
     * @return 类加载器
     */
    public static ClassLoader getClassLoader() {
        ClassLoader classLoader = getContextClassLoader();
        if (classLoader == null) {
            classLoader = ServiceLoaderUtil.class.getClassLoader();
            if (null == classLoader) {
                classLoader = getSystemClassLoader();
            }
        }
        return classLoader;
    }

    /**
     * 获取当前线程的{@link ClassLoader}
     *
     * @return 当前线程的class loader
     * @see Thread#getContextClassLoader()
     */
    public static ClassLoader getContextClassLoader() {
        if (System.getSecurityManager() == null) {
            return Thread.currentThread().getContextClassLoader();
        } else {
            // 绕开权限检查
            return AccessController.doPrivileged(
                    (PrivilegedAction<ClassLoader>) () -> Thread.currentThread().getContextClassLoader());
        }
    }

    /**
     * 获取系统{@link ClassLoader}
     *
     * @return 系统{@link ClassLoader}
     * @see ClassLoader#getSystemClassLoader()
     * @since 5.7.0
     */
    public static ClassLoader getSystemClassLoader() {
        if (System.getSecurityManager() == null) {
            return ClassLoader.getSystemClassLoader();
        } else {
            // 绕开权限检查
            return AccessController.doPrivileged(
                    (PrivilegedAction<ClassLoader>) ClassLoader::getSystemClassLoader);
        }
    }

}
