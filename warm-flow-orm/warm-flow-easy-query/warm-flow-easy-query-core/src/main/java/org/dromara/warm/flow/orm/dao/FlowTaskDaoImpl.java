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
import org.dromara.warm.flow.core.orm.dao.FlowTaskDao;
import org.dromara.warm.flow.core.utils.StringUtils;
import org.dromara.warm.flow.orm.entity.FlowTask;
import org.dromara.warm.flow.orm.entity.proxy.FlowTaskProxy;

import java.util.List;
import java.util.Objects;

/**
 * 待办任务Mapper接口
 * @author link2fun
 */
public class FlowTaskDaoImpl extends WarmDaoImpl<FlowTask, FlowTaskProxy> implements FlowTaskDao<FlowTask> {

    @Override
    public FlowTask newEntity() {
        return new FlowTask();
    }

    @Override
    public int deleteByInsIds(List<Long> instanceIds) {
        return (int) deletable()
            .where(proxy -> {
                proxy.instanceId().in(instanceIds);
            })
            .useLogicDelete(isLogicDelete())
            .allowDeleteStatement(!isLogicDelete())
            .executeRows();

    }

    @Override
    public int delete(FlowTask entity) {
        return (int) deletable()
            .useLogicDelete(isLogicDelete())
            .allowDeleteStatement(!isLogicDelete())
            .where(buildWhereCondition(entity))
            .executeRows();

    }

    @Override
    public List<FlowTask> getByInsIdAndNodeCodes(Long instanceId, List<String> nodeCodes) {
        return queryable()
                .useLogicDelete(isLogicDelete())
                .where(proxy -> {
                    proxy.instanceId().eq(instanceId);
                    proxy.nodeCode().in(nodeCodes);
                }).toList();
    }

    /** 参照 mybatis 实现， 构建删除条件 */
    @Override
    public SQLActionExpression1<FlowTaskProxy> buildWhereCondition(FlowTask entity) {
        return o -> {
            o.id().eq(Objects.nonNull(entity.getId()), entity.getId());
            o.nodeCode().eq(StringUtils.isNotEmpty(entity.getNodeCode()), entity.getNodeCode());
            o.nodeName().eq(StringUtils.isNotEmpty(entity.getNodeName()), entity.getNodeName());
            o.nodeType().eq(Objects.nonNull(entity.getNodeType()), entity.getNodeType());
            o.definitionId().eq(Objects.nonNull(entity.getDefinitionId()), entity.getDefinitionId());
            o.instanceId().eq(Objects.nonNull(entity.getInstanceId()), entity.getInstanceId());
            o.formCustom().eq(StringUtils.isNotEmpty(entity.getFormCustom()), entity.getFormCustom());
            o.formPath().eq(StringUtils.isNotEmpty(entity.getFormPath()), entity.getFormPath());
            o.createTime().eq(Objects.nonNull(entity.getCreateTime()), entity.getCreateTime());
            o.updateTime().eq(Objects.nonNull(entity.getUpdateTime()), entity.getUpdateTime());
            o.tenantId().eq(StringUtils.isNotEmpty(entity.getTenantId()), entity.getTenantId());
            o.createBy().eq(StringUtils.isNotEmpty(entity.getCreateBy()), entity.getCreateBy());
            o.updateBy().eq(StringUtils.isNotEmpty(entity.getUpdateBy()), entity.getUpdateBy());
            o.flowStatus().eq(StringUtils.isNotEmpty(entity.getFlowStatus()), entity.getFlowStatus());
        };

    }
}
