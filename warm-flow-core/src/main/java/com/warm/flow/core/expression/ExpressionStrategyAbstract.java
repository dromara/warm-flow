package com.warm.flow.core.expression;

import com.warm.flow.core.constant.ExceptionCons;
import com.warm.flow.core.constant.FlowCons;
import com.warm.flow.core.exception.FlowException;
import com.warm.flow.core.utils.MapUtil;
import com.warm.flow.core.utils.ObjectUtil;

import java.util.Map;

/**
 * 条件表达式抽象类，复用部分代码
 *
 * @author warm
 */
public abstract class ExpressionStrategyAbstract implements ExpressionStrategy {

    /**
     * @param expression @@eq@@|flag@@eq@@4
     * @param variable
     * @return
     */
    @Override
    public boolean eval(String expression, Map<String, Object> variable) {
        String[] split = expression.split(FlowCons.splitAt);
        preEval(split, variable);
        String variableValue = String.valueOf(variable.get(split[0].trim()));
        return afterEval(split, variableValue);
    }

    public void preEval(String[] split, Map<String, Object> variable) {
        Object o = variable.get(split[0].trim());
        if (MapUtil.isEmpty(variable) && ObjectUtil.isNull(o)) {
            throw new FlowException(ExceptionCons.NULL_CONDITIONVALUE);
        }
        // 判断 variable.get(split[0].trim()) 是否为 String 类型
        if (!(o instanceof String)) {
            throw new FlowException(ExceptionCons.CONDITIONVALUE_STRING);
        }
    }

    public abstract boolean afterEval(String[] split, String value);

}
