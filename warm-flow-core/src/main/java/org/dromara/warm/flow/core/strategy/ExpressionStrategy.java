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
package org.dromara.warm.flow.core.strategy;

import org.dromara.warm.flow.core.constant.ExceptionCons;
import org.dromara.warm.flow.core.exception.FlowException;
import org.dromara.warm.flow.core.utils.StringUtils;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 策略类接口
 *
 * @author warm
 */
public interface ExpressionStrategy<T> {

    /**
     * 获取策略类型
     *
     * @return 类型
     */
    String getType();

    /**
     * 执行表达式
     *
     * @param expression 表达式
     * @param variable   流程变量
     * @return 执行结果
     */
    T eval(String expression, Map<String, Object> variable);


    void setExpression(ExpressionStrategy<T> expressionStrategy);

    /**
     * 获取表达式对应的值
     *
     * @param expression 变量表达式，比如“@@default@@|${flag}” ，或者自定义策略
     * @param variable   流程变量
     * @return 执行结果
     */
    static <T> T getValue(Map<String, ExpressionStrategy<T>> map, String expression, Map<String, Object> variable) {
        AtomicReference<T> result = new AtomicReference<>();
        if (StringUtils.isNotEmpty(expression)) {
            map.forEach((k, v) -> {
                if (expression.startsWith(k + "|")) {
                    if (v == null) {
                        throw new FlowException(ExceptionCons.NULL_EXPRESSION_STRATEGY);
                    }
                    result.set(v.eval(expression.replace(k + "|", ""), variable));
                }
            });
        }

        return result.get();
    }
}
