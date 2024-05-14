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
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author warm
 * @description: 工作流bean注册配置
 * @date: 2023/6/5 23:01
 */
@Configuration
@MapperScan("com.warm.flow.orm.mapper")
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

    @Bean
    public WarmFlow initFlow(SqlSessionFactory sqlSessionFactory) {
        loadXml(sqlSessionFactory);
        // 设置创建对象方法
        EntityInvoker.setNewEntity();
        FrameInvoker.setCfgFunction((key) -> Objects.requireNonNull(SpringUtil.getBean(Environment.class)).getProperty(key));
        FrameInvoker.setBeanFunction(SpringUtil::getBean);
        WarmFlow flowConfig = WarmFlow.init();
        FlowFactory.setFlowConfig(flowConfig);
        log.info("warm-flow初始化结束");
        return FlowFactory.getFlowConfig();
    }

    private void loadXml(SqlSessionFactory sqlSessionFactory) {
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
    }
}
