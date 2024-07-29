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
package com.warm.flow.core.expression;

import com.warm.flow.core.constant.ExceptionCons;
import com.warm.flow.core.constant.FlowCons;
import com.warm.flow.core.exception.FlowException;
import com.warm.flow.core.utils.MapUtil;
import com.warm.flow.core.utils.ObjectUtil;

import java.util.Map;

/**
 * 条件表达式抽象类，复用部分代码
 *
 * @author warm
 */
public abstract class ExpressionStrategyAbstract implements ExpressionStrategy {

    /**
     * @param expression flag@@eq@@4
     * @param variable
     * @return
     */
    @Override
    public boolean eval(String expression, Map<String, Object> variable) {
        String[] split = expression.split(FlowCons.splitAt);
        preEval(split, variable);
        String variableValue = String.valueOf(variable.get(split[0].trim()));
        return afterEval(split, variableValue);
    }

    public void preEval(String[] split, Map<String, Object> variable) {
        Object o = variable.get(split[0].trim());
        if (MapUtil.isEmpty(variable) && ObjectUtil.isNull(o)) {
            throw new FlowException(ExceptionCons.NULL_CONDITIONVALUE);
        }
    }

    public abstract boolean afterEval(String[] split, String value);

}
