package com.warm.flow.solon;

import com.warm.flow.solon.config.FlowConfig;
import com.warm.mybatis.solon.config.MybatisConfig;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;

/**
 * redis缓存插件
 *
 * @author warm
 */
public class XPluginImpl implements Plugin {
    @Override
    public void start(AppContext context) {
        context.beanMake(FlowConfig.class);
    }
}