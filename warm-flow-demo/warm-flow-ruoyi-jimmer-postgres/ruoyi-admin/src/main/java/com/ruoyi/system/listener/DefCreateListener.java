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
package com.ruoyi.system.listener;

import org.dromara.warm.flow.core.entity.Instance;
import org.dromara.warm.flow.core.listener.Listener;
import org.dromara.warm.flow.core.listener.ListenerVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DefCreateListener implements Listener {

    private static final Logger log = LoggerFactory.getLogger(DefCreateListener.class);

    @Override
    public void notify(ListenerVariable listenerVariable) {
        log.info("流程创建监听器......");
        Instance instance = listenerVariable.getInstance();
        Map<String, Object> variable = listenerVariable.getVariable();
        log.info("流程创建监听器结束......");
    }
}
