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
package com.warm.expression.spel;

import com.warm.flow.core.constant.FlowCons;
import com.warm.flow.core.expression.ExpressionStrategy;
import com.warm.plugin.modes.sb.utils.SpringUtil;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.BeanResolver;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.Map;

/**
 * 条件表达式spel
 *
 * @author warm
 */
public class ExpressionStrategySpel implements ExpressionStrategy {

    private final ExpressionParser parser = new SpelExpressionParser();
    private final ParserContext parserContext = new TemplateParserContext();


    /**
     * bean解析器 用于处理 spel 表达式中对 bean 的调用
     */
    private final BeanResolver beanResolver = new BeanFactoryResolver(SpringUtil.getBeanFactory());

    @Override
    public String getType() {
        return FlowCons.splitAt + "spel" + FlowCons.splitAt;
    }

    /**
     * @param expression @@spel@@|flag@@eq@@4
     * @param variable
     * @return
     */
    @Override
    public boolean eval(String expression, Map<String, Object> variable) {
        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setBeanResolver(beanResolver);
        return Boolean.TRUE.equals(parser.parseExpression(expression, parserContext).getValue(context, Boolean.class));
    }



}
