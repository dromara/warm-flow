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
package com.warm.expression.spel;

import com.warm.flow.core.constant.FlowCons;
import com.warm.flow.core.expression.ExpressionStrategy;
import com.warm.plugin.modes.sb.utils.SpelHelper;

import java.util.Map;

/**
 * 条件表达式spel
 *
 * @author warm
 */
public class ExpressionStrategySpel implements ExpressionStrategy {

    @Override
    public String getType() {
        return FlowCons.splitAt + "spel" + FlowCons.splitAt;
    }

    /**
     * @param expression @@spel@@|flag@@eq@@4
     * @param variable
     * @return
     */
    @Override
    public boolean eval(String expression, Map<String, Object> variable) {
        return Boolean.TRUE.equals(SpelHelper.parseExpression(expression));
    }
}
