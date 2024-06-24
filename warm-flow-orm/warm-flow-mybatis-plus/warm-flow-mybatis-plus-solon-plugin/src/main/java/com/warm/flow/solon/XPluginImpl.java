package com.warm.flow.solon;

import com.warm.flow.solon.config.FlowAutoConfig;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.Configuration;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.event.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

/**
 * Warm-Flow工作流插件
 *
 * @author warm
 */
public class XPluginImpl implements Plugin {

    private static final Logger log = LoggerFactory.getLogger(XPluginImpl.class);

    @Override
    public void start(AppContext context) {
        context.beanMake(FlowAutoConfig.class);
        EventBus.subscribe(Configuration.class, e -> {
            List<String> mapperList = Arrays.asList("warm/flow/FlowDefinitionMapper.xml", "warm/flow/FlowHisTaskMapper.xml"
                    , "warm/flow/FlowInstanceMapper.xml", "warm/flow/FlowNodeMapper.xml"
                    , "warm/flow/FlowSkipMapper.xml", "warm/flow/FlowTaskMapper.xml", "warm/flow/FlowUserMapper.xml");

            try {
                for (String mapper : mapperList) {
                    XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(Resources.getResourceAsStream(mapper),
                            e, getClass().getResource("/") + mapper, e.getSqlFragments());
                    xmlMapperBuilder.parse();
                }
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
        log.debug("warm插件加载: 成功加载[Warm-Flow工作流]插件");
    }
}