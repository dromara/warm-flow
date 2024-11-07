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
package org.dromara.warm.flow.core.utils;

import org.dromara.warm.flow.core.constant.ExceptionCons;
import org.dromara.warm.flow.core.exception.FlowException;
import org.dromara.warm.flow.core.expression.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 条件表达式工具类
 *
 * @author warm
 */
public class ExpressionUtil {

    private static final Map<String, ExpressionStrategy> map = new HashMap<>();

    private ExpressionUtil() {
    }

    static {
        setExpression(new ExpressionStrategyEq());
        setExpression(new ExpressionStrategyGe());
        setExpression(new ExpressionStrategyGt());
        setExpression(new ExpressionStrategyLe());
        setExpression(new ExpressionStrategyLike());
        setExpression(new ExpressionStrategyLt());
        setExpression(new ExpressionStrategyNe());
        setExpression(new ExpressionStrategyNotLike());
    }

    public static void setExpression(ExpressionStrategy expressionStrategy) {
        map.put(expressionStrategy.getType(), expressionStrategy);
    }

    /**
     * @param expression 条件表达式，比如“@@eq@@|flag@@eq@@4” ，或者自定义策略
     * @param variable
     * @return
     */
    public static boolean eval(String expression, Map<String, Object> variable) {
        if (StringUtils.isEmpty(expression)) {
            return true;
        }
        AtomicBoolean flag = new AtomicBoolean(false);
        map.forEach((k, v) -> {
            if (expression.startsWith(k + "|")) {
                if (v == null) {
                    throw new FlowException(ExceptionCons.NULL_EXPRESSION_STRATEGY);
                }
                flag.set(v.eval(expression.replace(k + "|", ""), variable));
            }
        });
        return flag.get();
    }

}
