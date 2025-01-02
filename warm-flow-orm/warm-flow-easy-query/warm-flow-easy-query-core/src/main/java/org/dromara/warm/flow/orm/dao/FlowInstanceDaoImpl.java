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
package org.dromara.warm.flow.orm.dao;

import org.dromara.warm.flow.core.FlowEngine;
import org.dromara.warm.flow.core.orm.dao.FlowInstanceDao;
import org.dromara.warm.flow.core.utils.StringUtils;
import org.dromara.warm.flow.orm.entity.FlowInstance;
import org.dromara.warm.flow.orm.entity.proxy.FlowInstanceProxy;
import org.dromara.warm.flow.orm.utils.TenantDeleteUtil;

import java.util.List;
import java.util.Objects;

/**
 * 流程实例Mapper接口
 *
 * @author link2fun
 */
public class FlowInstanceDaoImpl extends WarmDaoImpl<FlowInstance, FlowInstanceProxy> implements FlowInstanceDao<FlowInstance> {
    @Override
    public Class<FlowInstance> entityClass() {
        return FlowInstance.class;
    }

    @Override
    public FlowInstance newEntity() {
        return new FlowInstance();
    }

    @Override
    public int delete(FlowInstance entity) {

        TenantDeleteUtil.applyContextCondition(entity);
        final boolean logicDelete = FlowEngine.getFlowConfig().isLogicDelete();



        return (int) entityQuery().deletable(entityClass())
            .useLogicDelete(logicDelete)
            .allowDeleteStatement(!logicDelete)
            .where(proxy -> buildDeleteEqCondition(entity, proxy))
            .executeRows();
    }

    /** 构建删除的过滤条件 使用 = 拼接， 参考 mybatis 实现 */
    private static void buildDeleteEqCondition(FlowInstance entity, FlowInstanceProxy proxy) {
        proxy.id().eq(Objects.nonNull(entity.getId()), entity.getId());
        proxy.businessId().eq(StringUtils.isNotEmpty(entity.getBusinessId()), entity.getBusinessId()); // 业务id
        proxy.definitionId().eq(Objects.nonNull(entity.getDefinitionId()), entity.getDefinitionId()); // 流程定义id
        proxy.nodeCode().eq(StringUtils.isNotEmpty(entity.getNodeCode()), entity.getNodeCode()); // 节点编码
        proxy.nodeName().eq(StringUtils.isNotEmpty(entity.getNodeName()), entity.getNodeName()); // 节点名称
        proxy.nodeType().eq(Objects.nonNull(entity.getNodeType()), entity.getNodeType()); // 节点类型
        proxy.flowStatus().eq(Objects.nonNull(entity.getFlowStatus()), entity.getFlowStatus()); // 流程状态
        proxy.variable().eq(StringUtils.isNotEmpty(entity.getVariable()), entity.getVariable()); // 流程变量
        proxy.createBy().eq(StringUtils.isNotEmpty(entity.getCreateBy()), entity.getCreateBy()); // 创建人
        proxy.createTime().eq(Objects.nonNull(entity.getCreateTime()), entity.getCreateTime()); // 创建时间
        proxy.updateTime().eq(Objects.nonNull(entity.getUpdateTime()), entity.getUpdateTime()); // 更新时间
        proxy.ext().eq(StringUtils.isNotEmpty(entity.getExt()), entity.getExt()); // 扩展字段
        proxy.tenantId().eq(StringUtils.isNotEmpty(entity.getTenantId()), entity.getTenantId()); // 租户id
    }

    @Override
    public List<FlowInstance> getByDefIds(List<Long> defIds) {
        FlowInstance entity = newEntity();
        TenantDeleteUtil.applyContextCondition(entity);
        String tenantId = entity.getTenantId();
        final boolean logicDelete = FlowEngine.getFlowConfig().isLogicDelete();

        return entityQuery().queryable(entityClass())
            .useLogicDelete(logicDelete)
            .where(proxy -> {
                proxy.definitionId().in(defIds); // 流程定义id过滤
                proxy.tenantId().eq(StringUtils.isNotEmpty(tenantId), tenantId);  // 租户过滤
            }).toList();
    }
}
