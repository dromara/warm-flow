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
package org.dromara.warm.plugin.modes.solon.expression;

import org.dromara.warm.flow.core.strategy.ListenerStrategy;
import org.dromara.warm.plugin.modes.solon.helper.SnElHelper;

import java.util.Map;

/**
 * snel监听器表达式 #{@user.eval()}
 *
 * @author warm
 */
public class ListenerStrategySnEl implements ListenerStrategy {

    @Override
    public String getType() {
        return "#";
    }

    @Override
    public Boolean eval(String expression, Map<String, Object> variable) {
        SnElHelper.parseExpression(expression, variable);
        // 恒返回true，说明匹配上监听器表达式，扩展策略也一定要返回true
        return true;
    }
}
