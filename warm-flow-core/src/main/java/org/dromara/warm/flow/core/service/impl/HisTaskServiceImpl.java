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
package org.dromara.warm.flow.core.service.impl;

import org.dromara.warm.flow.core.FlowEngine;
import org.dromara.warm.flow.core.dto.FlowParams;
import org.dromara.warm.flow.core.entity.HisTask;
import org.dromara.warm.flow.core.entity.Node;
import org.dromara.warm.flow.core.entity.Task;
import org.dromara.warm.flow.core.entity.User;
import org.dromara.warm.flow.core.enums.CooperateType;
import org.dromara.warm.flow.core.enums.FlowStatus;
import org.dromara.warm.flow.core.enums.SkipType;
import org.dromara.warm.flow.core.orm.dao.FlowHisTaskDao;
import org.dromara.warm.flow.core.orm.service.impl.WarmServiceImpl;
import org.dromara.warm.flow.core.service.HisTaskService;
import org.dromara.warm.flow.core.utils.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 历史任务记录Service业务层处理
 *
 * @author warm
 * @since 2023-03-29
 */
public class HisTaskServiceImpl extends WarmServiceImpl<FlowHisTaskDao<HisTask>, HisTask> implements HisTaskService {

    @Override
    public HisTaskService setDao(FlowHisTaskDao<HisTask> warmDao) {
        this.warmDao = warmDao;
        return this;
    }

    @Override
    public List<HisTask> listByTaskIdAndCooperateTypes(Long taskId, Integer... cooperateTypes) {
        if (ArrayUtil.isEmpty(cooperateTypes)) {
            return list(FlowEngine.newHisTask().setTaskId(taskId));
        }
        if (cooperateTypes.length == 1) {
            return list(FlowEngine.newHisTask().setTaskId(taskId).setCooperateType(cooperateTypes[0]));
        }
        return getDao().listByTaskIdAndCooperateTypes(taskId, cooperateTypes);
    }

    @Override
    public List<HisTask> getByInsAndNodeCodes(Long instanceId, List<String> nodeCodes) {
        return getDao().getByInsAndNodeCodes(instanceId, nodeCodes);
    }

    @Override
    public boolean deleteByInsIds(List<Long> instanceIds) {
        return SqlHelper.retBool(getDao().deleteByInsIds(instanceIds));
    }

    @Override
    public HisTask setSkipInsHis(Task task, List<Node> nextNodes, FlowParams flowParams) {
        String flowStatus = getFlowStatus(flowParams);
        return setSkipHis(task, nextNodes, flowParams, flowStatus);
    }

    @Override
    public List<HisTask> setSkipHisList(List<Task> taskList, List<Node> nextNodes, FlowParams flowParams) {
        String flowStatus = getFlowStatus(flowParams);
        List<HisTask> hisTasks = new ArrayList<>();
        for (Task task : taskList) {
            HisTask hisTask = setSkipHis(task, nextNodes, flowParams, flowStatus);
            hisTasks.add(hisTask);
        }
        return hisTasks;
    }

    @Override
    public HisTask setSkipHisTask(Task task, Node nextNode, FlowParams flowParams) {
        String flowStatus = getFlowStatus(flowParams);
        return setSkipHis(task, CollUtil.toList(nextNode), flowParams, flowStatus);
    }


    @Override
    public HisTask setCooperateHis(Task task, Node node, FlowParams flowParams
            , List<String> collaborators) {
        String flowStatus = getFlowStatus(flowParams);
        HisTask hisTask = FlowEngine.newHisTask()
                .setTaskId(task.getId())
                .setInstanceId(task.getInstanceId())
                .setCooperateType(ObjectUtil.isNotNull(flowParams.getCooperateType())
                        ? flowParams.getCooperateType() : CooperateType.APPROVAL.getKey())
                .setCollaborator(StreamUtils.join(collaborators, c -> c))
                .setNodeCode(task.getNodeCode())
                .setNodeName(task.getNodeName())
                .setNodeType(task.getNodeType())
                .setDefinitionId(task.getDefinitionId())
                .setTargetNodeCode(node.getNodeCode())
                .setTargetNodeName(node.getNodeName())
                .setApprover(flowParams.getHandler())
                .setSkipType(flowParams.getSkipType())
                .setFlowStatus(StringUtils.isNotEmpty(flowStatus)
                        ? flowStatus : FlowStatus.APPROVAL.getKey())
                .setFormCustom(task.getFormCustom())
                .setFormPath(task.getFormPath())
                .setMessage(flowParams.getMessage())
                .setVariable(flowParams.getVariableStr())
                //业务详情添加至历史记录
                .setExt(flowParams.getHisTaskExt())
                .setCreateTime(task.getCreateTime());
        FlowEngine.dataFillHandler().idFill(hisTask);
        return hisTask;
    }

