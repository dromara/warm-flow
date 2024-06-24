package com.warm.flow.orm.utils;

import com.mybatisflex.core.query.QueryWrapper;
import com.warm.flow.core.FlowFactory;
import com.warm.flow.core.config.WarmFlow;
import com.warm.flow.core.entity.RootEntity;
import com.warm.flow.core.handler.TenantHandler;
import com.warm.flow.core.utils.ObjectUtil;

/**
 * mybatis-flex 租户和逻辑删除工具类
 *
 * @author xiarg
 * @date 2024/5/10 11:16
 */
public class TenantDeleteUtil {

    private TenantDeleteUtil() {}

    /**
     * 获取默认的 QueryWrapper 处理租户和逻辑删除
     *
     * @param entity 实体类
     * @return QueryWrapper
     * @author xiarg
     * @date 2024/5/10 11:16
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
     * @date 2024/5/10 11:16
     */
    public static <T extends RootEntity> QueryWrapper getDelWrapper(T entity) {
        QueryWrapper queryWrapper = QueryWrapper.create();
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
     * @date 2024/5/10 11:16
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
     * @date 2024/5/10 11:16
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
     * @date 2024/5/10 11:16
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
     * @date 2024/5/10 11:16
     */
    public static <T extends RootEntity> T delFillEntity(T entity) {
        WarmFlow flowConfig = FlowFactory.getFlowConfig();
        if (flowConfig.isLogicDelete()) {
            entity.setDelFlag(flowConfig.getLogicDeleteValue());
        }
        return entity;
    }
}
