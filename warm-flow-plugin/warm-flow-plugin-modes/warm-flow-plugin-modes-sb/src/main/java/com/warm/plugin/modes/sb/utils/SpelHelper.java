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
package com.warm.plugin.modes.sb.utils;

import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.BeanResolver;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

/**
 * 条件表达式spel
 *
 * @author warm
 */
@Component
public class SpelHelper {

    private static final ExpressionParser parser = new SpelExpressionParser();
    private final static ParserContext parserContext = new TemplateParserContext();


    /**
     * bean解析器 用于处理 spel 表达式中对 bean 的调用
     */
    private final static BeanResolver beanResolver = new BeanFactoryResolver(SpringUtil.getBeanFactory());

    /**
     * @param expression
     * @return
     */
    public static Object parseExpression(String expression) {
        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setBeanResolver(beanResolver);
        return parser.parseExpression(expression, parserContext).getValue(context, Object.class);
    }



}
