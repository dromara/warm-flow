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

import org.dromara.warm.flow.core.entity.Task;
import org.dromara.warm.flow.core.expression.*;
import org.dromara.warm.flow.core.strategy.ExpressionStrategy;
import org.dromara.warm.flow.core.variable.DefaultVariableStrategy;
import org.dromara.warm.flow.core.variable.VariableStrategy;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 表达式工具类
 *
 * @author warm
 */
public class ExpressionUtil {

    static {
        // 注册条件表达式
        setExpression(new ConditionStrategyEq());
        setExpression(new ConditionStrategyGe());
        setExpression(new ConditionStrategyGt());
        setExpression(new ConditionStrategyLe());
        setExpression(new ConditionStrategyLike());
        setExpression(new ConditionStrategyLt());
        setExpression(new ConditionStrategyNe());
        setExpression(new ConditionStrategyNotLike());

        // 注册办理人变量表达式
        setExpression(new DefaultVariableStrategy());
    }

    /**
     * 设置表达式
     *
     * @param expressionStrategy 表达式策略
     * @param <T>                表达式类型
     */
    public static <T> void setExpression(ExpressionStrategy<T> expressionStrategy) {
        expressionStrategy.setExpression(expressionStrategy);
    }

    /**
     * 表达式替换
     *
     * @param expression 条件表达式，比如“@@eq@@|flag@@eq@@4” ，或者自定义策略
     * @param variable   变量
     * @return boolean
     */
    public static boolean evalCondition(String expression, Map<String, Object> variable) {
        return eval(ConditionStrategy.getExpressionMap(), expression, variable);
    }

    /**
     * 办理人变量表达式替换
     *
     * @param addTasks 任务列表
     * @param variable 流程变量
     */
    public static void evalVariable(List<Task> addTasks, Map<String, Object> variable) {
        addTasks.forEach(addTask -> addTask.setPermissionList(addTask.getPermissionList().stream()
                .map(s -> {
                    List<String> result = eval(VariableStrategy.getExpressionMap(), s, variable);
                    if (CollUtil.isNotEmpty(result)) {
                        return result;
                    }
                    return Collections.singletonList(s);
                })
                .flatMap(List::stream)
                .collect(Collectors.toList())));
    }

    /**
     * 表达式替换
     *
     * @param expression 条件表达式，比如“@@eq@@|flag@@eq@@4” ，或者自定义策略
     * @param variable   变量
     * @return boolean
     */
    public static <T> T eval(Map<String, ExpressionStrategy<T>> map, String expression
            , Map<String, Object> variable) {
        return ExpressionStrategy.getValue(map, expression, variable);
    }
}
