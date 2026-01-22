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

import com.easy.query.core.expression.lambda.SQLActionExpression1;
import org.dromara.warm.flow.core.orm.dao.FlowInstanceDao;
import org.dromara.warm.flow.core.utils.StringUtils;
import org.dromara.warm.flow.orm.entity.FlowInstance;
import org.dromara.warm.flow.orm.entity.proxy.FlowInstanceProxy;

import java.util.List;
import java.util.Objects;

/**
 * 流程实例Mapper接口
 *
 * @author link2fun
 */
public class FlowInstanceDaoImpl extends WarmDaoImpl<FlowInstance, FlowInstanceProxy> implements FlowInstanceDao<FlowInstance> {

    @Override
    public FlowInstance newEntity() {
        return new FlowInstance();
    }

    @Override
    public int delete(FlowInstance entity) {
        return (int) deletable()
            .useLogicDelete(isLogicDelete())
            .allowDeleteStatement(!isLogicDelete())
            .where(buildWhereCondition(entity))
            .executeRows();
    }

    @Override
    public List<FlowInstance> getByDefIds(List<Long> defIds) {
        return queryable()
            .useLogicDelete(isLogicDelete())
            .where(proxy -> proxy.definitionId().in(defIds)).toList();
    }

    /** 构建删除的过滤条件 使用 = 拼接， 参考 mybatis 实现 */
    @Override
    public SQLActionExpression1<FlowInstanceProxy> buildWhereCondition(FlowInstance entity) {
        return o -> {
            o.id().eq(Objects.nonNull(entity.getId()), entity.getId());
            o.businessId().eq(StringUtils.isNotEmpty(entity.getBusinessId()), entity.getBusinessId());
            o.definitionId().eq(Objects.nonNull(entity.getDefinitionId()), entity.getDefinitionId());
            o.nodeCode().eq(StringUtils.isNotEmpty(entity.getNodeCode()), entity.getNodeCode());
            o.nodeName().eq(StringUtils.isNotEmpty(entity.getNodeName()), entity.getNodeName());
            o.nodeType().eq(Objects.nonNull(entity.getNodeType()), entity.getNodeType());
            o.flowStatus().eq(Objects.nonNull(entity.getFlowStatus()), entity.getFlowStatus());
            o.variable().eq(StringUtils.isNotEmpty(entity.getVariable()), entity.getVariable());
            o.createTime().eq(Objects.nonNull(entity.getCreateTime()), entity.getCreateTime());
            o.updateTime().eq(Objects.nonNull(entity.getUpdateTime()), entity.getUpdateTime());
            o.ext().eq(StringUtils.isNotEmpty(entity.getExt()), entity.getExt());
            o.tenantId().eq(StringUtils.isNotEmpty(entity.getTenantId()), entity.getTenantId());
            o.createBy().eq(StringUtils.isNotEmpty(entity.getCreateBy()), entity.getCreateBy());
            o.updateBy().eq(StringUtils.isNotEmpty(entity.getUpdateBy()), entity.getUpdateBy());
        };
    }
}
