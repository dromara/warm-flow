package com.warm.flow.mybatisplus.solon;

import com.warm.flow.core.handler.DataFillHandler;
import com.warm.flow.core.test.Listener.CreateListener;
import com.warm.flow.core.test.Listener.FinishListener;
import com.warm.flow.core.test.Listener.PermissionListener;
import com.warm.flow.core.test.Listener.StartListener;
import com.warm.flow.core.test.handle.CustomDataFillHandler;
import com.zaxxer.hikari.HikariDataSource;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

import javax.sql.DataSource;

@Configuration
public class WarmFlowConfig {
    //此下的 db1 与 mybatis.db1 将对应在起来 //可以用 @Db("db1") 注入mapper
    //typed=true，表示默认数据源。@Db 可不带名字注入 
    @Bean(value = "db1", typed = true)
    public DataSource db1(@Inject("${demo.db1}") HikariDataSource ds) {
        return ds;
    }

    /**
     * 自定义填充 （可配置文件注入，也可用@Bean/@Component方式）
     */
    @Bean
    public DataFillHandler dataFillHandler() {
        return new CustomDataFillHandler();
    }

//    /**
//     * 全家租户处理器（可配置文件注入，也可用@Bean/@Component方式）
//     */
//    @Bean
//    public TenantHandler tenantHandler() {
//        return new CustomTenantHandler();
//    }

    /**
     * 创建监听器，可用@Bean/@Component方式注入
     */
    @Bean
    public CreateListener createListener() {
        return new CreateListener();
    }

    /**
     * 完成监听器，可用@Bean/@Component方式注入
     */
    @Bean
    public FinishListener finishListener() {
        return new FinishListener();
    }

    /**
     * 权限监听器，可用@Bean/@Component方式注入
     */
    @Bean
    public PermissionListener permissionListener() {
        return new PermissionListener();
    }

    /**
     * 开始监听器，可用@Bean/@Component方式注入
     */
    @Bean
    public StartListener startListener() {
        return new StartListener();
    }

}