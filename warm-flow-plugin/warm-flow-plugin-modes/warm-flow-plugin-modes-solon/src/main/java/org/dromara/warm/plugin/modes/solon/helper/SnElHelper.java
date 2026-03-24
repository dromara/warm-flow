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
package org.dromara.warm.plugin.modes.solon.helper;

import org.noear.solon.Solon;
import org.noear.solon.expression.context.EnhanceContext;
import org.noear.solon.expression.snel.SnEL;

import java.util.Map;

/**
 * 条件表达式 snel
 *
 * @author warm,battcn
 */
public class SnElHelper {

    /**
     * @param expression expression
     * @return  Object
     */
    public static Object parseExpression(String expression, Map<String, Object> variable) {
        String result = expression.replace("#{", "").replace("}", "");
        EnhanceContext context = new EnhanceContext(variable);
        context.forBeans(k -> Solon.context().getBean( (String)k));
        return SnEL.eval(result, context);
    }



}
