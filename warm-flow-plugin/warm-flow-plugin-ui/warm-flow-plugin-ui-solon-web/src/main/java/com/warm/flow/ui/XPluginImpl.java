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
package com.warm.flow.ui;

import com.warm.flow.core.config.WarmFlow;
import com.warm.flow.ui.config.WarmFlowUiConfig;
import com.warm.flow.ui.controller.WarmFlowUiController;
import org.noear.solon.Solon;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.web.staticfiles.StaticMappings;
import org.noear.solon.web.staticfiles.repository.ClassPathStaticRepository;

/**
 * Warm-Flow工作流插件
 *
 * @author warm
 */
public class XPluginImpl implements Plugin {


    @Override
    public void start(AppContext context) {
        context.beanMake(WarmFlowUiConfig.class);
        WarmFlow warmFlow = Solon.context().getBean(WarmFlow.class);
        if (warmFlow.isUi()) {
            String uiPath = "/";
            StaticMappings.add("/", new ClassPathStaticRepository("META-INF/resources"));
            Solon.app().add(uiPath, WarmFlowUiController.class);
        }
    }
}
