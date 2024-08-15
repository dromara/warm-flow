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
package com.warm.flow.sb.test.config;

import com.warm.flow.core.handler.DataFillHandler;
import com.warm.flow.core.test.GlobalListener.*;
import com.warm.flow.core.test.Listener.*;
import com.warm.flow.core.test.handle.CustomDataFillHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * warm-flow配置文件
 *
 * @author warm
 */
@Configuration
public class WarmFlowConfig {

    /**
     * 自定义填充 （可配置文件注入，也可用@Bean/@Component方式）
     */
    @Bean
    public DataFillHandler dataFillHandler() {
        return new CustomDataFillHandler();
    }

//    /**
//     * 全家租户处理器（可配置文件注入，也可用@Bean/@Component方式）
//     */
//    @Bean
//    public TenantHandler tenantHandler() {
//        return new CustomTenantHandler();
//    }

    /**
     * 节点创建监听器，可用@Bean/@Component方式注入
     */
    @Bean
    public CreateListener createListener() {
        return new CreateListener();
    }

    /**
     * 节点完成监听器，可用@Bean/@Component方式注入
     */
    @Bean
    public FinishListener finishListener() {
        return new FinishListener();
    }

    /**
     * 节点权限监听器，可用@Bean/@Component方式注入
     */
    @Bean
    public PermissionListener permissionListener() {
        return new PermissionListener();
    }

    /**
     * 节点开始监听器，可用@Bean/@Component方式注入
     */
    @Bean
    public StartListener startListener() {
        return new StartListener();
    }

    /**
     * 节点分派监听器，可用@Bean/@Component方式注入
     */
    @Bean
    public AssignmentListener assignmentListener() {
        return new AssignmentListener();
    }

    /**
     * 全局创建监听器，可用@Bean/@Component方式注入
     */
    @Bean
    public GlobalCreateListener globalCreateListener() {
        return new GlobalCreateListener();
    }

    /**
     * 全局完成监听器，可用@Bean/@Component方式注入
     */
    @Bean
    public GlobalFinishListener globalFinishListener() {
        return new GlobalFinishListener();
    }

    /**
     * 全局权限监听器，可用@Bean/@Component方式注入
     */
    @Bean
    public GlobalPermissionListener globalPermissionListener() {
        return new GlobalPermissionListener();
    }

    /**
     * 全局开始监听器，可用@Bean/@Component方式注入
     */
    @Bean
    public GlobalStartListener globalStartListener() {
        return new GlobalStartListener();
    }

    /**
     * 全局分派监听器，可用@Bean/@Component方式注入
     */
    @Bean
    public GlobalAssignmentListener globalAssignmentListener() {
        return new GlobalAssignmentListener();
    }
}