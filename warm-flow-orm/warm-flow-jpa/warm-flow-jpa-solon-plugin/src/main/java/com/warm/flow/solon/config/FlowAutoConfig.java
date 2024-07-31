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

import org.noear.solon.annotation.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author warm
 * @description: 工作流bean注册配置
 * @date: 2023/6/5 23:01
 */
@Configuration
public class FlowAutoConfig {

    private static final Logger log = LoggerFactory.getLogger(FlowAutoConfig.class);

    static {
        log.info("【warm-flow】，jpa的solon扩展包初始化开始");
    }

}
