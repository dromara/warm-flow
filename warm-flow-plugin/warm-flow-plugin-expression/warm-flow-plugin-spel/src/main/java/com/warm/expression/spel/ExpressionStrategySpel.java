///*
// *    Copyright 2024-2025, Warm-Flow (290631660@qq.com).
// *
// *    Licensed under the Apache License, Version 2.0 (the "License");
// *    you may not use this file except in compliance with the License.
// *    You may obtain a copy of the License at
// *
// *       https://www.apache.org/licenses/LICENSE-2.0
// *
// *    Unless required by applicable law or agreed to in writing, software
// *    distributed under the License is distributed on an "AS IS" BASIS,
// *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// *    See the License for the specific language governing permissions and
// *    limitations under the License.
// */
//package com.warm.expression.spel;
//
//import com.warm.flow.core.constant.ExceptionCons;
//import com.warm.flow.core.constant.FlowCons;
//import com.warm.flow.core.exception.FlowException;
//import com.warm.flow.core.expression.ExpressionStrategy;
//import com.warm.flow.core.utils.MapUtil;
//import com.warm.flow.core.utils.ObjectUtil;
//import org.springframework.context.expression.BeanFactoryResolver;
//import org.springframework.expression.BeanResolver;
//
//import java.util.Map;
//
///**
// * 条件表达式spel
// *
// * @author warm
// */
//public abstract class ExpressionStrategySpel implements ExpressionStrategy {
//
//    /**
//     * bean解析器 用于处理 spel 表达式中对 bean 的调用
//     */
//    private final BeanResolver beanResolver = new BeanFactoryResolver(SpringUtils.getBeanFactory());
//
//    @Override
//    public String getType() {
//        return FlowCons.splitAt + "spel" + FlowCons.splitAt;
//    }
//
//    /**
//     * @param expression @@spel@@|flag@@eq@@4
//     * @param variable
//     * @return
//     */
//    @Override
//    public boolean eval(String expression, Map<String, Object> variable) {
//        String[] split = expression.split(FlowCons.splitAt);
//        preEval(split, variable);
//        String variableValue = String.valueOf(variable.get(split[0].trim()));
//        return afterEval(split, variableValue);
//    }
//
//    public void preEval(String[] split, Map<String, Object> variable) {
//        Object o = variable.get(split[0].trim());
//        if (MapUtil.isEmpty(variable) && ObjectUtil.isNull(o)) {
//            throw new FlowException(ExceptionCons.NULL_CONDITIONVALUE);
//        }
//        // 判断 variable.get(split[0].trim()) 是否为 String 类型
//        if (!(o instanceof String)) {
//            throw new FlowException(ExceptionCons.CONDITIONVALUE_STRING);
//        }
//    }
//
//    public abstract boolean afterEval(String[] split, String value);
//
//}
