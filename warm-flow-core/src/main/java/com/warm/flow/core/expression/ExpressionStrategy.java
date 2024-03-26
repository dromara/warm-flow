package com.warm.flow.core.expression;

import java.util.Map;

public interface ExpressionStrategy {

    String getType();

    boolean eval(String expression, Map<String, Object> variable);
}
