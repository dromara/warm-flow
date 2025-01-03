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
package org.dromara.warm.plugin.modes.solon;

import org.dromara.warm.flow.core.utils.ReflectionUtil;
import org.dromara.warm.plugin.modes.solon.config.BeanConfig;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.Plugin;

import java.util.Map;

/**
 * Warm-Flow工作流插件
 *
 * @author warm
 */
public class XPluginImpl implements Plugin {


    @Override
    public void start(AppContext context) {
        Map<Class<?>, Object> beans = ReflectionUtil.scanAndInstance("org.dromara.warm.flow.core.orm.dao"
                , "org.dromara.warm.flow.orm.dao");
        beans.forEach((clazz, bean) -> {
            String simpleName = clazz.getSimpleName();
            String beanName = Character.toLowerCase(simpleName.charAt(0)) + simpleName.substring(1);
            BeanWrap beanWrap = context.wrap(beanName, bean);
            context.putWrap(clazz, beanWrap);
        });
        context.beanMake(BeanConfig.class);

    }
}
