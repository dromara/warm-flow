package com.warm.mybatis.spring.boot.starter.config;


import com.warm.mybatis.core.invoker.MapperInvoker;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author minliuhua
 * @description: 工作流bean注册配置
 * @date: 2023/6/5 23:01
 */
@Configuration
public class MybatisConfig {


    @Bean
    public MapperInvoker initMapperInvoker(SqlSessionFactory sqlSessionFactory) {
        return MapperInvoker.mapperInvoker = new MapperInvoker(sqlSessionFactory);
    }
}
