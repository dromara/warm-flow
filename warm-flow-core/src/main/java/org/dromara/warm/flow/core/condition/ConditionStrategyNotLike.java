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

import org.dromara.warm.flow.core.enums.ConditionType;

/**
 * 条件表达式不包含 notLike@@flag|4
 * 条件表达式不包含 notLike@@flag|4 and eq@@flag|5 or lt@@flag|6
 *
 * @author warm
 */
public class ConditionStrategyNotLike extends AbstractConditionStrategy {

    @Override
    public String getType() {
        return ConditionType.NOT_LIKE.getKey();
    }

    @Override
    public Boolean afterEval(String value, String variableValue) {
        return !value.contains(variableValue);
    }

}
