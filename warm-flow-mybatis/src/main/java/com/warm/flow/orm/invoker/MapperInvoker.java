package com.warm.flow.orm.invoker;

import java.util.function.Function;

/**
 * 执行mapper方法
 *
 * @author warm
 */
public class MapperInvoker<M> {

    public static MapperInvoker mapperInvoker = new MapperInvoker<>();

    private Function<Class<M>, M> function = null;

    public MapperInvoker() {
    }

    public static <M> void setMapperFunction(Function<Class<M>, M> function) {
        mapperInvoker.function = function;
    }

    public static <M> M getMapper(Class<M> tClass) {

        return (M) mapperInvoker.function.apply(tClass);
    }

}
