package com.warm.flow.spring.boot.config;

import com.warm.flow.core.FlowFactory;
import com.warm.flow.core.handler.DataFillHandlerImpl;
import com.warm.flow.core.service.*;
import com.warm.flow.core.service.impl.*;
import com.warm.flow.spring.boot.utils.SpringUtil;
import com.warm.mybatis.core.handler.DataFillHandlerFactory;
import com.warm.mybatis.core.invoker.MapperInvoker;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author minliuhua
 * @description: 工作流bean注册配置
 * @date: 2023/6/5 23:01
 */
@Configuration
@MapperScan("com.warm.**.mapper")
@Import(SpringUtil.class)
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
            , InsService instanceService, NodeService nodeService, SkipService skipService
            , TaskService taskService) {

        DataFillHandlerFactory.set(new DataFillHandlerImpl());

        MapperInvoker.setMapperFunction(SpringUtil::getBean);
        return new FlowFactory(definitionService, hisTaskService, instanceService
                , nodeService, skipService, taskService);
    }

}
