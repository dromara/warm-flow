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
import org.dromara.warm.flow.core.dto.FlowParams;
import org.dromara.warm.flow.core.entity.Task;
import org.dromara.warm.flow.core.exception.FlowException;
import org.dromara.warm.flow.core.variable.DefaultVariableStrategy;
import org.dromara.warm.flow.core.variable.VariableStrategy;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * 变量替换工具类
 *
 * @author warm
 */
public class VariableUtil {

    private static final Map<String, VariableStrategy> map = new HashMap<>();

    private VariableUtil() {

    }

    static {
        setExpression(new DefaultVariableStrategy());
    }

    public static void setExpression(VariableStrategy variableStrategy) {
        map.put(variableStrategy.getType(), variableStrategy);
    }

    /**
     * @param expression 变量表达式，比如“@@default@@|${flag}” ，或者自定义策略
     * @param variable
     * @return
     */
    public static List<String> eval(String expression, Map<String, Object> variable) {
        if (StringUtils.isNotEmpty(expression)) {
            AtomicReference<List<String>> result = new AtomicReference<>();
            map.forEach((k, v) -> {
                if (expression.startsWith(k + "|")) {
                    if (v == null) {
                        throw new FlowException(ExceptionCons.NULL_EXPRESSION_STRATEGY);
                    }
                    result.set(v.eval(expression.replace(k + "|", ""), variable));
                }
            });

            if (CollUtil.isNotEmpty(result.get())) {
                return result.get();
            }
        }

        return Collections.singletonList(expression);
    }

    /**
     * @param addTasks 任务列表
     * @param flowParams 内置参数
     */
    public static void replacement(List<Task> addTasks, FlowParams flowParams) {
        addTasks.forEach(addTask -> addTask.setPermissionList(addTask.getPermissionList().stream()
                .map(s -> VariableUtil.eval(s, flowParams.getVariable()))
                .flatMap(List::stream)
                .collect(Collectors.toList())));
    }

}
