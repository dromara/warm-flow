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
package com.warm.flow.orm.utils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.warm.flow.core.FlowFactory;
import com.warm.flow.core.config.WarmFlow;
import com.warm.flow.core.entity.RootEntity;
import com.warm.flow.core.handler.TenantHandler;
import com.warm.flow.core.utils.ObjectUtil;
import com.warm.flow.core.utils.StringUtils;

/**
 * mybatis-plus 租户和逻辑删除工具类
 *
 * @author warm
 */
public class TenantDeleteUtil {

    private TenantDeleteUtil() {
    }


    /**
     * 获取mybatis-plus查询条件, 根据是否租户或者逻辑删除，有默认值
     *
     * @param entity
     * @param <T>
     * @return
     */
    public static <T extends RootEntity> LambdaQueryWrapper<T> getLambdaWrapperDefault(T entity) {
        LambdaQueryWrapper<T> queryWrapper = getLambdaWrapper(entity);
        queryWrapper.setEntityClass((Class<T>) entity.getClass());
        return queryWrapper;
    }

    /**
     * 获取mybatis-plus查询条件, 根据是否租户或者逻辑删除
     *
     * @param entity
     * @param <T>
     * @return
     */
    public static <T extends RootEntity> LambdaQueryWrapper<T> getLambdaWrapper(T entity) {
        WarmFlow flowConfig = FlowFactory.getFlowConfig();
        LambdaQueryWrapper<T> queryWrapper = new LambdaQueryWrapper<>(entity);

        if (flowConfig.isLogicDelete()) {
            queryWrapper.eq(StringUtils.isNotEmpty(flowConfig.getLogicNotDeleteValue()), T::getDelFlag
                            , flowConfig.getLogicNotDeleteValue());
        }

        if (ObjectUtil.isNotNull(FlowFactory.tenantHandler())) {
            TenantHandler tenantHandler = FlowFactory.tenantHandler();
            queryWrapper.eq(StringUtils.isNotEmpty(tenantHandler.getTenantId()), T::getTenantId
                    , tenantHandler.getTenantId());
        }
        return queryWrapper;
    }

    /**
     * 获取mybatis-plus查询条件, 根据是否租户或者逻辑删除，有默认值
     *
     * @param entity
     * @param <T>
     * @return
     */
    public static <T> QueryWrapper<T> getQueryWrapperDefault(T entity) {
        QueryWrapper<T> queryWrapper = getQueryWrapper(entity);
        queryWrapper.setEntityClass((Class<T>) entity.getClass());
        return queryWrapper;
    }

    /**
     * 获取mybatis-plus查询条件, 根据是否租户或者逻辑删除
     *
     * @param entity
     * @param <T>
     * @return
     */
    public static <T> QueryWrapper<T> getQueryWrapper(T entity) {
        WarmFlow flowConfig = FlowFactory.getFlowConfig();
        QueryWrapper<T> queryWrapper = new QueryWrapper<>(entity);

        if (flowConfig.isLogicDelete()) {
            queryWrapper.eq(StringUtils.isNotEmpty(flowConfig.getLogicNotDeleteValue()), "del_flag"
                            , flowConfig.getLogicNotDeleteValue());
        }

        if (ObjectUtil.isNotNull(FlowFactory.tenantHandler())) {
            TenantHandler tenantHandler = FlowFactory.tenantHandler();
            queryWrapper.eq(StringUtils.isNotEmpty(tenantHandler.getTenantId()), "tenant_id"
                    , tenantHandler.getTenantId());
        }
        return queryWrapper;
    }

    /**
     * 设置租户和逻辑删除
     *
     * @return
     */
    public static void fillEntity(RootEntity entity) {
        WarmFlow flowConfig = FlowFactory.getFlowConfig();
        if (flowConfig.isLogicDelete()) {
            entity.setDelFlag(flowConfig.getLogicNotDeleteValue());
        }

        if (ObjectUtil.isNotNull(FlowFactory.tenantHandler())) {
            TenantHandler tenantHandler = FlowFactory.tenantHandler();
            entity.setTenantId(tenantHandler.getTenantId());
        }
    }

    public static <T extends RootEntity> LambdaUpdateWrapper<T> deleteWrapper(T entity) {
        LambdaUpdateWrapper<T> lambdaUpdateWrapper = null;

        if (ObjectUtil.isNotNull(FlowFactory.tenantHandler())) {
            TenantHandler tenantHandler = FlowFactory.tenantHandler();
            entity.setTenantId(tenantHandler.getTenantId());
        }

        WarmFlow flowConfig = FlowFactory.getFlowConfig();
        if (flowConfig.isLogicDelete()) {
            lambdaUpdateWrapper = new LambdaUpdateWrapper<>(entity)
                    .set(StringUtils.isNotEmpty(flowConfig.getLogicDeleteValue()), T::getDelFlag
                            , flowConfig.getLogicDeleteValue())
                    .eq(StringUtils.isNotEmpty(flowConfig.getLogicNotDeleteValue()), T::getDelFlag
                            , flowConfig.getLogicNotDeleteValue());
        }
        return lambdaUpdateWrapper;
    }
}
