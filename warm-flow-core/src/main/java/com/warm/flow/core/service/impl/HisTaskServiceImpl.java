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
import com.warm.tools.utils.CollUtil;
import com.warm.tools.utils.ObjectUtil;
import com.warm.tools.utils.StreamUtils;
import com.warm.tools.utils.page.Page;

import java.util.Date;
import java.util.List;

/**
 * 历史任务记录Service业务层处理
 *
 * @author warm
 * @date 2023-03-29
 */
public class HisTaskServiceImpl extends WarmServiceImpl<FlowHisTaskDao, HisTask> implements HisTaskService {

    @Override
    public HisTaskService setDao(FlowHisTaskDao warmDao) {
        this.warmDao = warmDao;
        return this;
    }

    @Override
    public HisTask setSkipInsHis(Task task, List<Node> nextNodes, FlowParams flowParams) {
        HisTask insHis = FlowFactory.newHisTask();
        insHis.setId(task.getId());
        insHis.setInstanceId(task.getInstanceId());
        insHis.setNodeCode(task.getNodeCode());
        insHis.setNodeName(task.getNodeName());
        insHis.setNodeType(task.getNodeType());
        insHis.setPermissionFlag(task.getPermissionFlag());
        insHis.setTenantId(task.getTenantId());
        insHis.setDefinitionId(task.getDefinitionId());
        insHis.setTargetNodeCode(StreamUtils.join(nextNodes, Node::getNodeCode));
        insHis.setTargetNodeName(StreamUtils.join(nextNodes, Node::getNodeName));
        insHis.setFlowStatus(SkipType.isReject(flowParams.getSkipType())
                ? FlowStatus.REJECT.getKey() : FlowStatus.PASS.getKey());
        insHis.setMessage(flowParams.getMessage());
        insHis.setCreateTime(new Date());
        insHis.setApprover(flowParams.getCreateBy());

        return insHis;
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
