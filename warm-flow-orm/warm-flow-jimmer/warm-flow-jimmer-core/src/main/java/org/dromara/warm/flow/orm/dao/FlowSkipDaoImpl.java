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
import org.dromara.warm.flow.core.orm.dao.FlowSkipDao;
import org.dromara.warm.flow.core.utils.CollUtil;
import org.dromara.warm.flow.orm.entity.FlowSkip;
import org.dromara.warm.flow.orm.model.FlowSkipModel;
import org.dromara.warm.flow.orm.model.FlowSkipModelTable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FlowSkipDaoImpl extends WarmDaoImpl<FlowSkip, FlowSkipModel> implements FlowSkipDao<FlowSkip> {

    private static final FlowSkipModelTable TABLE = FlowSkipModelTable.$;

    public FlowSkipDaoImpl() {
        super(FlowSkipModel.class);
    }

    @Override
    public FlowSkip newEntity() {
        return new FlowSkip();
    }

    @Override
    protected TableProxy<FlowSkipModel> table() {
        return TABLE;
    }

    @Override
    protected List<Predicate> predicates(FlowSkip entity, Table<FlowSkipModel> table) {
        List<Predicate> predicates = basePredicates(entity, table);
        eq(predicates, table, "definitionId", entity.getDefinitionId());
        eq(predicates, table, "nodeId", entity.getNodeId());
        eqIfNotEmpty(predicates, table, "nowNodeCode", entity.getNowNodeCode());
        eq(predicates, table, "nowNodeType", entity.getNowNodeType());
        eqIfNotEmpty(predicates, table, "nextNodeCode", entity.getNextNodeCode());
        eq(predicates, table, "nextNodeType", entity.getNextNodeType());
        eqIfNotEmpty(predicates, table, "skipName", entity.getSkipName());
        eqIfNotEmpty(predicates, table, "skipType", entity.getSkipType());
        eqIfNotEmpty(predicates, table, "skipCondition", entity.getSkipCondition());
        eqIfNotEmpty(predicates, table, "coordinate", entity.getCoordinate());
        return predicates;
    }

    @Override
    public int deleteSkipByDefIds(Collection<? extends Serializable> defIds) {
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
