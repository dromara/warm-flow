package com.warm.flow.spring.boot.config;

import com.warm.flow.core.FlowFactory;
import com.warm.flow.core.handler.DataFillHandlerImpl;
import com.warm.flow.core.service.*;
import com.warm.flow.core.service.impl.*;
import com.warm.mybatis.core.SqlSessionFactoryBean;
import com.warm.mybatis.core.handler.DataFillHandlerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;

/**
 * @author minliuhua
 * @description: 工作流bean注册配置
 * @date: 2023/6/5 23:01
 */
@Configuration
public class FlowConfig {

    @Bean
    public DefService definitionService() {
        return new DefServiceImpl();
    }

    @Bean
    public NodeService nodeService() {
        return new NodeServiceImpl();
    }

    @Bean
    public SkipService skipService() {
        return new SkipServiceImpl();
    }

    @Bean
    public InsService instanceService() {
        return new InsServiceImpl();
    }

    @Bean
    public TaskService taskService() {
        return new TaskServiceImpl();
    }

    @Bean
    public HisTaskService hisTaskService() {
        return new HisTaskServiceImpl();
    }

    @Bean
    public FlowFactory initFlowServer(DefService definitionService, HisTaskService hisTaskService
            , InsService instanceService, NodeService nodeService
            , SkipService skipService, TaskService taskService, DataSource dataSource) {

        DataFillHandlerFactory.set(new DataFillHandlerImpl());

        List<String> mapperList = Arrays.asList("mapper/flow/FlowDefinitionMapper.xml", "mapper/flow/FlowHisTaskMapper.xml"
                , "mapper/flow/FlowInstanceMapper.xml", "mapper/flow/FlowNodeMapper.xml"
                , "mapper/flow/FlowSkipMapper.xml", "mapper/flow/FlowTaskMapper.xml");

        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        sqlSessionFactoryBean.setMapperList(mapperList);
        sqlSessionFactoryBean.initMapperInvoker();

        return new FlowFactory(definitionService, hisTaskService, instanceService
                , nodeService, skipService, taskService);
    }
}
