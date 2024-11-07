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

import com.mybatisflex.core.query.QueryWrapper;
import org.dromara.warm.flow.core.FlowFactory;
import org.dromara.warm.flow.core.config.WarmFlow;
import org.dromara.warm.flow.core.entity.RootEntity;
import org.dromara.warm.flow.core.handler.TenantHandler;
import org.dromara.warm.flow.core.utils.ObjectUtil;

/**
 * mybatis-flex 租户和逻辑删除工具类
 *
 * @author xiarg
 * @since 2024/5/10 11:16
 */
public class TenantDeleteUtil {

    private TenantDeleteUtil() {}

    /**
     * 获取默认的 QueryWrapper 处理租户和逻辑删除
     *
     * @param entity 实体类
     * @return QueryWrapper
     * @author xiarg
     * @since 2024/5/10 11:16
     */
    public static <T extends RootEntity> QueryWrapper getDefaultWrapper(T entity) {
        QueryWrapper queryWrapper = QueryWrapper.create(entity);
        WarmFlow flowConfig = FlowFactory.getFlowConfig();
        handleQueryWrapper(queryWrapper, flowConfig);
        return queryWrapper;
    }
    /**
     * 获取默认的删除的 QueryWrapper 处理租户和逻辑删除
     *
     * @param entity 实体类
     * @return QueryWrapper
     * @author xiarg
     * @since 2024/5/10 11:16
     */
    public static <T extends RootEntity> QueryWrapper getDelWrapper(T entity) {
        QueryWrapper queryWrapper = QueryWrapper.create(entity);
        WarmFlow flowConfig = FlowFactory.getFlowConfig();
        handleQueryWrapper(queryWrapper, flowConfig);
        return queryWrapper;
    }
    /**
     * 获取默认的根据id查询的 QueryWrapper 处理租户和逻辑删除
     *
     * @param entity 实体类
     * @return QueryWrapper
     * @author xiarg
     * @since 2024/5/10 11:16
     */
    public static <T extends RootEntity> QueryWrapper getIdWrapper(T entity) {
        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.eq("id", entity.getId());
        WarmFlow flowConfig = FlowFactory.getFlowConfig();
        handleQueryWrapper(queryWrapper, flowConfig);
        return queryWrapper;
    }
    /**
     * 获取默认的根据id查询的 QueryWrapper
     *
     * @param queryWrapper mybatis-flex 查询处理器
     * @param flowConfig 流程配置
     * @author xiarg
     * @since 2024/5/10 11:16
     */
    private static void handleQueryWrapper(QueryWrapper queryWrapper, WarmFlow flowConfig) {
        if (ObjectUtil.isNotNull(FlowFactory.tenantHandler())) {
            TenantHandler tenantHandler = FlowFactory.tenantHandler();
            queryWrapper.eq("tenant_Id", tenantHandler.getTenantId());
        }
        if (flowConfig.isLogicDelete()) {
            queryWrapper.eq("del_Flag", flowConfig.getLogicNotDeleteValue());
        }
    }
    /**
     * 实体类处理租户和逻辑删除
     *
     * @param entity 实体类
     * @author xiarg
     * @since 2024/5/10 11:16
     */
    public static <T extends RootEntity> void fillEntity(T entity) {
        WarmFlow flowConfig = FlowFactory.getFlowConfig();
        if (flowConfig.isLogicDelete()) {
            entity.setDelFlag(flowConfig.getLogicNotDeleteValue());
        }
        if (ObjectUtil.isNotNull(FlowFactory.tenantHandler())) {
            TenantHandler tenantHandler = FlowFactory.tenantHandler();
            entity.setTenantId(tenantHandler.getTenantId());
        }
    }
    /**
     * 删除时，实体类处理租户和逻辑删除
     *
     * @param entity 实体类
     * @return T
     * @author xiarg
     * @since 2024/5/10 11:16
     */
    public static <T extends RootEntity> T delFillEntity(T entity) {
        WarmFlow flowConfig = FlowFactory.getFlowConfig();
        if (flowConfig.isLogicDelete()) {
            entity.setDelFlag(flowConfig.getLogicDeleteValue());
        }
        return entity;
    }
}
