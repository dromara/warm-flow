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

import org.dromara.warm.flow.core.strategy.ExpressionStrategy;
import org.dromara.warm.flow.core.utils.ObjectUtil;
import org.dromara.warm.flow.core.utils.StreamUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 办理人表达式策略接口
 *
 * @author warm
 * @see <a href="https://warm-flow.dromara.org/master/advanced/variableStategy.html">文档地址</a>
 */
public interface VariableStrategy extends ExpressionStrategy<List<String>> {

    /**
     * 办理人表达式策略实现类集合
     */
    List<ExpressionStrategy<List<String>>> expressionStrategyList = new ArrayList<>();

    default void setExpression(ExpressionStrategy<List<String>> expressionStrategy) {
        expressionStrategyList.add(expressionStrategy);
    }

    Object preEval(String expression, Map<String, Object> variable);

    @Override
    default List<String> eval(String expression, Map<String, Object> variable) {
        return afterEval(preEval(expression, variable));
    }

    default List<String> afterEval(Object o) {
        if (ObjectUtil.isNotNull(o)) {
            if (o instanceof List) {
                return StreamUtils.toList((List<?>) o, Object::toString);
            }
            if (o instanceof Object[]) {
                return Arrays.stream((Object[]) o).map(Object::toString).collect(Collectors.toList());
            }
            return Collections.singletonList(o.toString());
        }
        return null;
    }
}
