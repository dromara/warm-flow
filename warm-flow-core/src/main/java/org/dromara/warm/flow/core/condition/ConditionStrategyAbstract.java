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
package org.dromara.warm.flow.core.condition;

import org.dromara.warm.flow.core.constant.ExceptionCons;
import org.dromara.warm.flow.core.constant.FlowCons;
import org.dromara.warm.flow.core.utils.AssertUtil;
import org.dromara.warm.flow.core.utils.ExpressionUtil;

import java.util.Map;

/**
 * 条件表达式抽象类，复用部分代码
 *
 * @author warm
 * @see <a href="https://warm-flow.dromara.org/master/primary/condition.html">文档地址</a>
 */
public abstract class ConditionStrategyAbstract implements ConditionStrategy {


    /**
     * 执行表达式前置方法 合法性校验
     *
     * @param name    变量名称：flag
     * @param variable 流程变量
     */
    public void preEval(String name, Map<String, Object> variable) {
        AssertUtil.isEmpty(variable, ExceptionCons.NULL_CONDITION_VALUE);
        Object o = variable.get(name);
        AssertUtil.isNull(o, ExceptionCons.NULL_CONDITION_VALUE);
    }

    /**
     * 执行表达式

     * @param expression 表达式：flag@@5
     *                   在{@link ExpressionUtil#evalCondition}中格式为，比如：eq@@flag|5，
     *                   截取前缀进入此方法后为：flag|5
     * @param variable   流程变量
     * @return 执行结果
     */
    @Override
    public Boolean eval(String expression, Map<String, Object> variable) {
        String[] split = expression.split(FlowCons.splitVertical);
        preEval(split[0].trim(), variable);
        String variableValue = String.valueOf(variable.get(split[0].trim()));
        return afterEval(split[1].trim(), variableValue);
    }

    /**
     * 执行表达式后置方法
     *
     * @param value 表达式最后一个参数，比如：eq@@flag|5的[5]
     * @param variableValue 流程变量值
     * @return 执行结果
     */
    public abstract Boolean afterEval(String value, String variableValue);

}
