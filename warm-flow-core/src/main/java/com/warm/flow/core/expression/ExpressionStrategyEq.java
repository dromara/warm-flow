package com.warm.flow.core.expression;

import com.warm.tools.utils.MathUtil;

/**
 * 条件表达式等于
 *
 * @author warm
 */
public class ExpressionStrategyEq extends ExpressionStrategyAbstract {

    public String getType() {
        return splitAt +"eq" + splitAt;
    }

    @Override
    public boolean afterEval(String[] split, String value) {
        if (MathUtil.isNumeric(split[2].trim())) {
            return MathUtil.determineSize(value, split[2].trim()) == 0;
        } else {
            return value.equals(split[2].trim());
        }
    }

}
