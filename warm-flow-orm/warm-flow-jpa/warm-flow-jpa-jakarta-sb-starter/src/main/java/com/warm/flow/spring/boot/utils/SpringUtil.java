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
package com.warm.flow.spring.boot.utils;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;

import static org.springframework.core.ResolvableType.forRawClass;

/**
 * @author PMB
 */
@Component
public class SpringUtil implements BeanFactoryPostProcessor {


    /**
     * Spring应用上下文环境
     */
    private static ConfigurableListableBeanFactory beanFactory;


    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        SpringUtil.beanFactory = beanFactory;
    }


    /**
     * 通过class获取Bean
     */
    public static <M> M getBean(Class<M> clazz) {
        ObjectProvider<M> objectProvider = beanFactory.getBeanProvider(forRawClass(clazz), false);
        return objectProvider.getIfAvailable();
    }

}

