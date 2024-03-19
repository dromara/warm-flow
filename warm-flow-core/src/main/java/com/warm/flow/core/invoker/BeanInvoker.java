package com.warm.flow.core.invoker;

import java.util.function.Function;

/**
 * 获取bean方法
 *
 * @author warm
 */
public class BeanInvoker<M> {

    public static BeanInvoker beanInvoker = new BeanInvoker<>();

    private Function<Class<M>, M> function = null;

    public BeanInvoker() {
    }

    public static <M> void setBeanFunction(Function<Class<M>, M> function) {
        beanInvoker.function = function;
    }

    public static <M> M getBean(Class<M> tClass) {

        return (M) beanInvoker.function.apply(tClass);
    }

}
