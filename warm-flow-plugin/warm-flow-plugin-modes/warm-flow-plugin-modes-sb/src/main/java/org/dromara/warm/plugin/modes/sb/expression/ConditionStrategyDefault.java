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
package org.dromara.warm.plugin.modes.sb.expression;


import org.dromara.warm.flow.core.condition.ConditionStrategy;
import org.dromara.warm.plugin.modes.sb.helper.SpelHelper;

import java.util.Map;

/**
 * 默认条件表达式 default@@${flag == 5 && flag > 4}
 *
 * @author warm
 * @see <a href="https://warm-flow.dromara.org/master/primary/condition.html">文档地址</a>
 */
public class ConditionStrategyDefault implements ConditionStrategy {

    @Override
    public String getType() {
        return "default";
    }

    @Override
    public Boolean eval(String expression, Map<String, Object> variable) {
        expression = expression.replace("$", "#");
        for (Map.Entry<String, Object> entry: variable.entrySet()) {
            if (expression.contains(entry.getKey())) {
                expression = expression.replace(entry.getKey(), "#" + entry.getKey());
            }
        }
        return Boolean.TRUE.equals(SpelHelper.parseExpression(expression, variable));
    }
}
