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
package org.dromara.warm.flow.core.expression;

import org.dromara.warm.flow.core.constant.FlowCons;
import org.dromara.warm.flow.core.utils.MathUtil;

/**
 * 条件表达式大于等于 @@ge@@|flag@@ge@@4
 *
 * @author warm
 */
public class ConditionStrategyGe extends ConditionStrategyAbstract {

    @Override
    public String getType() {
        return FlowCons.splitAt + "ge" + FlowCons.splitAt;
    }

    @Override
    public Boolean afterEval(String[] split, String value) {
        if (MathUtil.isNumeric(split[2].trim())) {
            return MathUtil.determineSize(value, split[2].trim()) > 0 || MathUtil.determineSize(value, split[2].trim()) == 0;
        } else {
            return false;
        }
    }

}
