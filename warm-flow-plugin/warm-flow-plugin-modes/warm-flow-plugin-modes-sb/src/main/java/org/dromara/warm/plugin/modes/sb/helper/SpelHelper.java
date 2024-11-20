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
package org.dromara.warm.plugin.modes.sb.helper;

import org.dromara.warm.flow.core.exception.FlowException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
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
@Configuration
public class SpelHelper implements ApplicationContextAware {

    private static final ExpressionParser parser = new SpelExpressionParser();
    private final static ParserContext parserContext = new TemplateParserContext();

    private static ApplicationContext applicationContext;

    /**
     * bean解析器 用于处理 spel 表达式中对 bean 的调用
     */
    private static BeanResolver beanResolver = null;

    public static BeanResolver beanResolver() {
        if (beanResolver == null) {
            beanResolver = new BeanFactoryResolver(getBeanFactory());
        }
        return beanResolver;
    }

    /**
     * @param expression
     * @return
     */
    public static Object parseExpression(String expression, Map<String, Object> variable) {
        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setBeanResolver(beanResolver());
        context.setVariables(variable);
        return parser.parseExpression(expression, parserContext).getValue(context, Object.class);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (SpelHelper.applicationContext == null) {
            SpelHelper.applicationContext = applicationContext;
        }
    }

    public static ListableBeanFactory getBeanFactory() {
        if (null == applicationContext) {
            throw new FlowException("No ConfigurableListableBeanFactory or ApplicationContext injected, maybe not in the Spring environment?");
        } else {
            return applicationContext;
        }
    }


}
