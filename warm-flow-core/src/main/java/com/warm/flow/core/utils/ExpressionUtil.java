package com.warm.flow.core.utils;

import com.warm.flow.core.constant.ExceptionCons;
import com.warm.flow.core.exception.FlowException;
import com.warm.tools.utils.MapUtil;
import com.warm.tools.utils.StringUtils;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.Map;

/**
 * 表达式处理工具类
 *
 * @author warm
 */
public class ExpressionUtil {

    private ExpressionUtil() {
    }

    public static boolean eval(String expression, Map<String, String> variable) {
        if (StringUtils.isEmpty(expression)) {
            return true;
        }
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("javascript");
        if (MapUtil.isNotEmpty(variable)) {
            variable.forEach(engine::put);
        }
        int start = expression.indexOf("{");
        int end = expression.lastIndexOf("}");
        String result = expression.substring(start + 1, end);
        try {
            return (boolean) engine.eval(result);
        } catch (Exception e) {
            throw new FlowException(ExceptionCons.CONDITION_ERR);
        }
    }
}
