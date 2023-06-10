package com.monkey.flow.spring.boot.config;

import com.monkey.flow.core.*;
import com.monkey.flow.core.service.*;
import com.monkey.flow.core.service.impl.*;
import com.monkey.flow.core.webService.*;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @description:  工作流bean注册配置
 * @author minliuhua
 * @date: 2023/6/5 23:01
 */
@MapperScan({"com.monkey.flow.core.mapper"})
@Configuration
public class FlowBeanRegister
{
  @Bean
  public DefAppService defAppService()
  {
    return new DefAppServiceImpl();
  }

  @Bean
  public NodeAppService nodeAppService()
  {
    return new NodeAppServiceImpl();
  }

  @Bean
  public SkipAppService skipAppService()
  {
    return new SkipAppServiceImpl();
  }

  @Bean
  public InsAppService insAppService()
  {
    return new InsAppServiceImpl();
  }

  @Bean
  public TaskAppService taskAppService()
  {
    return new TaskAppServiceImpl();
  }

  @Bean
  public HisTaskAppService hisTaskAppService()
  {
    return new HisTaskAppServiceImpl();
  }

  @Bean
  public IFlowDefinitionService definitionService()
  {
    return new FlowDefinitionServiceImpl();
  }

  @Bean
  public IFlowNodeService nodeService()
  {
    return new FlowNodeServiceImpl();
  }

  @Bean
  public IFlowSkipService skipService()
  {
    return new FlowSkipServiceImpl();
  }

  @Bean
  public IFlowInstanceService instanceService()
  {
    return new FlowInstanceServiceImpl();
  }

  @Bean
  public IFlowTaskService taskService()
  {
    return new FlowTaskServiceImpl();
  }

  @Bean
  public IFlowHisTaskService hisTaskService()
  {
    return new FlowHisTaskServiceImpl();
  }
}
