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
import org.dromara.warm.flow.core.orm.dao.FlowSkipDao;
import org.dromara.warm.flow.core.utils.StringUtils;
import org.dromara.warm.flow.orm.entity.FlowSkip;
import org.dromara.warm.flow.orm.entity.proxy.FlowSkipProxy;

import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;

/**
 * 节点跳转关联Mapper接口
 * @author link2fun
 */
public class FlowSkipDaoImpl extends WarmDaoImpl<FlowSkip, FlowSkipProxy> implements FlowSkipDao<FlowSkip> {

    @Override
    public FlowSkip newEntity() {
        return new FlowSkip();
    }

    @Override
    public int deleteSkipByDefIds(Collection<? extends Serializable> defIds) {

        return (int) deletable()
            .where(proxy -> proxy.definitionId().in((Collection<? extends Long>) defIds))
            .useLogicDelete(isLogicDelete())
            .allowDeleteStatement(!isLogicDelete())
            .executeRows();
    }

    @Override
    public int delete(FlowSkip entity) {
        return (int) deletable()
            .where(buildWhereCondition(entity))
            .useLogicDelete(isLogicDelete())
            .allowDeleteStatement(!isLogicDelete())
            .executeRows();
    }


    /** 参照 mybatis 实现 构建删除时的条件 */
    @Override
    public SQLActionExpression1<FlowSkipProxy> buildWhereCondition(FlowSkip entity) {
        return o -> {
            o.id().eq(Objects.nonNull(entity.getId()), entity.getId());
            o.definitionId().eq(Objects.nonNull(entity.getDefinitionId()), entity.getDefinitionId());
            o.nowNodeCode().eq(StringUtils.isNotEmpty(entity.getNowNodeCode()), entity.getNowNodeCode());
            o.nowNodeType().eq(Objects.nonNull(entity.getNowNodeType()), entity.getNowNodeType());
            o.nextNodeCode().eq(StringUtils.isNotEmpty(entity.getNextNodeCode()), entity.getNextNodeCode());
            o.nextNodeType().eq(Objects.nonNull(entity.getNextNodeType()), entity.getNextNodeType());
            o.skipName().eq(StringUtils.isNotEmpty(entity.getSkipName()), entity.getSkipName());
            o.skipType().eq(StringUtils.isNotEmpty(entity.getSkipType()), entity.getSkipType());
            o.coordinate().eq(StringUtils.isNotEmpty(entity.getCoordinate()), entity.getCoordinate());
            o.skipCondition().eq(StringUtils.isNotEmpty(entity.getSkipCondition()), entity.getSkipCondition());
            o.createTime().eq(Objects.nonNull(entity.getCreateTime()), entity.getCreateTime());
            o.updateTime().eq(Objects.nonNull(entity.getUpdateTime()), entity.getUpdateTime());
            o.tenantId().eq(StringUtils.isNotEmpty(entity.getTenantId()), entity.getTenantId());
        };

    }
}
