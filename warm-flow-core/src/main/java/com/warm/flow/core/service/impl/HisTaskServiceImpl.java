package com.warm.flow.core.service.impl;

import com.warm.flow.core.FlowFactory;
import com.warm.flow.core.dao.FlowHisTaskDao;
import com.warm.flow.core.dto.FlowParams;
import com.warm.flow.core.entity.HisTask;
import com.warm.flow.core.entity.Node;
import com.warm.flow.core.entity.Task;
import com.warm.flow.core.enums.FlowStatus;
import com.warm.flow.core.enums.SkipType;
import com.warm.flow.core.orm.service.impl.WarmServiceImpl;
import com.warm.flow.core.service.HisTaskService;
import com.warm.flow.core.utils.SqlHelper;

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
            HisTask insHis = FlowFactory.newHisTask();
            insHis.setInstanceId(task.getInstanceId());
            insHis.setNodeCode(task.getNodeCode());
            insHis.setNodeName(task.getNodeName());
            insHis.setNodeType(task.getNodeType());
            insHis.setPermissionFlag(task.getPermissionFlag());
            insHis.setTenantId(task.getTenantId());
            insHis.setDefinitionId(task.getDefinitionId());
            insHis.setTargetNodeCode(nextNode.getNodeCode());
            insHis.setTargetNodeName(nextNode.getNodeName());
            insHis.setFlowStatus(SkipType.isReject(flowParams.getSkipType())
                    ? FlowStatus.REJECT.getKey() : FlowStatus.PASS.getKey());
            insHis.setMessage(flowParams.getMessage());
            insHis.setCreateTime(new Date());
            FlowFactory.dataFillHandler().idFill(insHis);
            hisTasks.add(insHis);
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
