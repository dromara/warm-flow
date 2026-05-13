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
import org.dromara.warm.flow.core.enums.SkipType;
import org.dromara.warm.flow.core.orm.dao.FlowHisTaskDao;
import org.dromara.warm.flow.core.utils.ArrayUtil;
import org.dromara.warm.flow.core.utils.CollUtil;
import org.dromara.warm.flow.orm.entity.FlowHisTask;
import org.dromara.warm.flow.orm.model.FlowHisTaskModel;
import org.dromara.warm.flow.orm.model.FlowHisTaskModelTable;

import java.util.Arrays;
import java.util.List;

public class FlowHisTaskDaoImpl extends WarmDaoImpl<FlowHisTask, FlowHisTaskModel> implements FlowHisTaskDao<FlowHisTask> {

    private static final FlowHisTaskModelTable TABLE = FlowHisTaskModelTable.$;

    public FlowHisTaskDaoImpl() {
        super(FlowHisTaskModel.class);
    }

    @Override
    public FlowHisTask newEntity() {
        return new FlowHisTask();
    }

    @Override
    protected TableProxy<FlowHisTaskModel> table() {
        return TABLE;
    }

    @Override
    protected List<Predicate> predicates(FlowHisTask entity, Table<FlowHisTaskModel> table) {
        List<Predicate> predicates = basePredicates(entity, table);
        eq(predicates, table, "definitionId", entity.getDefinitionId());
        eq(predicates, table, "instanceId", entity.getInstanceId());
        eq(predicates, table, "taskId", entity.getTaskId());
        eq(predicates, table, "cooperateType", entity.getCooperateType());
        eqIfNotEmpty(predicates, table, "nodeCode", entity.getNodeCode());
        eqIfNotEmpty(predicates, table, "nodeName", entity.getNodeName());
        eq(predicates, table, "nodeType", entity.getNodeType());
        eqIfNotEmpty(predicates, table, "targetNodeCode", entity.getTargetNodeCode());
        eqIfNotEmpty(predicates, table, "targetNodeName", entity.getTargetNodeName());
        eqIfNotEmpty(predicates, table, "approver", entity.getApprover());
        eqIfNotEmpty(predicates, table, "collaborator", entity.getCollaborator());
        eqIfNotEmpty(predicates, table, "skipType", entity.getSkipType());
        eqIfNotEmpty(predicates, table, "flowStatus", entity.getFlowStatus());
        eqIfNotEmpty(predicates, table, "message", entity.getMessage());
        eqIfNotEmpty(predicates, table, "variable", entity.getVariable());
        eqIfNotEmpty(predicates, table, "ext", entity.getExt());
        eqIfNotEmpty(predicates, table, "formCustom", entity.getFormCustom());
        eqIfNotEmpty(predicates, table, "formPath", entity.getFormPath());
        return predicates;
    }

    @Override
    public List<FlowHisTask> getNoReject(Long instanceId) {
        return fromModels(sqlClient().createQuery(TABLE)
            .where(scopedPredicates(TABLE.instanceId().eq(instanceId)).toArray(new Predicate[0]))
            .where(TABLE.skipType().eq(SkipType.PASS.getKey()))
            .orderBy(TABLE.createTime().desc())
            .select(TABLE)
            .execute());
    }

    @Override
    public List<FlowHisTask> getByInsAndNodeCodes(Long instanceId, List<String> nodeCodes) {
        return fromModels(sqlClient().createQuery(TABLE)
            .where(scopedPredicates(TABLE.instanceId().eq(instanceId)).toArray(new Predicate[0]))
            .where(TABLE.nodeCode().inIf(CollUtil.isNotEmpty(nodeCodes), nodeCodes))
            .orderBy(TABLE.createTime().desc())
            .select(TABLE)
            .execute());
    }

    @Override
    public int deleteByInsIds(List<Long> instanceIds) {
        if (CollUtil.isEmpty(instanceIds)) {
            return 0;
        }
        return deleteWhere(TABLE.instanceId().in(instanceIds));
    }

    @Override
    public List<FlowHisTask> listByTaskIdAndCooperateTypes(Long taskId, Integer[] cooperateTypes) {
        return fromModels(sqlClient().createQuery(TABLE)
            .where(scopedPredicates(TABLE.taskId().eq(taskId)).toArray(new Predicate[0]))
            .where(TABLE.cooperateType().inIf(ArrayUtil.isNotEmpty(cooperateTypes), Arrays.asList(cooperateTypes)))
            .select(TABLE)
            .execute());
    }
}
