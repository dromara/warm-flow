package com.warm.flow.core.test.handle;

import com.warm.flow.core.handler.TenantHandler;

/**
 * 全局租户处理器（可配置文件注入，也可用@Bean/@Component方式）
 *
 * @author warm
 */
public class CustomTenantHandler implements TenantHandler {


    @Override
    public String getTenantId() {
        return "000000";
    }
}
