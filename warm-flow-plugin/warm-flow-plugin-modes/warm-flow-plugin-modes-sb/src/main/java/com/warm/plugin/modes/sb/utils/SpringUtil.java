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
package com.warm.plugin.modes.sb.utils;

import com.warm.flow.core.exception.FlowException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author PMB
 */
@Component
public class SpringUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    private static ConfigurableListableBeanFactory beanFactory;

    /**
     * 获取applicationContext，应用上下文
     *
     * @return
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (SpringUtil.applicationContext == null) {
            SpringUtil.applicationContext = applicationContext;
        }
    }

    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        SpringUtil.beanFactory = beanFactory;
    }
    /**
     * 通过class获取Bean
     */
    public static <M> M getBean(Class<M> clazz) {
        return getApplicationContext().getBean(clazz);
    }

    public static ListableBeanFactory getBeanFactory() {
        ListableBeanFactory factory = null == beanFactory ? applicationContext : beanFactory;
        if (null == factory) {
            throw new FlowException("No ConfigurableListableBeanFactory or ApplicationContext injected, maybe not in the Spring environment?");
        } else {
            return factory;
        }
    }
}

