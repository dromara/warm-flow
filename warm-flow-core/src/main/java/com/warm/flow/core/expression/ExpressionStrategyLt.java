package com.warm.flow.core.expression;

import com.warm.flow.core.constant.FlowCons;
import com.warm.flow.core.utils.MathUtil;

/**
 * 条件表达式小于
 *
 * @author warm
 */
public class ExpressionStrategyLt extends ExpressionStrategyAbstract {

    @Override
    public String getType() {
        return FlowCons.splitAt + "lt" + FlowCons.splitAt;
    }

    @Override
    public boolean afterEval(String[] split, String value) {
        if (MathUtil.isNumeric(split[2].trim())) {
            return MathUtil.determineSize(value, split[2].trim()) < 0;
        } else {
            return false;
        }
    }

}
