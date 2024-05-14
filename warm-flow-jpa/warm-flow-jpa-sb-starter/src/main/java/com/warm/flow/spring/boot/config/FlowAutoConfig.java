package com.warm.flow.spring.boot.config;

import com.warm.flow.core.FlowFactory;
import com.warm.flow.core.config.WarmFlow;
import com.warm.flow.core.dao.*;
import com.warm.flow.core.invoker.FrameInvoker;
import com.warm.flow.core.service.*;
import com.warm.flow.core.service.impl.*;
import com.warm.flow.orm.dao.*;
import com.warm.flow.orm.invoker.EntityInvoker;
import com.warm.flow.spring.boot.utils.SpringUtil;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import javax.sql.DataSource;

/**
 * @author warm
 * @description: 工作流bean注册配置
 * @date: 2023/6/5 23:01
 */
@Configuration
@Import(SpringUtil.class)
public class FlowAutoConfig {

    private static final Logger log = LoggerFactory.getLogger(FlowAutoConfig.class);

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
    public FlowUserDao userDao() {
        return new FlowUserDaoImpl();
    }

    @Bean("flowUserService")
    public FlowUserService userService(FlowUserDao userDao) {
        return new FlowUserServiceImpl().setDao(userDao);
    }

    @Bean(name="entityManagerFactoryWarmFlow")
    public LocalContainerEntityManagerFactoryBean primaryEntityManagerFactory(DataSource dataSource, JpaProperties jpaProperties) {
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setDataSource(dataSource);
        factory.setJpaPropertyMap(jpaProperties.getProperties());
        factory.setPackagesToScan("com.warm.flow.orm.entity");
        factory.setPersistenceProviderClass(HibernatePersistenceProvider.class);
        factory.setPersistenceUnitName("warm-flow-jpa");
        factory.afterPropertiesSet();
        return factory;
    }

    @Bean
    public WarmFlow initFlow() {
        // 设置创建对象方法
        EntityInvoker.setNewEntity();
        FrameInvoker.setCfgFunction((key) -> SpringUtil.getBean(Environment.class).getProperty(key));
        FrameInvoker.setBeanFunction(SpringUtil::getBean);
        WarmFlow flowConfig = WarmFlow.init();
        FlowFactory.setFlowConfig(flowConfig);
        log.info("warm-flow初始化结束");
        return FlowFactory.getFlowConfig();
    }

}
