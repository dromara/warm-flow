package com.warm.flow.spring.boot.config;

import com.warm.flow.core.FlowFactory;
import com.warm.flow.core.handler.DataFillHandlerImpl;
import com.warm.flow.core.service.*;
import com.warm.flow.core.service.impl.*;
import com.warm.flow.spring.boot.utils.SpringUtil;
import com.warm.mybatis.core.handler.DataFillHandlerFactory;
import com.warm.mybatis.core.invoker.MapperInvoker;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.Arrays;
import java.util.List;

/**
 * @author minliuhua
 * @description: 工作流bean注册配置
 * @date: 2023/6/5 23:01
 */
@Configuration
@MapperScan("com.warm.**.mapper")
@Import(SpringUtil.class)
public class FlowAutoConfig {

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
            , InsService instanceService, NodeService nodeService, SkipService skipService
            , TaskService taskService, SqlSessionFactory sqlSessionFactory) {

        List<String> mapperList = Arrays.asList("warm/flow/FlowDefinitionMapper.xml", "warm/flow/FlowHisTaskMapper.xml"
                , "warm/flow/FlowInstanceMapper.xml", "warm/flow/FlowNodeMapper.xml"
                , "warm/flow/FlowSkipMapper.xml", "warm/flow/FlowTaskMapper.xml");

        org.apache.ibatis.session.Configuration configuration = sqlSessionFactory.getConfiguration();
        try {
            for (String mapper : mapperList) {
                XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(Resources.getResourceAsStream(mapper),
                        configuration, getClass().getResource("/") + mapper, configuration.getSqlFragments());
                xmlMapperBuilder.parse();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        DataFillHandlerFactory.set(new DataFillHandlerImpl());
        MapperInvoker.setMapperFunction(SpringUtil::getBean);
        return new FlowFactory(definitionService, hisTaskService, instanceService
                , nodeService, skipService, taskService);
    }

}
