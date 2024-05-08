package com.warm.flow.core.invoker;

import java.util.function.Function;

/**
 * 获取bean方法
 *
 * @author warm
 */
public class FrameInvoker<M> {

    public static FrameInvoker frameInvoker = new FrameInvoker<>();

    private Function<Class<M>, M> beanFunction;

    private Function<String, String> cfgFunction;

    public FrameInvoker() {
    }

    /**
     * 设置获取beanfunction
     *
     * @param function
     * @param <M>
     */
    public static <M> void setBeanFunction(Function<Class<M>, M> function) {
        frameInvoker.beanFunction = function;
    }

    public static <M> M getBean(Class<M> tClass) {
        try {
            return (M) frameInvoker.beanFunction.apply(tClass);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 设置获取配置function
     *
     * @param function
     */
    public static void setCfgFunction(Function<String, String> function) {
        frameInvoker.cfgFunction = function;
    }

    public static String getCfg(String key) {
        try {
            return (String) frameInvoker.cfgFunction.apply(key);
        } catch (Exception e) {
            return null;
        }
    }

}
