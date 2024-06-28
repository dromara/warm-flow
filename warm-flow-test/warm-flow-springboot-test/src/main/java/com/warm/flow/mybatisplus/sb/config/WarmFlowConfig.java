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
package com.warm.flow.mybatisplus.sb.config;

import com.warm.flow.core.handler.DataFillHandler;
import com.warm.flow.core.test.Listener.CreateListener;
import com.warm.flow.core.test.Listener.FinishListener;
import com.warm.flow.core.test.Listener.PermissionListener;
import com.warm.flow.core.test.Listener.StartListener;
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
     * 创建监听器，可用@Bean/@Component方式注入
     */
    @Bean
    public CreateListener createListener() {
        return new CreateListener();
    }

    /**
     * 完成监听器，可用@Bean/@Component方式注入
     */
    @Bean
    public FinishListener finishListener() {
        return new FinishListener();
    }

    /**
     * 权限监听器，可用@Bean/@Component方式注入
     */
    @Bean
    public PermissionListener permissionListener() {
        return new PermissionListener();
    }

    /**
     * 开始监听器，可用@Bean/@Component方式注入
     */
    @Bean
    public StartListener startListener() {
        return new StartListener();
    }
}