    @Override
    public HisTask setDeputeHisTask(Task task, FlowParams flowParams, User entrustedUser) {
        String flowStatus = getFlowStatus(flowParams);
        HisTask hisTask = FlowEngine.newHisTask()
                .setTaskId(task.getId())
                .setInstanceId(task.getInstanceId())
                .setCooperateType(CooperateType.DEPUTE.getKey())
                .setNodeCode(task.getNodeCode())
                .setNodeName(task.getNodeName())
                .setNodeType(task.getNodeType())
                .setDefinitionId(task.getDefinitionId())
                .setTargetNodeCode(task.getNodeCode())
                .setTargetNodeName(task.getNodeName())
                .setApprover(entrustedUser.getProcessedBy())
                .setCollaborator(entrustedUser.getCreateBy())
                .setSkipType(flowParams.getSkipType())
                .setFlowStatus(StringUtils.isNotEmpty(flowStatus)
                        ? flowStatus : SkipType.isReject(flowParams.getSkipType())
                        ? FlowStatus.REJECT.getKey() : FlowStatus.PASS.getKey())
                .setFormCustom(task.getFormCustom())
                .setFormPath(task.getFormPath())
                .setMessage(flowParams.getMessage())
                .setVariable(flowParams.getVariableStr())
                //业务详情添加至历史记录
                .setExt(flowParams.getHisTaskExt())
                .setCreateTime(task.getCreateTime());
        FlowEngine.dataFillHandler().idFill(hisTask);
        return hisTask;
    }

    @Override
    public HisTask setSignHisTask(Task task, FlowParams flowParams, BigDecimal nodeRatio, boolean isPass) {
        String flowStatus = getFlowStatus(flowParams);
        HisTask hisTask = FlowEngine.newHisTask()
                .setTaskId(task.getId())
                .setInstanceId(task.getInstanceId())
                .setCooperateType(CooperateType.isCountersign(nodeRatio)
                        ? CooperateType.COUNTERSIGN.getKey() : CooperateType.VOTE.getKey())
                .setNodeCode(task.getNodeCode())
                .setNodeName(task.getNodeName())
                .setNodeType(task.getNodeType())
                .setDefinitionId(task.getDefinitionId())
                .setApprover(flowParams.getHandler())
                .setMessage(flowParams.getMessage())
                .setSkipType(isPass ? SkipType.PASS.getKey() : SkipType.REJECT.getKey())
                .setFlowStatus(StringUtils.isNotEmpty(flowStatus)
                        ? flowStatus : isPass
                        ? FlowStatus.PASS.getKey() : FlowStatus.REJECT.getKey())
                .setFormCustom(task.getFormCustom())
                .setFormPath(task.getFormPath())
                .setMessage(flowParams.getMessage())
                .setVariable(flowParams.getVariableStr())
                //业务详情添加至历史记录
                .setExt(flowParams.getHisTaskExt())
                .setCreateTime(task.getCreateTime());
        FlowEngine.dataFillHandler().idFill(hisTask);
        return hisTask;
    }

    @Override
    public List<HisTask> getByInsId(Long instanceId) {
        return FlowEngine.hisTaskService().list(FlowEngine.newHisTask().setInstanceId(instanceId));
    }

    @Override
    public List<HisTask> getNoReject(Long instanceId) {
        return getDao().getNoReject(instanceId);
    }

    @Override
    public HisTask getNoReject(String nodeCode, String targetNodeCode, List<HisTask> hisTasks) {
        List<HisTask> hisTaskList = StreamUtils.filter(hisTasks, hisTask ->
                (StringUtils.isEmpty(nodeCode) || nodeCode.equals(hisTask.getNodeCode()))
                        && (StringUtils.isEmpty(targetNodeCode) || targetNodeCode.equals(hisTask.getTargetNodeCode())));
        return CollUtil.getOne(hisTaskList);
    }

    private HisTask setSkipHis(Task task, List<Node> nextNodes, FlowParams flowParams, String flowStatus) {
        HisTask hisTask = FlowEngine.newHisTask()
                .setTaskId(task.getId())
                .setInstanceId(task.getInstanceId())
                .setCooperateType(ObjectUtil.isNotNull(flowParams.getCooperateType())
                        ? flowParams.getCooperateType() : CooperateType.APPROVAL.getKey())
                .setNodeCode(task.getNodeCode())
                .setNodeName(task.getNodeName())
                .setNodeType(task.getNodeType())
                .setDefinitionId(task.getDefinitionId())
                .setTargetNodeCode(StreamUtils.join(nextNodes, Node::getNodeCode))
                .setTargetNodeName(StreamUtils.join(nextNodes, Node::getNodeName))
                .setApprover(flowParams.getHandler())
                .setSkipType(flowParams.getSkipType())
                .setFlowStatus(StringUtils.isNotEmpty(flowStatus)
                        ? flowStatus : SkipType.isReject(flowParams.getSkipType())
                        ? FlowStatus.REJECT.getKey() : FlowStatus.PASS.getKey())
                .setFormCustom(task.getFormCustom())
                .setFormPath(task.getFormPath())
                .setMessage(flowParams.getMessage())
                .setVariable(flowParams.getVariableStr())
                //业务详情添加至历史记录
                .setExt(flowParams.getHisTaskExt())
                .setCreateTime(task.getCreateTime());
        FlowEngine.dataFillHandler().idFill(hisTask);
        return hisTask;
    }

    private String getFlowStatus(FlowParams flowParams) {
        return StringUtils.emptyDefault(flowParams.getHisStatus(), flowParams.getFlowStatus());
    }
}
