package org.dromara.warm.flow.orm.strategy;

import com.easy.query.core.basic.extension.logicdel.LogicDeleteBuilder;
import com.easy.query.core.basic.extension.logicdel.abstraction.AbstractLogicDeleteStrategy;
import com.easy.query.core.expression.lambda.SQLActionExpression1;
import com.easy.query.core.expression.parser.core.base.ColumnSetter;
import com.easy.query.core.expression.parser.core.base.WherePredicate;
import org.dromara.warm.flow.core.FlowEngine;
import org.dromara.warm.flow.core.config.WarmFlow;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * WarmFlow 逻辑删除策略
 *
 * @author warm
 * @since 2026/1/21
 */
public class WarmFlowLogicDeleteStrategy extends AbstractLogicDeleteStrategy {

    public static final String NAME = "WarmFlowLogicDelete";

    private static final Set<Class<?>> ALLOW_TYPES = new HashSet<>(Collections.singletonList(String.class));

    @Override
    protected SQLActionExpression1<WherePredicate<Object>> getPredicateFilterExpression(LogicDeleteBuilder logicDeleteBuilder, String propertyName) {
        return o ->{
            WarmFlow flowConfig = FlowEngine.getFlowConfig();
            if (flowConfig.isLogicDelete()) {
                o.eq(propertyName, flowConfig.getLogicNotDeleteValue());
            }
        };
    }

    @Override
    protected SQLActionExpression1<ColumnSetter<Object>> getDeletedSQLExpression(LogicDeleteBuilder logicDeleteBuilder, String propertyName) {
        // 设置逻辑删除字段值为 warmFlow 配置的 删除值
        return o -> {
            WarmFlow flowConfig = FlowEngine.getFlowConfig();
            if (flowConfig.isLogicDelete()) {
                o.set(propertyName, FlowEngine.getFlowConfig().getLogicDeleteValue());
            }
        };
    }

    @Override
    public String getStrategy() {
        return NAME;
    }

    @Override
    public Set<Class<?>> allowedPropertyTypes() {
        return ALLOW_TYPES;
    }
}
