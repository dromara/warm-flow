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
import org.dromara.warm.flow.core.orm.dao.FlowTaskDao;
import org.dromara.warm.flow.core.utils.StringUtils;
import org.dromara.warm.flow.orm.entity.FlowTask;
import org.dromara.warm.flow.orm.entity.proxy.FlowTaskProxy;
import org.dromara.warm.flow.orm.utils.TenantDeleteUtil;

import java.util.List;
import java.util.Objects;

/**
 * 待办任务Mapper接口
 * @author link2fun
 */
public class FlowTaskDaoImpl extends WarmDaoImpl<FlowTask, FlowTaskProxy> implements FlowTaskDao<FlowTask> {
    @Override
    public int deleteByInsIds(List<Long> instanceIds) {
        FlowTask entity = newEntity();
        TenantDeleteUtil.applyContextCondition(entity);
        final boolean logicDelete = FlowEngine.getFlowConfig().isLogicDelete();
        return (int) entityQuery().deletable(entityClass())
            .where(proxy -> {
                proxy.instanceId().in(instanceIds); // 流程实例表id
                proxy.tenantId().eq(StringUtils.isNotEmpty(entity.getTenantId()), entity.getTenantId()); // 租户ID
            })
            .useLogicDelete(logicDelete)
            .allowDeleteStatement(!logicDelete)
            .executeRows();

    }

    @Override
    public Class<FlowTask> entityClass() {
        return FlowTask.class;
    }

    @Override
    public FlowTask newEntity() {
        return new FlowTask();
    }

    @Override
    public int delete(FlowTask entity) {
        TenantDeleteUtil.applyContextCondition(entity);

        final boolean logicDelete = FlowEngine.getFlowConfig().isLogicDelete();
        return (int) entityQuery().deletable(entityClass())
            .useLogicDelete(logicDelete)
            .allowDeleteStatement(!logicDelete)
            .where(proxy -> {
                buildDeleteEqCondition(entity, proxy);
            })
            .executeRows();

    }

    /** 参照 mybatis 实现， 构建删除条件 */
    private static void buildDeleteEqCondition(FlowTask entity, FlowTaskProxy proxy) {
        proxy.id().eq(Objects.nonNull(entity.getId()), entity.getId()); // 主键
        proxy.nodeCode().eq(StringUtils.isNotEmpty(entity.getNodeCode()), entity.getNodeCode()); // 节点编码
        proxy.nodeName().eq(StringUtils.isNotEmpty(entity.getNodeName()), entity.getNodeName()); // 节点名称
        proxy.nodeType().eq(Objects.nonNull(entity.getNodeType()), entity.getNodeType()); // 节点类型
        proxy.definitionId().eq(Objects.nonNull(entity.getDefinitionId()), entity.getDefinitionId()); // 对应flow_definition表的id
        proxy.instanceId().eq(Objects.nonNull(entity.getInstanceId()), entity.getInstanceId()); // 流程实例表id
        proxy.formCustom().eq(StringUtils.isNotEmpty(entity.getFormCustom()), entity.getFormCustom()); // 自定义表单
        proxy.formPath().eq(StringUtils.isNotEmpty(entity.getFormPath()), entity.getFormPath()); // 表单路径
        proxy.createTime().eq(Objects.nonNull(entity.getCreateTime()), entity.getCreateTime()); // 创建时间
        proxy.updateTime().eq(Objects.nonNull(entity.getUpdateTime()), entity.getUpdateTime()); // 更新时间
        proxy.tenantId().eq(StringUtils.isNotEmpty(entity.getTenantId()), entity.getTenantId()); // 租户ID
    }
}
