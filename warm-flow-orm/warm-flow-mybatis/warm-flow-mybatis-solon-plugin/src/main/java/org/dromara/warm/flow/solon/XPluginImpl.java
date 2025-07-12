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
package org.dromara.warm.flow.solon;

import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.Configuration;
import org.dromara.warm.flow.solon.config.FlowAutoConfig;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.event.EventBus;

import java.util.Arrays;
import java.util.List;

/**
 * Warm-Flow工作流插件
 *
 * @author warm
 */
public class XPluginImpl implements Plugin {

    @Override
    public void start(AppContext context) {
        context.beanMake(FlowAutoConfig.class);
        EventBus.subscribe(Configuration.class, e -> {
            List<String> mapperList = Arrays.asList("warm/flow/FlowDefinitionMapper.xml", "warm/flow/FlowHisTaskMapper.xml"
                    , "warm/flow/FlowInstanceMapper.xml", "warm/flow/FlowNodeMapper.xml", "warm/flow/FlowFormMapper.xml"
                    , "warm/flow/FlowSkipMapper.xml", "warm/flow/FlowTaskMapper.xml", "warm/flow/FlowUserMapper.xml");

            try {
                for (String mapper : mapperList) {
                    XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(Resources.getResourceAsStream(mapper),
                            e, getClass().getResource("/") + mapper, e.getSqlFragments());
                    xmlMapperBuilder.parse();
                }
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
    }
}
