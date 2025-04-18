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

import org.dromara.warm.flow.core.utils.MathUtil;

/**
 * 条件表达式小于等于 le@@flag|4
 *
 * @author warm
 * @see <a href="https://warm-flow.dromara.org/master/primary/condition.html">文档地址</a>
 */
public class ConditionStrategyLe extends ConditionStrategyAbstract {

    @Override
    public String getType() {
        return "le";
    }

    @Override
    public Boolean afterEval(String value, String variableValue) {
        if (MathUtil.isNumeric(value)) {
            return MathUtil.determineSize(variableValue, value) < 0 || MathUtil.determineSize(variableValue, value) == 0;
        } else {
            return false;
        }
    }

}
