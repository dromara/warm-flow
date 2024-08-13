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

import com.easy.query.api.proxy.client.EasyBaseQuery;
import com.easy.query.api.proxy.client.EasyEntityQuery;
import com.easy.query.core.configuration.QueryConfiguration;
import com.easy.query.core.context.QueryRuntimeContext;
import com.warm.flow.orm.config.WarmFlowLogicDeleteStrategy;
import com.warm.flow.solon.config.FlowAutoConfig;
import org.noear.solon.Solon;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;
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
        context.beanMake(FlowAutoConfig.class);
        log.debug("warm插件加载: 成功加载[Warm-Flow工作流]插件");
    }
}