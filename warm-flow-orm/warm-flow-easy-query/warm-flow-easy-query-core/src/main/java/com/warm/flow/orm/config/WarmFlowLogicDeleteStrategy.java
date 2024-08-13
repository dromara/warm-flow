package com.warm.flow.orm.config;

import com.easy.query.core.basic.extension.logicdel.LogicDeleteBuilder;
import com.easy.query.core.basic.extension.logicdel.abstraction.AbstractLogicDeleteStrategy;
import com.easy.query.core.expression.lambda.SQLExpression1;
import com.easy.query.core.expression.parser.core.base.ColumnSetter;
import com.easy.query.core.expression.parser.core.base.WherePredicate;
import com.warm.flow.core.FlowFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


/**
 * WarmFlow 逻辑删除策略
 */
public class WarmFlowLogicDeleteStrategy extends AbstractLogicDeleteStrategy {
    private static final Set<Class<?>> allowedPropertyTypes = new HashSet<>(Arrays.asList(String.class, Character.class));

    @Override
    protected SQLExpression1<WherePredicate<Object>> getPredicateFilterExpression(LogicDeleteBuilder logicDeleteBuilder, String propertyName) {
        // 追加查询条件 逻辑删除字段 = warmFlow 配置的 未删除值
        return o -> o.eq(propertyName, FlowFactory.getFlowConfig().getLogicNotDeleteValue());
    }

    @Override
    protected SQLExpression1<ColumnSetter<Object>> getDeletedSQLExpression(LogicDeleteBuilder logicDeleteBuilder, String propertyName) {
        // 设置逻辑删除字段值为 warmFlow 配置的 删除值
        return o -> o.set(propertyName, FlowFactory.getFlowConfig().getLogicDeleteValue());
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
