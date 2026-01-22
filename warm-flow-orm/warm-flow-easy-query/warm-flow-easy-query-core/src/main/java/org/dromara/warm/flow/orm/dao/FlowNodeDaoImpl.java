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
import com.easy.query.core.util.EasyCollectionUtil;
import org.dromara.warm.flow.core.orm.dao.FlowNodeDao;
import org.dromara.warm.flow.core.utils.StringUtils;
import org.dromara.warm.flow.orm.entity.FlowNode;
import org.dromara.warm.flow.orm.entity.proxy.FlowNodeProxy;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * 流程节点Mapper接口
 * @author link2fun
 */
public class FlowNodeDaoImpl extends WarmDaoImpl<FlowNode, FlowNodeProxy> implements FlowNodeDao<FlowNode> {

    @Override
    public FlowNode newEntity() {
        return new FlowNode();
    }

    @Override
    public List<FlowNode> getByNodeCodes(List<String> nodeCodes, Long definitionId) {
        return queryable()
            .useLogicDelete(isLogicDelete())
            .where(proxy -> {
                proxy.definitionId().eq(definitionId);
                proxy.nodeCode().in(EasyCollectionUtil.isNotEmpty(nodeCodes), nodeCodes);
            })
            .toList();
    }

    @Override
    public int deleteNodeByDefIds(Collection<? extends Serializable> defIds) {
        // 没有使用逻辑删除， 直接物理删除
       return (int) deletable()
           .useLogicDelete(isLogicDelete())
           .allowDeleteStatement(!isLogicDelete())
            .where(proxy -> {
                //noinspection unchecked
                proxy.definitionId().in((Collection<? extends Long>) defIds);
            })
            .executeRows();
    }

    @Override
    public int delete(FlowNode entity) {
        return (int) deletable()
            .useLogicDelete(isLogicDelete())
            .allowDeleteStatement(!isLogicDelete())
            .where(buildWhereCondition(entity))
            .executeRows();
    }

    /** 参考 mybatis 的实现 构建删除时的条件 */
    @Override
    public SQLActionExpression1<FlowNodeProxy> buildWhereCondition(FlowNode entity) {
        return o -> {
                o.id().eq(Objects.nonNull(entity.getId()), entity.getId());
            o.nodeType().eq(Objects.nonNull(entity.getNodeType()), entity.getNodeType());
            o.definitionId().eq(Objects.nonNull(entity.getDefinitionId()), entity.getDefinitionId());
            o.nodeCode().eq(StringUtils.isNotEmpty(entity.getNodeCode()), entity.getNodeCode());
            o.nodeName().eq(StringUtils.isNotEmpty(entity.getNodeName()), entity.getNodeName());
            o.permissionFlag().eq(StringUtils.isNotEmpty(entity.getPermissionFlag()), entity.getPermissionFlag());
            o.nodeRatio().eq(StringUtils.isNotEmpty(entity.getNodeRatio()), entity.getNodeRatio());
            o.coordinate().eq(StringUtils.isNotEmpty(entity.getCoordinate()), entity.getCoordinate());
            o.listenerType().eq(StringUtils.isNotEmpty(entity.getListenerType()), entity.getListenerType());
            o.listenerPath().eq(StringUtils.isNotEmpty(entity.getListenerPath()), entity.getListenerPath());
            o.formCustom().eq(StringUtils.isNotEmpty(entity.getFormCustom()), entity.getFormCustom());
            o.formPath().eq(StringUtils.isNotEmpty(entity.getFormPath()), entity.getFormPath());
            o.version().eq(StringUtils.isNotEmpty(entity.getVersion()), entity.getVersion());
            o.createTime().eq(Objects.nonNull(entity.getCreateTime()), entity.getCreateTime());
            o.updateTime().eq(Objects.nonNull(entity.getUpdateTime()), entity.getUpdateTime());
            o.tenantId().eq(StringUtils.isNotEmpty(entity.getTenantId()), entity.getTenantId());
            o.createBy().eq(StringUtils.isNotEmpty(entity.getCreateBy()), entity.getCreateBy());
            o.updateBy().eq(StringUtils.isNotEmpty(entity.getUpdateBy()), entity.getUpdateBy());
            o.ext().eq(StringUtils.isNotEmpty(entity.getExt()), entity.getExt());
        };
    }
}
