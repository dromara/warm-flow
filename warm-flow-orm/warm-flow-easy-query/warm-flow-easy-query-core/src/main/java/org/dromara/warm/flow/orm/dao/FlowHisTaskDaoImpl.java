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
import com.easy.query.core.util.EasyArrayUtil;
import org.dromara.warm.flow.core.enums.SkipType;
import org.dromara.warm.flow.core.orm.dao.FlowHisTaskDao;
import org.dromara.warm.flow.core.utils.CollUtil;
import org.dromara.warm.flow.core.utils.StringUtils;
import org.dromara.warm.flow.orm.entity.FlowHisTask;
import org.dromara.warm.flow.orm.entity.proxy.FlowHisTaskProxy;

import java.util.List;
import java.util.Objects;

/**
 * 历史任务记录Mapper接口
 * @author link2fun
 */
public class FlowHisTaskDaoImpl extends WarmDaoImpl<FlowHisTask, FlowHisTaskProxy> implements FlowHisTaskDao<FlowHisTask> {

    /** 根据instanceId获取未退回的历史记录 */
    @Override
    public List<FlowHisTask> getNoReject(Long instanceId) {
        return queryable()
            .where(proxy -> {
                proxy.instanceId().eq(instanceId);
                proxy.skipType().eq(SkipType.PASS.getKey());
            })
            .orderBy(hisTask -> hisTask.createTime().desc()).toList();
    }

    @Override
    public List<FlowHisTask> getByInsAndNodeCodes(Long instanceId, List<String> nodeCodes) {
        return queryable()
            .where(proxy -> {
                proxy.instanceId().eq(instanceId);
                proxy.nodeCode().in(CollUtil.isNotEmpty(nodeCodes), nodeCodes);
            })
            .orderBy(hisTask -> hisTask.createTime().desc()).toList();
    }

    @Override
    public int deleteByInsIds(List<Long> instanceIds) {
        return (int) deletable()
            .where(proxy -> proxy.instanceId().in(instanceIds))
            .executeRows();
    }

    @Override
    public List<FlowHisTask> listByTaskIdAndCooperateTypes(Long taskId, Integer[] cooperateTypes) {
        return queryable()
            .where(proxy -> {
                proxy.taskId().eq(Objects.nonNull(proxy.taskId()), taskId);
                proxy.cooperateType().in(EasyArrayUtil.isNotEmpty(cooperateTypes), cooperateTypes);
            })
            .toList();
    }

    @Override
    public FlowHisTask newEntity() {
        return new FlowHisTask();
    }

    @Override
    public int delete(FlowHisTask entity) {
        // 没有启用逻辑删除， 执行物理删除
        return (int) deletable()
            .where(buildWhereCondition(entity))
            .executeRows();

    }

    /** 参考 mybatis 实现 构建删除语句条件， 使用 = 拼接 */
    @Override
    public SQLActionExpression1<FlowHisTaskProxy> buildWhereCondition(FlowHisTask entity) {
        return o -> {
            o.id().eq(Objects.nonNull(entity.getId()), entity.getId());
            o.nodeCode().eq(StringUtils.isNotEmpty(entity.getNodeCode()), entity.getNodeCode());
            o.nodeName().eq(StringUtils.isNotEmpty(entity.getNodeName()), entity.getNodeName());
            o.nodeType().eq(Objects.nonNull(entity.getNodeType()), entity.getNodeType());
            o.targetNodeCode().eq(StringUtils.isNotEmpty(entity.getTargetNodeCode()), entity.getTargetNodeCode());
            o.targetNodeName().eq(StringUtils.isNotEmpty(entity.getTargetNodeName()), entity.getTargetNodeName());
            o.collaborator().eq(StringUtils.isNotEmpty(entity.getCollaborator()), entity.getCollaborator());
            o.definitionId().eq(Objects.nonNull(entity.getDefinitionId()), entity.getDefinitionId());
            o.instanceId().eq(Objects.nonNull(entity.getInstanceId()), entity.getInstanceId());
            o.taskId().eq(Objects.nonNull(entity.getTaskId()), entity.getTaskId());
            o.cooperateType().eq(Objects.nonNull(entity.getCooperateType()), entity.getCooperateType());
            o.flowStatus().eq(Objects.nonNull(entity.getFlowStatus()), entity.getFlowStatus());
            o.skipType().eq(StringUtils.isNotEmpty(entity.getSkipType()), entity.getSkipType());
            o.message().eq(StringUtils.isNotEmpty(entity.getMessage()), entity.getMessage());
            o.ext().eq(StringUtils.isNotEmpty(entity.getExt()), entity.getExt());
            o.createTime().eq(Objects.nonNull(entity.getCreateTime()), entity.getCreateTime());
            o.updateTime().eq(Objects.nonNull(entity.getUpdateTime()), entity.getUpdateTime());
            o.tenantId().eq(StringUtils.isNotEmpty(entity.getTenantId()), entity.getTenantId());
        };
    }
}
