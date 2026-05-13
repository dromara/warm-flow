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
import org.dromara.warm.flow.core.orm.dao.FlowInstanceDao;
import org.dromara.warm.flow.core.utils.CollUtil;
import org.dromara.warm.flow.orm.entity.FlowInstance;
import org.dromara.warm.flow.orm.model.FlowInstanceModel;
import org.dromara.warm.flow.orm.model.FlowInstanceModelTable;

import java.util.ArrayList;
import java.util.List;

public class FlowInstanceDaoImpl extends WarmDaoImpl<FlowInstance, FlowInstanceModel> implements FlowInstanceDao<FlowInstance> {

    private static final FlowInstanceModelTable TABLE = FlowInstanceModelTable.$;

    public FlowInstanceDaoImpl() {
        super(FlowInstanceModel.class);
    }

    @Override
    public FlowInstance newEntity() {
        return new FlowInstance();
    }

    @Override
    protected TableProxy<FlowInstanceModel> table() {
        return TABLE;
    }

    @Override
    protected List<Predicate> predicates(FlowInstance entity, Table<FlowInstanceModel> table) {
        List<Predicate> predicates = basePredicates(entity, table);
        eq(predicates, table, "definitionId", entity.getDefinitionId());
        eqIfNotEmpty(predicates, table, "businessId", entity.getBusinessId());
        eq(predicates, table, "nodeType", entity.getNodeType());
        eqIfNotEmpty(predicates, table, "nodeCode", entity.getNodeCode());
        eqIfNotEmpty(predicates, table, "nodeName", entity.getNodeName());
        eqIfNotEmpty(predicates, table, "variable", entity.getVariable());
        eqIfNotEmpty(predicates, table, "flowStatus", entity.getFlowStatus());
        eq(predicates, table, "activityStatus", entity.getActivityStatus());
        eqIfNotEmpty(predicates, table, "formCustom", entity.getFormCustom());
        eqIfNotEmpty(predicates, table, "formPath", entity.getFormPath());
        eqIfNotEmpty(predicates, table, "defJson", entity.getDefJson());
        eqIfNotEmpty(predicates, table, "ext", entity.getExt());
        return predicates;
    }

    @Override
    public List<FlowInstance> getByDefIds(List<Long> defIds) {
        if (CollUtil.isEmpty(defIds)) {
            return new ArrayList<>();
        }
        return fromModels(sqlClient().createQuery(TABLE)
            .where(scopedPredicates(TABLE.definitionId().in(defIds)).toArray(new Predicate[0]))
            .select(TABLE)
            .execute());
    }
}
