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
import org.dromara.warm.flow.core.orm.dao.FlowUserDao;
import org.dromara.warm.flow.core.utils.ArrayUtil;
import org.dromara.warm.flow.core.utils.CollUtil;
import org.dromara.warm.flow.orm.entity.FlowUser;
import org.dromara.warm.flow.orm.model.FlowUserModel;
import org.dromara.warm.flow.orm.model.FlowUserModelTable;

import java.util.Arrays;
import java.util.List;

public class FlowUserDaoImpl extends WarmDaoImpl<FlowUser, FlowUserModel> implements FlowUserDao<FlowUser> {

    private static final FlowUserModelTable TABLE = FlowUserModelTable.$;

    public FlowUserDaoImpl() {
        super(FlowUserModel.class);
    }

    @Override
    public FlowUser newEntity() {
        return new FlowUser();
    }

    @Override
    protected TableProxy<FlowUserModel> table() {
        return TABLE;
    }

    @Override
    protected List<Predicate> predicates(FlowUser entity, Table<FlowUserModel> table) {
        List<Predicate> predicates = basePredicates(entity, table);
        eqIfNotEmpty(predicates, table, "type", entity.getType());
        eqIfNotEmpty(predicates, table, "processedBy", entity.getProcessedBy());
        eq(predicates, table, "associated", entity.getAssociated());
        return predicates;
    }

    @Override
    public int deleteByTaskIds(List<Long> taskIdList) {
        if (CollUtil.isEmpty(taskIdList)) {
            return 0;
        }
        return deleteWhere(TABLE.associated().in(taskIdList));
    }

    @Override
    public List<FlowUser> listByAssociatedAndTypes(List<Long> associatedList, String[] types) {
        return fromModels(sqlClient().createQuery(TABLE)
            .where(scopedPredicates().toArray(new Predicate[0]))
            .where(TABLE.associated().inIf(CollUtil.isNotEmpty(associatedList), associatedList),
                TABLE.type().inIf(ArrayUtil.isNotEmpty(types), Arrays.asList(types)))
            .select(TABLE)
            .execute());
    }

    @Override
    public List<FlowUser> listByProcessedBys(Long associated, List<String> processedBys, String[] types) {
        return fromModels(sqlClient().createQuery(TABLE)
            .where(scopedPredicates().toArray(new Predicate[0]))
            .where(TABLE.associated().eqIf(associated),
                TABLE.processedBy().inIf(CollUtil.isNotEmpty(processedBys), processedBys),
                TABLE.type().inIf(ArrayUtil.isNotEmpty(types), Arrays.asList(types)))
            .select(TABLE)
            .execute());
    }
}
