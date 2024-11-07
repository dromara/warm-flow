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
package org.dromara.warm.flow.solon.config;


import com.easy.query.api.proxy.client.EasyEntityQuery;
import com.easy.query.core.configuration.QueryConfiguration;
import com.easy.query.core.context.QueryRuntimeContext;
import com.easy.query.solon.annotation.Db;
import org.dromara.warm.flow.core.FlowFactory;
import org.dromara.warm.flow.core.config.WarmFlow;
import org.dromara.warm.flow.core.invoker.FrameInvoker;
import org.dromara.warm.flow.orm.config.WarmFlowLogicDeleteFakeStrategy;
import org.dromara.warm.flow.orm.config.WarmFlowLogicDeleteStrategy;
import org.dromara.warm.plugin.modes.solon.config.BeanConfig;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;

/**
 * 工作流bean注册配置
 *
 * @author warm
 * @since 2023/6/5 23:01
 */
@Configuration
public class FlowAutoConfig extends BeanConfig {

    @Bean
    public WarmFlow after(@Db EasyEntityQuery entityQuery, WarmFlow flowConfig) {
        FrameInvoker.setBeanFunction((clazz)->{
            if (clazz.isAssignableFrom(EasyEntityQuery.class)) {
                return entityQuery;
            }
            return Solon.context().getBean(clazz);
        });

        //region 注入逻辑删除策略 http://www.easy-query.com/easy-query-doc/guide/adv/logic-delete.html
        QueryRuntimeContext runtimeContext = entityQuery.getRuntimeContext();
        QueryConfiguration queryConfiguration = runtimeContext.getQueryConfiguration();

        //  逻辑删除策略
        if (flowConfig.isLogicDelete()){
            // 注入正常的逻辑删除策略
            queryConfiguration.applyLogicDeleteStrategy(new WarmFlowLogicDeleteStrategy());
        }else{
            // 注入空的逻辑删除策略（同名的空逻辑 逻辑删除策略，保证eq运行不会因找不到策略报错）
            queryConfiguration.applyLogicDeleteStrategy(new WarmFlowLogicDeleteFakeStrategy());
        }
        //endregion


        return FlowFactory.getFlowConfig();
    }
}
