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

import com.easy.query.core.util.EasyArrayUtil;
import org.dromara.warm.flow.core.FlowEngine;
import org.dromara.warm.flow.core.orm.dao.FlowHisTaskDao;
import org.dromara.warm.flow.core.enums.SkipType;
import org.dromara.warm.flow.core.utils.StringUtils;
import org.dromara.warm.flow.orm.entity.FlowHisTask;
import org.dromara.warm.flow.orm.entity.proxy.FlowHisTaskProxy;
import org.dromara.warm.flow.orm.utils.TenantDeleteUtil;

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
        FlowHisTask entity = TenantDeleteUtil.applyContextCondition(newEntity());
        final boolean logicDelete = FlowEngine.getFlowConfig().isLogicDelete();
        return entityQuery().queryable(entityClass())
            .useLogicDelete(logicDelete)
            .where(proxy -> {
                proxy.instanceId().eq(instanceId); // 流程实例表id
                proxy.skipType().eq(SkipType.PASS.getKey()); // 跳转类型（PASS通过 REJECT退回 NONE无动作）
                proxy.tenantId().eq(StringUtils.isNotEmpty(entity.getTenantId()),entity.getTenantId()); // 租户ID
            })
            .orderBy(hisTask -> hisTask.createTime().desc()).toList();
    }

    @Override
    public List<FlowHisTask> getByInsAndNodeCodes(Long instanceId, List<String> nodeCodes) {
        FlowHisTask entity = TenantDeleteUtil.applyContextCondition(newEntity());
        final boolean logicDelete = FlowEngine.getFlowConfig().isLogicDelete();
        return entityQuery().queryable(entityClass())
            .useLogicDelete(logicDelete)
            .where(proxy -> {
                proxy.instanceId().eq(instanceId); // 流程实例表id
                proxy.nodeCode().in(nodeCodes); // 跳转类型（PASS通过 REJECT退回 NONE无动作）
                proxy.tenantId().eq(StringUtils.isNotEmpty(entity.getTenantId()),entity.getTenantId()); // 租户ID
            })
            .orderBy(hisTask -> hisTask.createTime().desc()).toList();
    }

    @Override
    public int deleteByInsIds(List<Long> instanceIds) {
        FlowHisTask entityFilter = TenantDeleteUtil.applyContextCondition(newEntity());

        final boolean logicDelete = FlowEngine.getFlowConfig().isLogicDelete();

        return (int) entityQuery().deletable(entityClass())
            .useLogicDelete(logicDelete)
            .allowDeleteStatement(!logicDelete)
            .where(proxy -> proxy.instanceId().in(instanceIds))
            .where(f -> {
                // 如果有租户， 则添加租户过滤
                f.tenantId().eq(StringUtils.isNotEmpty(entityFilter.getTenantId()),entityFilter.getTenantId());
            })
            .executeRows();
    }

    @Override
    public List<FlowHisTask> listByTaskIdAndCooperateTypes(Long taskId, Integer[] cooperateTypes) {


        FlowHisTask entity = newEntity();
        TenantDeleteUtil.applyContextCondition(entity);
        final boolean logicDelete = FlowEngine.getFlowConfig().isLogicDelete();


        return entityQuery().queryable(entityClass())
            .useLogicDelete(logicDelete)
            .where(proxy -> {
                proxy.taskId().eq(Objects.nonNull(proxy.taskId()), taskId); // 任务表id
                proxy.cooperateType().in(EasyArrayUtil.isNotEmpty(cooperateTypes), cooperateTypes); //协作方式(1审批 2转办 3委派 4会签 5票签 6加签 7减签)
                proxy.tenantId().eq(StringUtils.isNotEmpty(entity.getTenantId()), entity.getTenantId()); // 租户ID
            })
            .toList();
    }

    @Override
    public Class<FlowHisTask> entityClass() {
        return FlowHisTask.class;
    }

    @Override
    public FlowHisTask newEntity() {
        return new FlowHisTask();
    }

    @Override
    public int delete(FlowHisTask entity) {
        TenantDeleteUtil.applyContextCondition(entity);
        final boolean logicDelete = FlowEngine.getFlowConfig().isLogicDelete();

        // 没有启用逻辑删除， 执行物理删除
        return (int) entityQuery().deletable(entityClass())
            .useLogicDelete(logicDelete)
            .allowDeleteStatement(!logicDelete)
            .where(proxy -> buildDeleteEqCondition(entity, proxy))
            .executeRows();

    }

    /** 参考 mybatis 实现 构建删除语句条件， 使用 = 拼接 */
    private static void buildDeleteEqCondition(FlowHisTask entity, FlowHisTaskProxy proxy) {
        proxy.id().eq(Objects.nonNull(entity.getId()), entity.getId()); // 主键
        proxy.nodeCode().eq(StringUtils.isNotEmpty(entity.getNodeCode()), entity.getNodeCode()); // 开始节点编码
        proxy.nodeName().eq(StringUtils.isNotEmpty(entity.getNodeName()), entity.getNodeName()); // 开始节点名称
        proxy.nodeType().eq(Objects.nonNull(entity.getNodeType()), entity.getNodeType()); // 开始节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关）
        proxy.targetNodeCode().eq(StringUtils.isNotEmpty(entity.getTargetNodeCode()), entity.getTargetNodeCode()); // 目标节点编码
        proxy.targetNodeName().eq(StringUtils.isNotEmpty(entity.getTargetNodeName()), entity.getTargetNodeName()); // 结束节点名称
        proxy.collaborator().eq(StringUtils.isNotEmpty(entity.getCollaborator()), entity.getCollaborator()); // 协作人(只有转办、会签、票签、委派)
        proxy.definitionId().eq(Objects.nonNull(entity.getDefinitionId()), entity.getDefinitionId()); // 对应flow_definition表的id
        proxy.instanceId().eq(Objects.nonNull(entity.getInstanceId()), entity.getInstanceId()); // 流程实例表id
        proxy.taskId().eq(Objects.nonNull(entity.getTaskId()), entity.getTaskId()); // 任务表id
        proxy.cooperateType().eq(Objects.nonNull(entity.getCooperateType()), entity.getCooperateType()); // 协作方式(1审批 2转办 3委派 4会签 5票签 6加签 7减签)
        proxy.flowStatus().eq(Objects.nonNull(entity.getFlowStatus()), entity.getFlowStatus()); // 流程状态（1审批中 2 审批通过 9已退回 10失效）
        proxy.skipType().eq(StringUtils.isNotEmpty(entity.getSkipType()), entity.getSkipType()); // 跳转类型（PASS通过 REJECT退回 NONE无动作）
        proxy.message().eq(StringUtils.isNotEmpty(entity.getMessage()), entity.getMessage()); // 审批意见
        proxy.ext().eq(StringUtils.isNotEmpty(entity.getExt()), entity.getExt()); // 业务详情 存业务类的json
        proxy.createTime().eq(Objects.nonNull(entity.getCreateTime()), entity.getCreateTime()); // 创建时间
        proxy.updateTime().eq(Objects.nonNull(entity.getUpdateTime()), entity.getUpdateTime()); // 更新时间
        proxy.tenantId().eq(StringUtils.isNotEmpty(entity.getTenantId()), entity.getTenantId()); // 租户ID
    }
}
