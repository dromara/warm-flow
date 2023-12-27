package com.warm.flow.core.expression;

/**
 * 条件表达式不包含
 *
 * @author warm
 */
public class ExpressionStrategyNotLike extends ExpressionStrategyAbstract {

    public String getType() {
        return splitAt + "notNike" +splitAt;
    }

    @Override
    public boolean afterEval(String[] split, String value) {
        return !split[2].trim().contains(value);
    }

}
