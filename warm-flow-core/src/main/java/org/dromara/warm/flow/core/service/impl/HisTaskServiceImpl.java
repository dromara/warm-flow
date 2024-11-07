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

import org.dromara.warm.flow.core.FlowFactory;
import org.dromara.warm.flow.core.dao.FlowHisTaskDao;
import org.dromara.warm.flow.core.dto.FlowParams;
import org.dromara.warm.flow.core.entity.HisTask;
import org.dromara.warm.flow.core.entity.Node;
import org.dromara.warm.flow.core.entity.Task;
import org.dromara.warm.flow.core.entity.User;
import org.dromara.warm.flow.core.enums.CooperateType;
import org.dromara.warm.flow.core.enums.FlowStatus;
import org.dromara.warm.flow.core.enums.NodeType;
import org.dromara.warm.flow.core.enums.SkipType;
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
            return list(FlowFactory.newHisTask().setTaskId(taskId));
        }
        if (cooperateTypes.length == 1) {
            return list(FlowFactory.newHisTask().setTaskId(taskId).setCooperateType(cooperateTypes[0]));
        }
        return getDao().listByTaskIdAndCooperateTypes(taskId, cooperateTypes);
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

    @Override
    public List<HisTask> getByInsAndNodeCodes(Long instanceId, List<String> nodeCodes) {
        return getDao().getByInsAndNodeCodes(instanceId, nodeCodes);
    }

    @Override
    public boolean deleteByInsIds(List<Long> instanceIds) {
        return SqlHelper.retBool(getDao().deleteByInsIds(instanceIds));
    }

    @Override
    public List<HisTask> setSkipInsHis(Task task, List<Node> nextNodes, FlowParams flowParams) {
        List<HisTask> hisTasks = new ArrayList<>();
        String flowStatus = getFlowStatus(flowParams);
        for (Node nextNode : nextNodes) {
            HisTask hisTask = FlowFactory.newHisTask()
                    .setTaskId(task.getId())
                    .setInstanceId(task.getInstanceId())
                    .setCooperateType(ObjectUtil.isNotNull(flowParams.getCooperateType())
                            ? flowParams.getCooperateType() : CooperateType.APPROVAL.getKey())
                    .setNodeCode(task.getNodeCode())
                    .setNodeName(task.getNodeName())
                    .setNodeType(task.getNodeType())
                    .setDefinitionId(task.getDefinitionId())
                    .setTargetNodeCode(nextNode.getNodeCode())
                    .setTargetNodeName(nextNode.getNodeName())
                    .setApprover(flowParams.getHandler())
                    .setSkipType(NodeType.isStart(task.getNodeType()) ? SkipType.PASS.getKey() : flowParams.getSkipType())
                    .setFlowStatus(StringUtils.isNotEmpty(flowStatus)
                            ? flowStatus : SkipType.isReject(flowParams.getSkipType())
                            ? FlowStatus.REJECT.getKey() : FlowStatus.PASS.getKey())
                    .setFormCustom(task.getFormCustom())
                    .setFormPath(task.getFormPath())
                    .setMessage(flowParams.getMessage())
                    //业务详情添加至历史记录
                    .setExt(flowParams.getHisTaskExt())
                    .setCreateTime(task.getCreateTime());
            FlowFactory.dataFillHandler().idFill(hisTask);
            hisTasks.add(hisTask);
        }
        return hisTasks;
    }

    @Override
    public List<HisTask> setCooperateHis(Task task, Node node, FlowParams flowParams
            , List<String> collaborators) {
        List<HisTask> hisTasks = new ArrayList<>();
        String flowStatus = getFlowStatus(flowParams);
        for (String collaborator : collaborators) {
            HisTask hisTask = FlowFactory.newHisTask()
                    .setTaskId(task.getId())
                    .setInstanceId(task.getInstanceId())
                    .setCooperateType(ObjectUtil.isNotNull(flowParams.getCooperateType())
                            ? flowParams.getCooperateType() : CooperateType.APPROVAL.getKey())
                    .setCollaborator(collaborator)
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
                    //业务详情添加至历史记录
                    .setExt(flowParams.getHisTaskExt())
                    .setCreateTime(task.getCreateTime());
            FlowFactory.dataFillHandler().idFill(hisTask);
            hisTasks.add(hisTask);
        }
        return hisTasks;
    }

    @Override
    public HisTask setDeputeHisTask(Task task, FlowParams flowParams, User entrustedUser) {
        String flowStatus = getFlowStatus(flowParams);
        HisTask hisTask = FlowFactory.newHisTask()
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
                //业务详情添加至历史记录
                .setExt(flowParams.getHisTaskExt())
                .setCreateTime(task.getCreateTime());
        FlowFactory.dataFillHandler().idFill(hisTask);
        return hisTask;
    }

    @Override
    public HisTask setSignHisTask(Task task, FlowParams flowParams, BigDecimal nodeRatio, boolean isPass) {
        String flowStatus = getFlowStatus(flowParams);
        HisTask hisTask = FlowFactory.newHisTask()
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
                //业务详情添加至历史记录
                .setExt(flowParams.getHisTaskExt())
                .setCreateTime(task.getCreateTime());
        FlowFactory.dataFillHandler().idFill(hisTask);
        return hisTask;
    }

    @Override
    public List<HisTask> autoHisTask(FlowParams flowParams, String flowStatus, Task task, List<User> userList, Integer cooperateType) {
        List<HisTask> hisTasks = new ArrayList<>();
        for (User user : userList) {
            HisTask hisTask = FlowFactory.newHisTask()
                    .setTaskId(task.getId())
                    .setInstanceId(task.getInstanceId())
                    .setCooperateType(cooperateType)
                    .setNodeCode(task.getNodeCode())
                    .setNodeName(task.getNodeName())
                    .setNodeType(task.getNodeType())
                    .setDefinitionId(task.getDefinitionId())
                    .setApprover(user.getProcessedBy())
                    .setSkipType(flowParams.getSkipType())
                    .setFlowStatus(StringUtils.isNotEmpty(flowParams.getHisStatus())
                            ? flowParams.getHisStatus() : flowStatus)
                    .setFormCustom(task.getFormCustom())
                    .setFormPath(task.getFormPath())
                    .setMessage(flowParams.getMessage())
                    .setCreateTime(task.getCreateTime());
            FlowFactory.dataFillHandler().idFill(hisTask);
            hisTasks.add(hisTask);
        }

        return hisTasks;
    }

    private static String getFlowStatus(FlowParams flowParams) {
        return StringUtils.isNotEmpty(flowParams.getHisStatus())
                ? flowParams.getHisStatus() : flowParams.getFlowStatus();
    }
}
