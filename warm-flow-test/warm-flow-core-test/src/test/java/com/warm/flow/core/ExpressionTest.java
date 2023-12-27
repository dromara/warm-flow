package com.warm.flow.core;

import com.warm.flow.core.utils.ExpressionUtil;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * JavaScript表达式
 *
 * @author warm
 */
public class ExpressionTest {

    @Test
    public void test1() {
        Map<String, String> variable = new HashMap<>();
        variable.put("flag", "1");
        String expression = "flag @@eq@@ 0";
        System.out.println(ExpressionUtil.eval(expression, variable));

    }


}
