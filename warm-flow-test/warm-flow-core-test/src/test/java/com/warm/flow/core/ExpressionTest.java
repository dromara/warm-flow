package com.warm.flow.core;

import com.warm.flow.core.constant.ExceptionCons;
import com.warm.flow.core.exception.FlowException;
import com.warm.flow.core.utils.ExpressionUtil;
import com.warm.tools.utils.MapUtil;
import com.warm.tools.utils.ObjectUtil;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static com.warm.flow.core.expression.ExpressionStrategyAbstract.splitAt;

/**
 * JavaScript表达式
 *
 * @author warm
 */
public class ExpressionTest {

    @Test
    public void test1() {
        Map<String, Object> variable = new HashMap<>();
        variable.put("flag", "1");
        String expression = "flag @@eq@@ 0";
        System.out.println(ExpressionUtil.eval(expression, variable));

    }
    public static void main(String[] args) {
        String expression = "@@gt@@|flag@@gt@@4".replace("@@gt@@|", "");
        //System.out.println(expression);//flag@@gt@@4
        String[] split = expression.split(splitAt);
        for (String item : split) {
            System.out.println(item);
//            flag
//            gt
//            4
        }
        Map<String, Object> variable = new HashMap<>();
        variable.put("flag","2");
        // 打印 variable 的类型
        Object o = variable.get(split[0].trim());

        System.out.println("variable 的类型: " + o.getClass().getName());

        // 判断 variable 是否为 String 类型
        if (o instanceof String) {
            System.out.println("variable 是 String 类型");
        } else {
            System.out.println("variable 不是 String 类型");
        }
        System.out.println(o.toString());

        if (MapUtil.isEmpty(variable) && ObjectUtil.isNull(variable.get(split[0].trim()))) {
            throw new FlowException(ExceptionCons.NULL_CONDITIONVALUE);
        }
    }


}
