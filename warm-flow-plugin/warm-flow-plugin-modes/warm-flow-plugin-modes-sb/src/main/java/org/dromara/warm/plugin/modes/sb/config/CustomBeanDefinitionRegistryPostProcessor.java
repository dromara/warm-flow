package org.dromara.warm.plugin.modes.sb.config;

import org.dromara.warm.flow.core.utils.ReflectionUtil;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CustomBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        Map<Class<?>, Class<?>> beans = ReflectionUtil.scanAndClass("org.dromara.warm.flow.core.orm.dao"
                , "org.dromara.warm.flow.orm.dao");
        beans.forEach((clazz, bean) -> {
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(bean);
            String simpleName = clazz.getSimpleName();
            String beanName = Character.toLowerCase(simpleName.charAt(0)) + simpleName.substring(1);
            registry.registerBeanDefinition(beanName, builder.getBeanDefinition());
        });

    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        // 可以在这里进行其他BeanFactory的配置
    }
}
