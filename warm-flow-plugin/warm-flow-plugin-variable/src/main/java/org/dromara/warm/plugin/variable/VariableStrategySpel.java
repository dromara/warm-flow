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
package org.dromara.warm.plugin.variable;

import org.dromara.warm.flow.core.constant.FlowCons;
import org.dromara.warm.flow.core.utils.ObjectUtil;
import org.dromara.warm.flow.core.variable.VariableStrategy;
import org.dromara.warm.plugin.spel.SpelHelper;

import java.util.Map;

/**
 * 条件表达式spel: @@spel@@|#{@user.evalVar()}
 *
 * @author warm
 */
public class VariableStrategySpel implements VariableStrategy {

    @Override
    public String getType() {
        return FlowCons.splitAt + "spel" + FlowCons.splitAt;
    }

    @Override
    public String eval(String expression, Map<String, Object> variable) {
        Object o = SpelHelper.parseExpression(expression, variable);
        return ObjectUtil.isNull(o) ? null : o.toString();
    }
}
