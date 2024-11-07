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
package org.dromara.warm.flow.spring.boot.config;

import com.easy.query.api.proxy.client.EasyEntityQuery;
import org.dromara.warm.flow.core.config.WarmFlow;
import org.dromara.warm.flow.core.invoker.FrameInvoker;
import org.dromara.warm.flow.orm.config.WarmFlowLogicDeleteFakeStrategy;
import org.dromara.warm.flow.orm.config.WarmFlowLogicDeleteStrategy;
import org.dromara.warm.plugin.modes.sb.config.BeanConfig;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

/**
 * 工作流bean注册配置
 *
 * @author warm
 * @since 2023/6/5 23:01
 */
@Configuration
@ConditionalOnProperty(value = "warm-flow.enabled", havingValue = "true", matchIfMissing = true)
public class FlowAutoConfig extends BeanConfig {

    @Override
    public void after(WarmFlow flowConfig) {

        EasyEntityQuery entityQuery = FrameInvoker.getBean(EasyEntityQuery.class);
        if (entityQuery == null) {
            throw new BeanCreationException("EasyEntityQuery is not found, please check the configuration");
        }
        if (flowConfig.isLogicDelete()) {
            entityQuery.getRuntimeContext().getQueryConfiguration().applyLogicDeleteStrategy(new WarmFlowLogicDeleteStrategy());
        }else{
            entityQuery.getRuntimeContext().getQueryConfiguration().applyLogicDeleteStrategy(new WarmFlowLogicDeleteFakeStrategy());
        }
    }
}
