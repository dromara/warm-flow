package com.warm.flow.solon.config;

import com.warm.flow.core.FlowFactory;
import com.warm.flow.core.dao.*;
import com.warm.flow.core.invoker.FrameInvoker;
import com.warm.flow.core.service.*;
import com.warm.flow.core.service.impl.*;
import com.warm.flow.orm.dao.*;
import com.warm.flow.orm.invoker.EntityInvoker;
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
    public FlowDefinitionDao definitionDao() {
        return new FlowDefinitionDaoImpl();
    }

    @Bean
    public DefService definitionService(FlowDefinitionDao definitionDao) {
        return new DefServiceImpl().setDao(definitionDao);
    }


    @Bean
    public FlowNodeDao nodeDao() {
        return new FlowNodeDaoImpl();
    }

    @Bean
    public NodeService nodeService(FlowNodeDao nodeDao) {
        return new NodeServiceImpl().setDao(nodeDao);
    }

    @Bean
    public FlowSkipDao skipDao() {
        return new FlowSkipDaoImpl();
    }

    @Bean
    public SkipService skipService(FlowSkipDao skipDao) {
        return new SkipServiceImpl().setDao(skipDao);
    }

    @Bean
    public FlowInstanceDao instanceDao() {
        return new FlowInstanceDaoImpl();
    }

    @Bean
    public InsService instanceService(FlowInstanceDao instanceDao) {
        return new InsServiceImpl().setDao(instanceDao);
    }

    @Bean
    public FlowTaskDao taskDao() {
        return new FlowTaskDaoImpl();
    }

    @Bean
    public TaskService taskService(FlowTaskDao taskDao) {
        return new TaskServiceImpl().setDao(taskDao);
    }

    @Bean
    public FlowHisTaskDao hisTaskDao() {
        return new FlowHisTaskDaoImpl();
    }

    @Bean
    public HisTaskService hisTaskService(FlowHisTaskDao hisTaskDao) {
        return new HisTaskServiceImpl().setDao(hisTaskDao);
    }

    @Bean
    public FlowFactory initFlowServer(DefService definitionService, HisTaskService hisTaskService
            , InsService instanceService, NodeService nodeService, SkipService skipService
            , TaskService taskService) {
        EntityInvoker.setEntity();
        FrameInvoker.setCfgFunction((key) -> Solon.cfg().get(key));
        FrameInvoker.setBeanFunction(Solon.context()::getBean);
        return new FlowFactory(definitionService, hisTaskService, instanceService
                , nodeService, skipService, taskService);
    }

}
