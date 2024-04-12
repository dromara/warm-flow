package com.warm.flow.core.invoker;

import java.util.function.Function;

/**
 * 获取bean方法
 *
 * @author warm
 */
public class FrameInvoker<M> {

    public static FrameInvoker frameInvoker = new FrameInvoker<>();

    private Function<Class<M>, M> beanFunction = null;

    private Function<String, String> cfgFunction = null;

    public FrameInvoker() {
    }

    public static <M> void setBeanFunction(Function<Class<M>, M> function) {
        frameInvoker.beanFunction = function;
    }

    public static <M> M getBean(Class<M> tClass) {
        Object apply = frameInvoker.beanFunction.apply(tClass);
        if (apply == null) {
            throw new NullPointerException("bean is null");
        }
        return (M) apply;
    }

    public static void setCfgFunction(Function<String, String> function) {
        frameInvoker.beanFunction = function;
    }
    public static String getCfg(String key) {
        Object apply = frameInvoker.cfgFunction.apply(key);
        if (apply == null) {
            throw new NullPointerException("value is null");
        }
        return (String) apply;
    }
}
