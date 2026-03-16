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
import org.dromara.warm.flow.core.orm.dao.FlowDefinitionDao;
import org.dromara.warm.flow.core.utils.StringUtils;
import org.dromara.warm.flow.orm.entity.FlowDefinition;
import org.dromara.warm.flow.orm.entity.proxy.FlowDefinitionProxy;

import java.util.List;
import java.util.Objects;

/**
 * 流程定义Mapper接口
 *
 * @author link2fun
 */
public class FlowDefinitionDaoImpl extends WarmDaoImpl<FlowDefinition, FlowDefinitionProxy> implements FlowDefinitionDao<FlowDefinition> {

    @Override
    public FlowDefinition newEntity() {
        return new FlowDefinition();
    }

    @Override
    public List<FlowDefinition> queryByCodeList(List<String> flowCodeList) {
        return queryable()
                .useInterceptor()
                .where(proxy -> proxy.flowCode().in(flowCodeList)).toList();
    }

    @Override
    public void updatePublishStatus(List<Long> ids, Integer publishStatus) {
        updatable()
                .where(flowDefinition-> flowDefinition.id().in(ids))
                .setColumns(proxy -> proxy.isPublish().set(publishStatus))
                .executeRows();
    }

    @Override
    public int delete(FlowDefinition entity) {
        return (int) deletable()
            .where(buildWhereCondition(entity))
            .executeRows();
    }


    /** 参考 mybatis orm 实现， 构建删除语句条件， 使用 = 拼接 */
    @Override
    public SQLActionExpression1<FlowDefinitionProxy> buildWhereCondition(FlowDefinition entity) {
        return o -> {
            o.id().eq(Objects.nonNull(entity.getId()), entity.getId());
            o.flowCode().eq(StringUtils.isNotEmpty(entity.getFlowCode()), entity.getFlowCode());
            o.flowName().eq(StringUtils.isNotEmpty(entity.getFlowName()), entity.getFlowName());
            o.version().eq(StringUtils.isNotEmpty(entity.getVersion()), entity.getVersion());
            o.isPublish().eq(Objects.nonNull(entity.getIsPublish()), entity.getIsPublish());
            o.formCustom().eq(StringUtils.isNotEmpty(entity.getFormCustom()), entity.getFormCustom());
            o.formPath().eq(StringUtils.isNotEmpty(entity.getFormPath()), entity.getFormPath());
            o.tenantId().eq(StringUtils.isNotEmpty(entity.getTenantId()), entity.getTenantId());
            o.createTime().eq(Objects.nonNull(entity.getCreateTime()), entity.getCreateTime());
            o.updateTime().eq(Objects.nonNull(entity.getUpdateTime()), entity.getUpdateTime());
            o.createBy().eq(StringUtils.isNotEmpty(entity.getCreateBy()), entity.getCreateBy());
            o.updateBy().eq(StringUtils.isNotEmpty(entity.getUpdateBy()), entity.getUpdateBy());
            o.modelValue().eq(StringUtils.isNotEmpty(entity.getModelValue()), entity.getModelValue());
        };
    }
}
