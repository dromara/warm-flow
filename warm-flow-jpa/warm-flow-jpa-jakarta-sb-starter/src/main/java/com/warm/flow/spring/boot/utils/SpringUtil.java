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

