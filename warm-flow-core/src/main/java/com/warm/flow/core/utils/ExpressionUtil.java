package com.warm.flow.core.utils;

import com.warm.flow.core.constant.ExceptionCons;
import com.warm.flow.core.exception.FlowException;
import com.warm.flow.core.expression.*;
import com.warm.tools.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 条件表达式map
 *
 * @author warm
 */
public class ExpressionUtil {

    private static final Map<String, ExpressionStrategy> map = new HashMap<>();

    static {
        setExpression(new ExpressionStrategyEq());
        setExpression(new ExpressionStrategyGe());
        setExpression(new ExpressionStrategyGt());
        setExpression(new ExpressionStrategyLe());
        setExpression(new ExpressionStrategyLike());
        setExpression(new ExpressionStrategyLt());
        setExpression(new ExpressionStrategyNe());
        setExpression(new ExpressionStrategyNotLike());
    }

    public static void setExpression(ExpressionStrategy expressionStrategy) {
        map.put(expressionStrategy.getType(), expressionStrategy);
    }

    /**
     * @param expression 条件表达式，比如“@@eq@@|flag@@eq@@4” ，或者自定义策略
     * @param variable
     * @return
     */
    public static boolean eval(String expression, Map<String, Object> variable) {
        if (StringUtils.isEmpty(expression)) {
            return true;
        }
        AtomicBoolean flag = new AtomicBoolean(false);
        map.forEach((k, v) -> {
            if (expression.contains(k + "|")) {
                if (v == null) {
                    throw new FlowException(ExceptionCons.NULL_EXPRESSION_STRATEGY);
                }
                flag.set(v.eval(expression.replace(k + "|", ""), variable));
            }
        });
        return flag.get();
    }
}
