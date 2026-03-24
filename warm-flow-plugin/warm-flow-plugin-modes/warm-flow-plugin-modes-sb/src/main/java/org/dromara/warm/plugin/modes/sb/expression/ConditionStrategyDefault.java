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
package org.dromara.warm.plugin.modes.sb.expression;


import org.dromara.warm.flow.core.constant.FlowCons;
import org.dromara.warm.plugin.modes.sb.helper.SpelHelper;

import java.util.Map;

/**
 * 默认条件表达式 default@@${flag == 5 && flag > 4}
 * 实际上是基于spring的spel表达式简化使用，会把flag替换成#flag，然后由spel执行#flag == 5 && #flag > 4
 *
 * @author warm
 */
public class ConditionStrategyDefault extends ConditionStrategySpel {

    @Override
    public String getType() {
        return FlowCons.DEFAULT;
    }

    @Override
    public Boolean eval(String expression, Map<String, Object> variable) {
        return super.eval(SpelHelper.replace(expression,  variable), variable);
    }
}
