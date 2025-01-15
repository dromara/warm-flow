/*
 *    Copyright 2024-2025, Warm-Flow (290631660@qq.com).
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.dromara.warm.plugin.modes.sb.config;

import org.dromara.warm.flow.core.FlowEngine;
import org.dromara.warm.flow.core.config.WarmFlow;
import org.dromara.warm.flow.core.invoker.FrameInvoker;
import org.dromara.warm.flow.core.orm.dao.*;
import org.dromara.warm.flow.core.service.*;
import org.dromara.warm.flow.core.service.impl.*;
import org.dromara.warm.flow.core.utils.ExpressionUtil;
import org.dromara.warm.flow.orm.dao.*;
import org.dromara.warm.flow.orm.entity.*;
import org.dromara.warm.plugin.modes.sb.expression.ConditionStrategyDefault;
import org.dromara.warm.plugin.modes.sb.expression.ConditionStrategySpel;
import org.dromara.warm.plugin.modes.sb.expression.ListenerStrategySpel;
import org.dromara.warm.plugin.modes.sb.expression.VariableStrategySpel;
import org.dromara.warm.plugin.modes.sb.helper.SpelHelper;
import org.dromara.warm.plugin.modes.sb.utils.SpringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

import java.util.Objects;

/**
 * 工作流bean注册配置
 *
 * @author warm
 * @since 2023/6/5 23:01
 */
@SuppressWarnings("rawtypes unchecked")
@Import({SpringUtil.class, SpelHelper.class})
@ConditionalOnProperty(value = "warm-flow.enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(WarmFlowProperties.class)
public class BeanConfig {

    private static final Logger log = LoggerFactory.getLogger(BeanConfig.class);

    @Bean
    public FlowDefinitionDao definitionDao() {
        return new FlowDefinitionDaoImpl();
    }

    @Bean
    public DefService definitionService(FlowDefinitionDao definitionDao) {
        return new DefServiceImpl().setDao(definitionDao);
    }

    @Bean
    public ChartService chartService() {
        return new ChartServiceImpl();
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
    public FlowUserDao flowUserDao() {
        return new FlowUserDaoImpl();
    }

    @Bean
    public UserService flowUserService(FlowUserDao userDao) {
        return new UserServiceImpl().setDao(userDao);
    }

    @Bean
    public FlowFormDao formDao() {
        return new FlowFormDaoImpl();
    }

    @Bean
    public FormService flowFormService(FlowFormDao formDao) {
        return new FormServiceImpl().setDao(formDao);
    }

    @Bean
    public WarmFlow initFlow() {
        setNewEntity();
        FrameInvoker.setCfgFunction((key) -> Objects.requireNonNull(SpringUtil.getBean(Environment.class)).getProperty(key));
        FrameInvoker.setBeanFunction(SpringUtil::getBean);
        WarmFlowProperties warmFlow = SpringUtil.getBean(WarmFlowProperties.class);
        warmFlow.init();
        FlowEngine.setFlowConfig(warmFlow);
        setExpression();
        after(warmFlow);
        log.info("【warm-flow】，加载完成");
        return warmFlow;
    }

    private void setExpression() {
        ExpressionUtil.setExpression(new ConditionStrategyDefault());
        ExpressionUtil.setExpression(new ConditionStrategySpel());
        ExpressionUtil.setExpression(new ListenerStrategySpel());
        ExpressionUtil.setExpression(new VariableStrategySpel());
    }

    public void setNewEntity() {
        FlowEngine.setNewDef(FlowDefinition::new);
        FlowEngine.setNewIns(FlowInstance::new);
        FlowEngine.setNewHisTask(FlowHisTask::new);
        FlowEngine.setNewNode(FlowNode::new);
        FlowEngine.setNewSkip(FlowSkip::new);
        FlowEngine.setNewTask(FlowTask::new);
        FlowEngine.setNewUser(FlowUser::new);
        FlowEngine.setNewForm(FlowForm::new);
    }

    public void after(WarmFlow flowConfig) {
    }
}
