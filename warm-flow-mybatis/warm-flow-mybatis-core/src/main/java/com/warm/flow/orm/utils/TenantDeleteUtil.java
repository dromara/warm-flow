package com.warm.flow.orm.utils;

import com.warm.flow.core.FlowFactory;
import com.warm.flow.core.config.WarmFlow;
import com.warm.flow.core.entity.RootEntity;
import com.warm.flow.core.handler.TenantHandler;
import com.warm.tools.utils.ObjectUtil;

import java.util.function.Supplier;

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
    public static <T extends RootEntity> T getEntity(Supplier<T> supplier) {
        WarmFlow flowConfig = FlowFactory.getFlowConfig();
        T t = null;
        if (flowConfig.isLogicDelete()) {
            t = supplier.get();
            t.setDelFlag(flowConfig.getLogicDeleteValue());
        }

        if (ObjectUtil.isNotNull(FlowFactory.tenantHandler())) {
            TenantHandler tenantHandler = FlowFactory.tenantHandler();
            if (ObjectUtil.isNull(t)) {
                t = supplier.get();;
            }
            t.setTenantId(tenantHandler.getTenantId());
        }
        return t;
    }
}
