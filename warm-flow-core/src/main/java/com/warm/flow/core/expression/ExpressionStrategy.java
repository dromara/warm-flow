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

import java.util.Map;

/**
 * 条件表达式
 *
 * @author warm
 */
public interface ExpressionStrategy {

    /**
     * 获取表达式类型
     * @return 表达式类型
     */
    String getType();

    /**
     * 执行表达式
     * @param expression 表达式
     * @param variable 流程变量
     * @return 执行结果
     */
    boolean eval(String expression, Map<String, Object> variable);
}
