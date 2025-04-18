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
package org.dromara.warm.flow.core.variable;

import java.util.Map;

/**
 * 默认办理人表达式策略： @@default@@|${flag}
 *
 * @author warm
 * @see <a href="https://warm-flow.dromara.org/master/advanced/variableStategy.html">文档地址</a>
 */
public class DefaultVariableStrategy implements VariableStrategy {

    @Override
    public String getType() {
        return "$";
    }

    @Override
    public Object preEval(String expression, Map<String, Object> variable) {
        String result = expression.replace("${", "").replace("}", "");
        return variable.get(result);
    }

}
