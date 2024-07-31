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
package com.warm.flow.spring.boot.config;

import com.warm.flow.core.FlowFactory;
import com.warm.flow.core.config.WarmFlow;
import com.warm.flow.orm.utils.CommonUtil;
import com.warm.plugin.modes.sb.config.BeanConfig;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * @author warm
 * @description: 工作流bean注册配置
 * @date: 2023/6/5 23:01
 */
@Configuration
@MapperScan("com.warm.flow.orm.mapper")
public class FlowAutoConfig extends BeanConfig {

    private static final Logger log = LoggerFactory.getLogger(FlowAutoConfig.class);

    static {
        log.info("【warm-flow】，mybatis的springboot扩展包初始化开始");
    }

    @Resource
    private SqlSessionFactory sqlSessionFactory;

    @Override
    public WarmFlow after(WarmFlow flowConfig) {
        loadXml(sqlSessionFactory);
        CommonUtil.setDataSourceType(flowConfig, sqlSessionFactory.getConfiguration());
        return FlowFactory.getFlowConfig();
    }

    private void loadXml(SqlSessionFactory sqlSessionFactory) {
        List<String> mapperList = Arrays.asList("warm/flow/FlowDefinitionMapper.xml", "warm/flow/FlowHisTaskMapper.xml"
                , "warm/flow/FlowInstanceMapper.xml", "warm/flow/FlowNodeMapper.xml"
                , "warm/flow/FlowSkipMapper.xml", "warm/flow/FlowTaskMapper.xml", "warm/flow/FlowUserMapper.xml");
        org.apache.ibatis.session.Configuration configuration = sqlSessionFactory.getConfiguration();
        configuration.setJdbcTypeForNull(JdbcType.NULL);
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
