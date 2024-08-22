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
package com.warm.flow.solon;

import com.mybatisflex.core.FlexGlobalConfig;
import com.warm.flow.orm.mapper.*;
import com.warm.flow.solon.config.FlowAutoConfig;
import com.warm.plugin.modes.solon.config.BeanConfig;
import org.apache.ibatis.solon.MybatisAdapter;
import org.apache.ibatis.solon.integration.MybatisAdapterManager;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.event.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Warm-Flow工作流插件
 *
 * @author warm
 */
public class XPluginImpl implements Plugin {

    private static final Logger log = LoggerFactory.getLogger(XPluginImpl.class);

    @Override
    public void start(AppContext context) {
        context.beanMake(BeanConfig.class);
        context.beanMake(FlowAutoConfig.class);
        EventBus.subscribe(FlexGlobalConfig.class, e -> {
            e.getConfiguration().addMapper(FlowDefinitionMapper.class);
            e.getConfiguration().addMapper(FlowHisTaskMapper.class);
            e.getConfiguration().addMapper(FlowInstanceMapper.class);
            e.getConfiguration().addMapper(FlowNodeMapper.class);
            e.getConfiguration().addMapper(FlowSkipMapper.class);
            e.getConfiguration().addMapper(FlowTaskMapper.class);
            e.getConfiguration().addMapper(FlowUserMapper.class);
        });
        context.lifecycle(() -> {
            final MybatisAdapter mybatisAdapter = MybatisAdapterManager.getAll().values().iterator().next();
            context.beanInject(mybatisAdapter.getMapper(FlowDefinitionMapper.class));
            context.beanInject(mybatisAdapter.getMapper(FlowHisTaskMapper.class));
            context.beanInject(mybatisAdapter.getMapper(FlowInstanceMapper.class));
            context.beanInject(mybatisAdapter.getMapper(FlowNodeMapper.class));
            context.beanInject(mybatisAdapter.getMapper(FlowSkipMapper.class));
            context.beanInject(mybatisAdapter.getMapper(FlowTaskMapper.class));
            context.beanInject(mybatisAdapter.getMapper(FlowUserMapper.class));
        });
        log.debug("warm插件加载: 成功加载[Warm-Flow工作流]插件");
    }
}