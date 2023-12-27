package com.warm.flow.core.expression;

import com.warm.flow.core.constant.ExceptionCons;
import com.warm.flow.core.exception.FlowException;
import com.warm.tools.utils.MapUtil;
import com.warm.tools.utils.StringUtils;

import java.util.Map;

/**
 * 条件表达式抽象类，复用部分代码
 *
 * @author warm
 */
public abstract class ExpressionStrategyAbstract implements ExpressionStrategy {

    public static final String splitAt = "@@";

    /**
     *
     * @param expression @@eq@@|flag@@eq@@4
     * @param variable
     * @return
     */
    @Override
    public boolean eval(String expression, Map<String, String> variable) {
        String[] split = expression.split(splitAt);
        preEval(split, variable);
        return afterEval(split, variable.get(split[0].trim()));
    }

    public void preEval(String[] split, Map<String, String> variable) {
        if (MapUtil.isEmpty(variable) && StringUtils.isEmpty(variable.get(split[0].trim()))) {
            throw new FlowException(ExceptionCons.NULL_CONDITIONVALUE);
        }
    }

    public abstract boolean afterEval(String[] split, String value);

}
