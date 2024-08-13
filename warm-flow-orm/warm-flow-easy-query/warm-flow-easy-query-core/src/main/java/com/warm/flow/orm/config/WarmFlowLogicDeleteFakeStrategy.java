package com.warm.flow.orm.config;

import com.easy.query.core.basic.extension.logicdel.LogicDeleteBuilder;
import com.easy.query.core.basic.extension.logicdel.abstraction.AbstractLogicDeleteStrategy;
import com.easy.query.core.expression.lambda.SQLExpression1;
import com.easy.query.core.expression.parser.core.base.ColumnSetter;
import com.easy.query.core.expression.parser.core.base.WherePredicate;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 当配置 WarmFlow 不使用逻辑删除时，使用此策略
 */
public class WarmFlowLogicDeleteFakeStrategy extends AbstractLogicDeleteStrategy {
    private static final Set<Class<?>> allowedPropertyTypes = new HashSet<>(Arrays.asList(String.class, Character.class));

    @Override
    protected SQLExpression1<WherePredicate<Object>> getPredicateFilterExpression(LogicDeleteBuilder logicDeleteBuilder, String propertyName) {
        // 假的逻辑删除策略，不做任何操作
        return o -> {
        };
    }

    @Override
    protected SQLExpression1<ColumnSetter<Object>> getDeletedSQLExpression(LogicDeleteBuilder logicDeleteBuilder, String propertyName) {
        // 假的逻辑删除策略，不做任何操作
        return o -> {
        };
    }

    @Override
    public String getStrategy() {
        return "WarmFlowLogicDelete";
    }

    @Override
    public Set<Class<?>> allowedPropertyTypes() {
        return allowedPropertyTypes;
    }
}
