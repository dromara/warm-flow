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
package org.dromara.warm.flow.spring.boot.config;

import org.dromara.warm.flow.core.constant.ExceptionCons;
import org.dromara.warm.flow.core.exception.FlowException;
import org.dromara.warm.flow.orm.utils.FlowJpaConfigCons;
import org.dromara.warm.plugin.modes.sb.config.BeanConfig;
import org.dromara.warm.plugin.modes.sb.utils.SpringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import javax.persistence.spi.PersistenceProvider;
import javax.sql.DataSource;

/**
 * 工作流bean注册配置
 *
 * @author warm
 * @since 2023/6/5 23:01
 */
@Configuration
@ConditionalOnProperty(value = "warm-flow.enabled", havingValue = "true", matchIfMissing = true)
public class FlowAutoConfig extends BeanConfig {

}
