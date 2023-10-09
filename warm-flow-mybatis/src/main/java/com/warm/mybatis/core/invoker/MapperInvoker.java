package com.warm.mybatis.core.invoker;

import org.apache.ibatis.session.SqlSessionFactory;

import java.util.function.Function;

/**
 * 执行mapper方法
 *
 * @author warm
 */
public class MapperInvoker<M> {

    private SqlSessionFactory sqlSessionFactory = null;

    public static MapperInvoker mapperInvoker = new MapperInvoker<>();

    private Function<Class<M>, M> function = null;

    public MapperInvoker() {
    }

    public MapperInvoker(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }

    public static <M> void setMapperFunction(Function<Class<M>, M> function) {
        mapperInvoker.function = function;
    }

    public M getMapper(Class<M> tClass) {

        return this.function.apply(tClass);
    }

}
