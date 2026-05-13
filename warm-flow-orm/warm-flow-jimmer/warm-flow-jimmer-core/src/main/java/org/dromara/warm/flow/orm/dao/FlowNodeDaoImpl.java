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

import org.babyfish.jimmer.sql.ast.Predicate;
import org.babyfish.jimmer.sql.ast.table.Table;
import org.babyfish.jimmer.sql.ast.table.spi.TableProxy;
import org.dromara.warm.flow.core.orm.dao.FlowNodeDao;
import org.dromara.warm.flow.core.utils.CollUtil;
import org.dromara.warm.flow.orm.entity.FlowNode;
import org.dromara.warm.flow.orm.model.FlowNodeModel;
import org.dromara.warm.flow.orm.model.FlowNodeModelTable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FlowNodeDaoImpl extends WarmDaoImpl<FlowNode, FlowNodeModel> implements FlowNodeDao<FlowNode> {

    private static final FlowNodeModelTable TABLE = FlowNodeModelTable.$;

    public FlowNodeDaoImpl() {
        super(FlowNodeModel.class);
    }

    @Override
    public FlowNode newEntity() {
        return new FlowNode();
    }

    @Override
    protected TableProxy<FlowNodeModel> table() {
        return TABLE;
    }

    @Override
    protected List<Predicate> predicates(FlowNode entity, Table<FlowNodeModel> table) {
        List<Predicate> predicates = basePredicates(entity, table);
        eq(predicates, table, "nodeType", entity.getNodeType());
        eq(predicates, table, "definitionId", entity.getDefinitionId());
        eqIfNotEmpty(predicates, table, "nodeCode", entity.getNodeCode());
        eqIfNotEmpty(predicates, table, "nodeName", entity.getNodeName());
        eqIfNotEmpty(predicates, table, "nodeRatio", entity.getNodeRatio());
        eqIfNotEmpty(predicates, table, "permissionFlag", entity.getPermissionFlag());
        eqIfNotEmpty(predicates, table, "coordinate", entity.getCoordinate());
        eqIfNotEmpty(predicates, table, "anyNodeSkip", entity.getAnyNodeSkip());
        eqIfNotEmpty(predicates, table, "listenerType", entity.getListenerType());
        eqIfNotEmpty(predicates, table, "listenerPath", entity.getListenerPath());
        eqIfNotEmpty(predicates, table, "formCustom", entity.getFormCustom());
        eqIfNotEmpty(predicates, table, "formPath", entity.getFormPath());
        eqIfNotEmpty(predicates, table, "ext", entity.getExt());
        eqIfNotEmpty(predicates, table, "version", entity.getVersion());
        return predicates;
    }

    @Override
    public List<FlowNode> getByNodeCodes(List<String> nodeCodes, Long definitionId) {
        return fromModels(sqlClient().createQuery(TABLE)
            .where(scopedPredicates(TABLE.definitionId().eq(definitionId)).toArray(new Predicate[0]))
            .where(TABLE.nodeCode().inIf(CollUtil.isNotEmpty(nodeCodes), nodeCodes))
            .select(TABLE)
            .execute());
    }

    @Override
    public int deleteNodeByDefIds(Collection<? extends Serializable> defIds) {
        if (CollUtil.isEmpty(defIds)) {
            return 0;
        }
        List<Long> ids = new ArrayList<>();
        for (Serializable defId : defIds) {
            ids.add(Long.valueOf(String.valueOf(defId)));
        }
        return deleteWhere(TABLE.definitionId().in(ids));
    }
}
