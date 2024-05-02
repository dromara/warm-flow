package com.warm.flow.orm.utils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.warm.flow.core.FlowFactory;
import com.warm.flow.core.config.WarmFlow;
import com.warm.flow.core.entity.RootEntity;
import com.warm.flow.core.handler.TenantHandler;
import com.warm.flow.orm.mapper.WarmMapper;
import com.warm.tools.utils.ObjectUtil;
import com.warm.tools.utils.StringUtils;

import java.util.function.Consumer;

/**
 * mybatis-plus 租户和逻辑删除工具类
 *
 * @author warm
 */
public class TenantDeleteUtil {

    private TenantDeleteUtil() {}

    /**
     * 获取mybatis-plus查询条件, 根据是否租户或者逻辑删除
     * @return
     * @param <T>
     */
    public static <T extends RootEntity> LambdaQueryWrapper<T> getLambdaWrapper() {
        return getLambdaWrapper(null);
    }

    /**
     * 获取mybatis-plus查询条件, 根据是否租户或者逻辑删除
     * @param entity
     * @return
     * @param <T>
     */
    public static <T extends RootEntity> LambdaQueryWrapper<T> getLambdaWrapper(T entity) {
        WarmFlow flowConfig = FlowFactory.getFlowConfig();
        LambdaQueryWrapper<T> queryWrapper = null;

        if (flowConfig.isLogicDelete()) {
            if (ObjectUtil.isNull(entity)) {
                queryWrapper = new LambdaQueryWrapper<>();
            } else {
                queryWrapper = new LambdaQueryWrapper<>(entity);
            }
            queryWrapper.eq(StringUtils.isNotEmpty(flowConfig.getLogicNotDeleteValue()), T::getDelFlag
                    , flowConfig.getLogicNotDeleteValue());
        }

        if (ObjectUtil.isNotNull(FlowFactory.tenantHandler())) {
            TenantHandler tenantHandler = FlowFactory.tenantHandler();
            if (ObjectUtil.isNull(queryWrapper)) {
                if (ObjectUtil.isNull(entity)) {
                    queryWrapper = new LambdaQueryWrapper<>();
                } else {
                    queryWrapper = new LambdaQueryWrapper<>(entity);
                }
            }
            queryWrapper.eq(StringUtils.isNotEmpty(tenantHandler.getTenantId()), T::getTenantId
                    , tenantHandler.getTenantId());
        }
        return queryWrapper;
    }

    /**
     * 获取mybatis-plus查询条件, 根据是否租户或者逻辑删除
     * @return
     * @param <T>
     */
    public static <T> QueryWrapper<T> getQueryWrapper() {
        return getQueryWrapper(null);
    }

    /**
     * 获取mybatis-plus查询条件, 根据是否租户或者逻辑删除
     * @param entity
     * @return
     * @param <T>
     */
    public static <T> QueryWrapper<T> getQueryWrapper(T entity) {
        WarmFlow flowConfig = FlowFactory.getFlowConfig();
        QueryWrapper<T> queryWrapper = null;

        if (flowConfig.isLogicDelete()) {
            if (ObjectUtil.isNull(entity)) {
                queryWrapper = new QueryWrapper<>();
            } else {
                queryWrapper = new QueryWrapper<>(entity);
            }
            queryWrapper.eq(StringUtils.isNotEmpty(flowConfig.getLogicNotDeleteValue()), "del_flag"
                    , flowConfig.getLogicNotDeleteValue());
        }

        if (ObjectUtil.isNotNull(FlowFactory.tenantHandler())) {
            TenantHandler tenantHandler = FlowFactory.tenantHandler();
            if (ObjectUtil.isNull(queryWrapper)) {
                if (ObjectUtil.isNull(entity)) {
                    queryWrapper = new QueryWrapper<>();
                } else {
                    queryWrapper = new QueryWrapper<>(entity);
                }
            }
            queryWrapper.eq(StringUtils.isNotEmpty(tenantHandler.getTenantId()), "tenant_id"
                    , tenantHandler.getTenantId());
        }
        return queryWrapper;
    }

    /**
     * 设置租户和逻辑删除
     * @return
     */
    public static void getEntity(RootEntity entity) {
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
