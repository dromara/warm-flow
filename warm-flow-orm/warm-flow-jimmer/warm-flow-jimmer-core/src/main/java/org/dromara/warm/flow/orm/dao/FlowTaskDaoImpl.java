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
import org.dromara.warm.flow.core.orm.dao.FlowTaskDao;
import org.dromara.warm.flow.core.utils.CollUtil;
import org.dromara.warm.flow.orm.entity.FlowTask;
import org.dromara.warm.flow.orm.model.FlowTaskModel;
import org.dromara.warm.flow.orm.model.FlowTaskModelTable;

import java.util.List;

public class FlowTaskDaoImpl extends WarmDaoImpl<FlowTask, FlowTaskModel> implements FlowTaskDao<FlowTask> {

    private static final FlowTaskModelTable TABLE = FlowTaskModelTable.$;

    public FlowTaskDaoImpl() {
        super(FlowTaskModel.class);
    }

    @Override
    public FlowTask newEntity() {
        return new FlowTask();
    }

    @Override
    protected TableProxy<FlowTaskModel> table() {
        return TABLE;
    }

    @Override
    protected List<Predicate> predicates(FlowTask entity, Table<FlowTaskModel> table) {
        List<Predicate> predicates = basePredicates(entity, table);
        eq(predicates, table, "definitionId", entity.getDefinitionId());
        eq(predicates, table, "instanceId", entity.getInstanceId());
        eqIfNotEmpty(predicates, table, "nodeCode", entity.getNodeCode());
        eqIfNotEmpty(predicates, table, "nodeName", entity.getNodeName());
        eq(predicates, table, "nodeType", entity.getNodeType());
        eqIfNotEmpty(predicates, table, "flowStatus", entity.getFlowStatus());
        eqIfNotEmpty(predicates, table, "formCustom", entity.getFormCustom());
        eqIfNotEmpty(predicates, table, "formPath", entity.getFormPath());
        return predicates;
    }

    @Override
    public int deleteByInsIds(List<Long> instanceIds) {
        if (CollUtil.isEmpty(instanceIds)) {
            return 0;
        }
        return deleteWhere(TABLE.instanceId().in(instanceIds));
    }

    @Override
    public List<FlowTask> getByInsIdAndNodeCodes(Long instanceId, List<String> nodeCodes) {
        return fromModels(sqlClient().createQuery(TABLE)
            .where(scopedPredicates(TABLE.instanceId().eq(instanceId)).toArray(new Predicate[0]))
            .where(TABLE.nodeCode().inIf(CollUtil.isNotEmpty(nodeCodes), nodeCodes))
            .select(TABLE)
            .execute());
    }
}
