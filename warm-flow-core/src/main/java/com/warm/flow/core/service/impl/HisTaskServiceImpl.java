package com.warm.flow.core.service.impl;

import com.warm.flow.core.FlowFactory;
import com.warm.flow.core.dao.FlowHisTaskDao;
import com.warm.flow.core.dto.FlowParams;
import com.warm.flow.core.entity.HisTask;
import com.warm.flow.core.entity.Node;
import com.warm.flow.core.entity.Task;
import com.warm.flow.core.enums.CooperateType;
import com.warm.flow.core.entity.User;
import com.warm.flow.core.enums.*;
import com.warm.flow.core.orm.service.impl.WarmServiceImpl;
import com.warm.flow.core.service.HisTaskService;
import com.warm.flow.core.utils.ObjectUtil;
import com.warm.flow.core.utils.SqlHelper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 历史任务记录Service业务层处理
 *
 * @author warm
 * @date 2023-03-29
 */
public class HisTaskServiceImpl extends WarmServiceImpl<FlowHisTaskDao<HisTask>, HisTask> implements HisTaskService {

    @Override
    public HisTaskService setDao(FlowHisTaskDao<HisTask> warmDao) {
        this.warmDao = warmDao;
        return this;
    }

    @Override
    public List<HisTask> setSkipInsHis(Task task, List<Node> nextNodes, FlowParams flowParams) {
        List<HisTask> hisTasks = new ArrayList<>();
        for (Node nextNode : nextNodes) {
            HisTask hisTask = FlowFactory.newHisTask();
            hisTask.setInstanceId(task.getInstanceId());
            hisTask.setTaskId(task.getId());
            if (ObjectUtil.isNotNull(flowParams.getCooperateType())) {
                hisTask.setCooperateType(flowParams.getCooperateType());
            } else {
                hisTask.setCooperateType(CooperateType.APPROVAL.getKey());
            }
            hisTask.setNodeCode(task.getNodeCode());
            hisTask.setNodeName(task.getNodeName());
            hisTask.setNodeType(task.getNodeType());
            hisTask.setDefinitionId(task.getDefinitionId());
            hisTask.setTargetNodeCode(nextNode.getNodeCode());
            hisTask.setTargetNodeName(nextNode.getNodeName());
            hisTask.setApprover(flowParams.getHandler());
            hisTask.setApprover(flowParams.getHandler());
            if (ObjectUtil.isNotNull(flowParams.getFlowStatus())) {
                hisTask.setFlowStatus(flowParams.getFlowStatus());
            } else if (ObjectUtil.isNotNull(nextNode.getNodeType()) && NodeType.isEnd(nextNode.getNodeType())) {
                hisTask.setFlowStatus(FlowStatus.FINISHED.getKey());
            } else {
                hisTask.setFlowStatus(SkipType.isReject(flowParams.getSkipType())
                        ? FlowStatus.REJECT.getKey() : FlowStatus.PASS.getKey());
            }
            hisTask.setMessage(flowParams.getMessage());
            hisTask.setCreateTime(new Date());
            FlowFactory.dataFillHandler().idFill(hisTask);
            hisTasks.add(hisTask);
        }
        return hisTasks;
    }

    @Override
    public HisTask setDeputeHisTask(Task task, FlowParams flowParams, User entrustedUser) {
        HisTask hisTask = FlowFactory.newHisTask()
                .setInstanceId(task.getInstanceId())
                .setTaskId(task.getId())
                .setCooperateType(CooperateType.DEPUTE.getKey())
                .setNodeCode(task.getNodeCode())
                .setNodeName(task.getNodeName())
                .setNodeType(task.getNodeType())
                .setDefinitionId(task.getDefinitionId())
                .setTargetNodeCode(task.getNodeCode())
                .setTargetNodeName(task.getNodeName())
                .setApprover(entrustedUser.getProcessedBy())
                .setCollaborator(entrustedUser.getCreateBy())
                .setFlowStatus(SkipType.isReject(flowParams.getSkipType())
                        ? FlowStatus.REJECT.getKey() : FlowStatus.PASS.getKey())
                .setMessage(flowParams.getMessage())
                .setCreateTime(new Date());
        FlowFactory.dataFillHandler().idFill(hisTask);
        return hisTask;
    }

    @Override
    public HisTask setSignHisTask(Task task, FlowParams flowParams, BigDecimal nodeRatio, boolean isPass) {
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
                .setFlowStatus(isPass ? FlowStatus.PASS.getKey() : FlowStatus.REJECT.getKey())
                .setCreateTime(new Date());
        FlowFactory.dataFillHandler().idFill(hisTask);
        return hisTask;
    }

    @Override
    public List<HisTask> autoHisTask(Integer flowStatus, Task task, List<User> userList, Integer cooperateType) {
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
                    .setFlowStatus(flowStatus)
                    .setCreateTime(new Date());
            FlowFactory.dataFillHandler().idFill(hisTask);
            hisTasks.add(hisTask);
        }

        return hisTasks;
    }

    @Override
    public List<HisTask> getNoReject(String nodeCode, Long instanceId) {
        return getDao().getNoReject(nodeCode, instanceId);
    }

    @Override
    public boolean deleteByInsIds(List<Long> instanceIds) {
        return SqlHelper.retBool(getDao().deleteByInsIds(instanceIds));
    }
}
