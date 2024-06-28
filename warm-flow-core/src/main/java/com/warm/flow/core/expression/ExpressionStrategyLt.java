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

import com.warm.flow.core.constant.FlowCons;
import com.warm.flow.core.utils.MathUtil;

/**
 * 条件表达式小于
 *
 * @author warm
 */
public class ExpressionStrategyLt extends ExpressionStrategyAbstract {

    @Override
    public String getType() {
        return FlowCons.splitAt + "lt" + FlowCons.splitAt;
    }

    @Override
    public boolean afterEval(String[] split, String value) {
        if (MathUtil.isNumeric(split[2].trim())) {
            return MathUtil.determineSize(value, split[2].trim()) < 0;
        } else {
            return false;
        }
    }

}
