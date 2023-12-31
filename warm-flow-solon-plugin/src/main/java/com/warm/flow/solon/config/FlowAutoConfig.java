package com.warm.flow.solon.config;

import com.warm.flow.core.FlowFactory;
import com.warm.flow.core.handler.DataFillHandlerImpl;
import com.warm.flow.core.service.*;
import com.warm.flow.core.service.impl.*;
import com.warm.mybatis.core.handler.DataFillHandlerFactory;
import com.warm.mybatis.core.invoker.MapperInvoker;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;

/**
 * @author minliuhua
 * @description: 工作流bean注册配置
 * @date: 2023/6/5 23:01
 */
@Configuration
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
            , TaskService taskService) {

        DataFillHandlerFactory.set(new DataFillHandlerImpl());

        MapperInvoker.setMapperFunction(Solon.context()::getBean);
        return new FlowFactory(definitionService, hisTaskService, instanceService
                , nodeService, skipService, taskService);
    }

}
