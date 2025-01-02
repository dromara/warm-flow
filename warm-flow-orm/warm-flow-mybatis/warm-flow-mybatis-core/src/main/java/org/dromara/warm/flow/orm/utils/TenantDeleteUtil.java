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
package org.dromara.warm.flow.orm.utils;

import org.dromara.warm.flow.core.FlowEngine;
import org.dromara.warm.flow.core.config.WarmFlow;
import org.dromara.warm.flow.core.entity.RootEntity;
import org.dromara.warm.flow.core.handler.TenantHandler;
import org.dromara.warm.flow.core.utils.ObjectUtil;

/**
 * mybatis-plus 租户和逻辑删除工具类
 *
 * @author warm
 */
public class TenantDeleteUtil {

    private TenantDeleteUtil() {
    }

    /**
     * 获取mybatis-plus查询条件, 根据是否租户或者逻辑删除
     *
     * @param <T>
     */
    public static <T extends RootEntity> T getEntity(T entity) {
        WarmFlow flowConfig = FlowEngine.getFlowConfig();
        if (flowConfig.isLogicDelete()) {
            entity.setDelFlag(flowConfig.getLogicNotDeleteValue());
        }

        TenantHandler tenantHandler = FlowEngine.tenantHandler();
        if (ObjectUtil.isNotNull(tenantHandler)) {
            entity.setTenantId(tenantHandler.getTenantId());
        }
        return entity;
    }
}
