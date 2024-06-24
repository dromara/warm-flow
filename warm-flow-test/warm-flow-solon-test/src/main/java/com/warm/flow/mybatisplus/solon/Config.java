package com.warm.flow.mybatisplus.solon;

import com.zaxxer.hikari.HikariDataSource;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

import javax.sql.DataSource;

@Configuration
public class Config {
    //此下的 db1 与 mybatis.db1 将对应在起来 //可以用 @Db("db1") 注入mapper
    //typed=true，表示默认数据源。@Db 可不带名字注入 
    @Bean(value = "db1", typed = true)
    public DataSource db1(@Inject("${demo.db1}") HikariDataSource ds) {
        return ds;
    }

}