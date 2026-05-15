package com.ruoyi.system.handle;

import org.dromara.warm.flow.core.handler.TenantHandler;

/**
 * 全局租户处理器（可通过配置文件注入，也可用@Bean/@Component方式
 *
 * @author warm
 */
public class CustomTenantHandler implements TenantHandler {


    @Override
    public String getTenantId() {
        // 这里返回系统中的当前办理人的租户ID，一般会有工具类获取
        return "000000";
    }
}
