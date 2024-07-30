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
package com.warm.flow.solon.config;


import com.easy.query.api.proxy.client.EasyEntityQuery;
import com.easy.query.solon.annotation.Db;
import com.warm.flow.core.FlowFactory;
import com.warm.flow.core.config.WarmFlow;
import com.warm.flow.core.invoker.FrameInvoker;
import com.warm.plugin.modes.solon.config.BeanConfig;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author warm
 * @description: 工作流bean注册配置
 * @date: 2023/6/5 23:01
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Configuration
public class FlowAutoConfig extends BeanConfig {

    private static final Logger log = LoggerFactory.getLogger(FlowAutoConfig.class);

    @Db
    private EasyEntityQuery entityQuery;

    @Bean
    public WarmFlow initFlow(WarmFlow flowConfig) {
        FrameInvoker.setBeanFunction((clazz)->{
            if (clazz.isAssignableFrom(EasyEntityQuery.class)) {
                return entityQuery;
            }
            return Solon.context().getBean(clazz);
        });
        log.info("【warm-flow】，easy-query的solon扩展包初始化结束");
        return FlowFactory.getFlowConfig();
    }
}
