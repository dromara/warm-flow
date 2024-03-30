package com.warm.flow.core.expression;

import com.warm.flow.core.constant.FlowCons;

/**
 * 条件表达式包含
 *
 * @author warm
 */
public class ExpressionStrategyLike extends ExpressionStrategyAbstract {

    public String getType() {
        return FlowCons.splitAt + "like" + FlowCons.splitAt;
    }

    @Override
    public boolean afterEval(String[] split, String value) {
        return split[2].trim().contains(value);
    }

}
