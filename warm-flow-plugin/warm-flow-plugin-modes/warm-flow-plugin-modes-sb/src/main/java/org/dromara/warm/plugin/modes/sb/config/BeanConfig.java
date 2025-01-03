
package org.dromara.warm.plugin.modes.sb.config;

import org.dromara.warm.flow.core.FlowEngine;
import org.dromara.warm.flow.core.config.WarmFlow;
import org.dromara.warm.flow.core.invoker.FrameInvoker;
import org.dromara.warm.flow.core.orm.dao.*;
import org.dromara.warm.flow.core.service.*;
import org.dromara.warm.flow.core.service.impl.*;
import org.dromara.warm.flow.core.utils.ExpressionUtil;
import org.dromara.warm.plugin.modes.sb.expression.ConditionStrategyDefault;
import org.dromara.warm.plugin.modes.sb.expression.ConditionStrategySpel;
import org.dromara.warm.plugin.modes.sb.expression.ListenerStrategySpel;
import org.dromara.warm.plugin.modes.sb.expression.VariableStrategySpel;
import org.dromara.warm.plugin.modes.sb.helper.SpelHelper;
import org.dromara.warm.plugin.modes.sb.utils.SpringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
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
@Import({SpringUtil.class, SpelHelper.class, CustomBeanDefinitionRegistryPostProcessor.class})
@ConditionalOnProperty(value = "warm-flow.enabled", havingValue = "true", matchIfMissing = true)
public class BeanConfig {

    private static final Logger log = LoggerFactory.getLogger(BeanConfig.class);

    @Bean
    @DependsOn("warmFlowSpringUtil")
    public DefService definitionService() {
        return new DefServiceImpl().setDao(SpringUtil.getBean(FlowDefinitionDao.class));
    }

    @Bean
    @DependsOn("warmFlowSpringUtil")
    public ChartService chartService() {
        return new ChartServiceImpl();
    }

    @Bean
    @DependsOn("warmFlowSpringUtil")
    public NodeService nodeService() {
        return new NodeServiceImpl().setDao(SpringUtil.getBean(FlowNodeDao.class));
    }

    @Bean
    @DependsOn("warmFlowSpringUtil")
    public SkipService skipService() {
        return new SkipServiceImpl().setDao(SpringUtil.getBean(FlowSkipDao.class));
    }

    @Bean
    @DependsOn("warmFlowSpringUtil")
    public InsService instanceService() {
        return new InsServiceImpl().setDao(SpringUtil.getBean(FlowInstanceDao.class));
    }

    @Bean
    @DependsOn("warmFlowSpringUtil")
    public TaskService taskService() {
        return new TaskServiceImpl().setDao(SpringUtil.getBean(FlowTaskDao.class));
    }

    @Bean
    @DependsOn("warmFlowSpringUtil")
    public HisTaskService hisTaskService() {
        return new HisTaskServiceImpl().setDao(SpringUtil.getBean(FlowHisTaskDao.class));
    }

    @Bean
    @DependsOn("warmFlowSpringUtil")
    public UserService flowUserService() {
        return new UserServiceImpl().setDao(SpringUtil.getBean(FlowUserDao.class));
    }

    @Bean
    @DependsOn("warmFlowSpringUtil")
    public FormService flowFormService() {
        return new FormServiceImpl().setDao(SpringUtil.getBean(FlowFormDao.class));
    }

    @Bean
    @ConfigurationProperties(prefix = "warm-flow")
    @DependsOn("warmFlowSpringUtil")
    public WarmFlow initFlow() {
        FrameInvoker.setCfgFunction((key) -> Objects.requireNonNull(SpringUtil.getBean(Environment.class)).getProperty(key));
        FrameInvoker.setBeanFunction(SpringUtil::getBean);
        WarmFlow flowConfig = WarmFlow.init();
        FlowEngine.setFlowConfig(flowConfig);
        setExpression();
        after(flowConfig);
        log.info("【warm-flow】，加载完成");
        return FlowEngine.getFlowConfig();
    }

    private void setExpression() {
        ExpressionUtil.setExpression(new ConditionStrategyDefault());
        ExpressionUtil.setExpression(new ConditionStrategySpel());
        ExpressionUtil.setExpression(new ListenerStrategySpel());
        ExpressionUtil.setExpression(new VariableStrategySpel());
    }

    public void after(WarmFlow flowConfig) {
    }
}
