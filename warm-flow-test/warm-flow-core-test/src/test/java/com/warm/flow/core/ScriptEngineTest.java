package com.warm.flow.core;

import com.warm.flow.core.utils.ExpressionUtil;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.junit.Test;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.SimpleBindings;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * JavaScript表达式
 *
 * @author warm
 */
public class ScriptEngineTest {

    @Test
    public void test1() {
        Map<String, String> variable = new HashMap<>();
        variable.put("flag", "1");
        String expression = "${flag > 0}";
        System.out.println(ExpressionUtil.eval(expression, variable));

        variable.put("a", "1");
        variable.put("b", "2");
        String expression1 = "${a + b > 4}";
        System.out.println(ExpressionUtil.eval(expression1, variable));
    }

    public static ScriptEngine engine;
    private static String str;
    //该函数测试Java获取JS变量值的能力
    public static void getJsValue() throws Exception{
        str = "  var msg='hello';          "
                + "  var number = 123;         "
                + "  var array=['A','B','C'];  "
                + "  var json={                "
                + "      'name':'pd',          "
                + "      'subjson':{           "
                + "           'subname':'spd'  "
                + "           ,'id':123        "
                + "           }                "
                + "      };                    ";
        //执行语句
        engine.eval(str);
        str="msg+=' world';number+=5";
        //再次执行
        engine.eval(str);
        //获取js变量msg（String类型）
        System.out.println(engine.get("msg"));
        //获取js变量msg（int类型）
        System.out.println(engine.get("number"));
        //获取js变量array（数组）
        ScriptObjectMirror array=(ScriptObjectMirror) engine.get("array");
        //getSlot（int index）函数用于获取下标为index的值
        System.out.println(array.getSlot(0));
        //获取js变量json（json类型）
        ScriptObjectMirror json=(ScriptObjectMirror) engine.get("json");
        //get（String key）函数用于键key的值
        System.out.println(json.get("name"));
        //获取js变量subjson（嵌套json类型）
        ScriptObjectMirror subjson=(ScriptObjectMirror)json.get("subjson");
        System.out.println(subjson.get("subname"));
    }
    //该函数测试Java与js对象
    public static void getObject() throws Exception{
        str = "  var obj=new Object();     "
                + "  obj.info='hello world';   "
                + "  obj.getInfo=function(){   "
                + "        return this.info;   "
                + "  };                        ";
        engine.eval(str);
        //获取对象
        ScriptObjectMirror obj=(ScriptObjectMirror) engine.get("obj");
        //输出属性
        System.out.println(obj.get("info"));
        System.out.println(obj.get("getInfo"));
        str="obj.getInfo()";
        //执行方法
        System.out.println(engine.eval(str));
    }
    //java将变量导入js脚本
    public static void putValue() throws Exception{
        str="Math.pow(a,b)";
        Map<String, Object>input=new TreeMap<>();
        input.put("a",2);
        input.put("b",8);
        System.out.println(engine.eval(str,new SimpleBindings(input)));
    }
    //调用js函数
    public static void callJsFunction() throws Exception{
        engine.eval("function add (a, b) {return a+b; }");
        Invocable jsInvoke = (Invocable) engine;
        Object res = jsInvoke.invokeFunction("add", new Object[] { 10, 5 });
        System.out.println(res);
    }
    public static void main(String[] args) throws Exception {
        //获取js引擎实例
        ScriptEngineManager sem = new ScriptEngineManager();
        engine=sem.getEngineByName("javascript");
        getJsValue();
        getObject();
        putValue();
        callJsFunction();
    }
}
