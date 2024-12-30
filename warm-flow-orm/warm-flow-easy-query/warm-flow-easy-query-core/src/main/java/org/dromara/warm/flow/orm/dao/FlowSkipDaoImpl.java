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
import org.dromara.warm.flow.core.orm.dao.FlowSkipDao;
import org.dromara.warm.flow.core.utils.StringUtils;
import org.dromara.warm.flow.orm.entity.FlowSkip;
import org.dromara.warm.flow.orm.entity.proxy.FlowSkipProxy;
import org.dromara.warm.flow.orm.utils.TenantDeleteUtil;

import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;

/**
 * 节点跳转关联Mapper接口
 * @author link2fun
 */
public class FlowSkipDaoImpl extends WarmDaoImpl<FlowSkip, FlowSkipProxy> implements FlowSkipDao<FlowSkip> {
    @Override
    public int deleteSkipByDefIds(Collection<? extends Serializable> defIds) {
        FlowSkip entity = newEntity();
        TenantDeleteUtil.applyContextCondition(entity);
        final boolean logicDelete = FlowEngine.getFlowConfig().isLogicDelete();

        return (int) entityQuery().deletable(entityClass())
            .where(proxy -> {
                //noinspection unchecked
                proxy.definitionId().in((Collection<? extends Long>) defIds);
                proxy.tenantId().eq(StringUtils.isNotEmpty(entity.getTenantId()), entity.getTenantId());
            })
            .useLogicDelete(logicDelete)
            .allowDeleteStatement(!logicDelete)
            .executeRows();
    }

    @Override
    public Class<FlowSkip> entityClass() {
        return FlowSkip.class;
    }

    @Override
    public FlowSkip newEntity() {
        return new FlowSkip();
    }

    @Override
    public int delete(FlowSkip entity) {
        TenantDeleteUtil.applyContextCondition(entity);
        final boolean logicDelete = FlowEngine.getFlowConfig().isLogicDelete();

        return (int) entityQuery().deletable(entityClass())
            .where(proxy -> {
                buildDeleteEqCondition(entity, proxy);
            })
            .useLogicDelete(logicDelete)
            .allowDeleteStatement(!logicDelete)
            .executeRows();
    }


    /** 参照 mybatis 实现 构建删除时的条件 */
    private static void buildDeleteEqCondition(FlowSkip entity, FlowSkipProxy proxy) {
        proxy.id().eq(Objects.nonNull(entity.getId()), entity.getId());// 主键
        proxy.definitionId().eq(Objects.nonNull(entity.getDefinitionId()), entity.getDefinitionId());// 流程id
        proxy.nowNodeCode().eq(StringUtils.isNotEmpty(entity.getNowNodeCode()), entity.getNowNodeCode());// 当前流程节点的编码
        proxy.nowNodeType().eq(Objects.nonNull(entity.getNowNodeType()), entity.getNowNodeType());// 当前节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关）
        proxy.nextNodeCode().eq(StringUtils.isNotEmpty(entity.getNextNodeCode()), entity.getNextNodeCode());// 下一个流程节点的编码
        proxy.nextNodeType().eq(Objects.nonNull(entity.getNextNodeType()), entity.getNextNodeType());// 下一个节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关）
        proxy.skipName().eq(StringUtils.isNotEmpty(entity.getSkipName()), entity.getSkipName());// 跳转名称
        proxy.skipType().eq(StringUtils.isNotEmpty(entity.getSkipType()), entity.getSkipType());// 跳转类型（PASS审批通过 REJECT退回）
        proxy.coordinate().eq(StringUtils.isNotEmpty(entity.getCoordinate()), entity.getCoordinate());// 流程跳转坐标
        proxy.skipCondition().eq(StringUtils.isNotEmpty(entity.getSkipCondition()), entity.getSkipCondition());// 跳转条件
        proxy.createTime().eq(Objects.nonNull(entity.getCreateTime()), entity.getCreateTime());// 创建时间
        proxy.updateTime().eq(Objects.nonNull(entity.getUpdateTime()), entity.getUpdateTime());// 更新时间
        proxy.tenantId().eq(StringUtils.isNotEmpty(entity.getTenantId()), entity.getTenantId());// 租户ID
    }
}
