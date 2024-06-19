package com.warm.flow.orm.utils;

import com.mybatisflex.core.query.QueryWrapper;
import com.warm.flow.core.FlowFactory;
import com.warm.flow.core.config.WarmFlow;
import com.warm.flow.core.entity.RootEntity;
import com.warm.flow.core.handler.TenantHandler;
import com.warm.flow.core.utils.ObjectUtil;

/**
 * mybatis-plus 租户和逻辑删除工具类
 *
 * @author warm
 */
public class TenantDeleteUtil {

    private TenantDeleteUtil() {}

    /**
     * 流程用户Mapper接口
     *
     * @param entity 实体类
     * @return QueryWrapper
     * @author xiarg
     * @date 2024/5/10 11:16
     */
    public static <T extends RootEntity> QueryWrapper getDefaultWrapper(T entity) {
        QueryWrapper queryWrapper = QueryWrapper.create(entity);
        WarmFlow flowConfig = FlowFactory.getFlowConfig();
        if (ObjectUtil.isNotNull(FlowFactory.tenantHandler())) {
            TenantHandler tenantHandler = FlowFactory.tenantHandler();
            queryWrapper.eq("tenant_Id", tenantHandler.getTenantId());
        }
        if (flowConfig.isLogicDelete()) {
            queryWrapper.eq("del_Flag", flowConfig.getLogicNotDeleteValue());
        }
        return queryWrapper;
    }

    /**
     * 获取mybatis-plus查询条件, 根据是否租户或者逻辑删除
     * @param <T>
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
}
