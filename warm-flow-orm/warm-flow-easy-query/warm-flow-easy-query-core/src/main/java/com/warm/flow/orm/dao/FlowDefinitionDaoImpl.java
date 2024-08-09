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
package com.warm.flow.orm.dao;

import com.warm.flow.core.FlowFactory;
import com.warm.flow.core.dao.FlowDefinitionDao;
import com.warm.flow.core.utils.StringUtils;
import com.warm.flow.orm.entity.FlowDefinition;
import com.warm.flow.orm.entity.proxy.FlowDefinitionProxy;
import com.warm.flow.orm.utils.TenantDeleteUtil;

import java.util.List;
import java.util.Objects;

/**
 * 流程定义Mapper接口
 *
 * @author link2fun
 */
public class FlowDefinitionDaoImpl extends WarmDaoImpl<FlowDefinition, FlowDefinitionProxy> implements FlowDefinitionDao<FlowDefinition> {

    @Override
    public Class<FlowDefinition> entityClass() {
        return FlowDefinition.class;
    }

    @Override
    public FlowDefinition newEntity() {
        return new FlowDefinition();
    }

    @Override
    public List<FlowDefinition> queryByCodeList(List<String> flowCodeList) {

        FlowDefinition entity = newEntity();
        TenantDeleteUtil.getEntity(entity);
        String tenantId = entity.getTenantId();

        return entityQuery().queryable(entityClass())
            .where(proxy -> {
                proxy.flowCode().in(flowCodeList); // 流程编码过滤
                proxy.tenantId().eq(StringUtils.isNotEmpty(tenantId), tenantId);  // 租户过滤
                proxy.delFlag().eq(StringUtils.isNotEmpty(entity.getDelFlag()), entity.getDelFlag()); // 删除标记过滤
            }).toList();
    }

    @Override
    public void closeFlowByCodeList(List<String> flowCodeList) {
        FlowDefinition entity = newEntity();
        TenantDeleteUtil.getEntity(entity);
        String tenantId = entity.getTenantId();

        entityQuery().updatable(entityClass())
            .where(flowDefinition->{
                flowDefinition.flowCode().in(flowCodeList); // 流程编码过滤
                flowDefinition.tenantId().eq(StringUtils.isNotEmpty(tenantId), tenantId);  // 租户过滤
                flowDefinition.delFlag().eq(StringUtils.isNotEmpty(entity.getDelFlag()), FlowFactory.getFlowConfig().getLogicNotDeleteValue()); // 删除标记过滤
            })
            .setColumns(proxy -> proxy.isPublish().set(9)) // 设置为失效
            .executeRows();
    }

    @Override
    public int delete(FlowDefinition entity) {

        TenantDeleteUtil.getEntity(entity);
        if (StringUtils.isNotEmpty(entity.getDelFlag())) {
            // 有逻辑删除
            entityQuery().updatable(entityClass())
                .where(proxy -> buildDeleteEqCondition(entity, proxy))
                // 设置逻辑删除 标识 为 warm-flow 配置的逻辑删除值
                .setColumns(f -> f.delFlag().set(FlowFactory.getFlowConfig().getLogicDeleteValue()))
                .executeRows();
        }

        return (int) entityQuery().deletable(entityClass())
            .where(proxy -> buildDeleteEqCondition(entity, proxy))
            .allowDeleteStatement(true)
            .executeRows();
    }


    /** 参考 mybatis orm 实现， 构建删除语句条件， 使用 = 拼接 */
    private static void buildDeleteEqCondition(FlowDefinition entity, FlowDefinitionProxy proxy) {
        proxy.id().eq(Objects.nonNull(entity.getId()), entity.getId()); // id
        proxy.flowCode().eq(StringUtils.isNotEmpty(entity.getFlowCode()), entity.getFlowCode()); // 流程编码
        proxy.flowName().eq(StringUtils.isNotEmpty(entity.getFlowName()), entity.getFlowName()); // 流程名称
        proxy.version().eq(StringUtils.isNotEmpty(entity.getVersion()), entity.getVersion()); // 流程版本
        proxy.isPublish().eq(Objects.nonNull(entity.getIsPublish()), entity.getIsPublish()); // 是否发布
        proxy.formCustom().eq(StringUtils.isNotEmpty(entity.getFormCustom()), entity.getFormCustom()); // 审批表单是否自定义
        proxy.formPath().eq(StringUtils.isNotEmpty(entity.getFormPath()), entity.getFormPath()); // 审批表单是否自定义
        proxy.tenantId().eq(StringUtils.isNotEmpty(entity.getTenantId()), entity.getTenantId()); // 租户ID
        proxy.createTime().eq(Objects.nonNull(entity.getCreateTime()), entity.getCreateTime()); // 创建时间
        proxy.updateTime().eq(Objects.nonNull(entity.getUpdateTime()), entity.getUpdateTime()); // 更新时间
        proxy.delFlag().eq(StringUtils.isNotEmpty(entity.getDelFlag()), entity.getDelFlag()); // 删除标记
    }
}
