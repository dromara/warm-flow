package com.warm.flow.orm.dao;

import com.mybatisflex.core.query.QueryWrapper;
import com.warm.flow.core.dao.FlowHisTaskDao;
import com.warm.flow.core.enums.FlowStatus;
import com.warm.flow.core.invoker.FrameInvoker;
import com.warm.flow.core.utils.StringUtils;
import com.warm.flow.orm.entity.FlowHisTask;
import com.warm.flow.orm.mapper.FlowHisTaskMapper;
import com.warm.flow.orm.utils.TenantDeleteUtil;

import java.util.List;

/**
 * 历史任务记录Mapper接口
 *
 * @author warm
 * @date 2023-03-29
 */
public class FlowHisTaskDaoImpl extends WarmDaoImpl<FlowHisTask> implements FlowHisTaskDao<FlowHisTask> {

    @Override
    public FlowHisTaskMapper getMapper() {
        return FrameInvoker.getBean(FlowHisTaskMapper.class);
    }

    @Override
    public FlowHisTask newEntity() {
        return new FlowHisTask();
    }

    @Override
    public List<FlowHisTask> getNoReject(String nodeCode, String targetNodeCode, Long instanceId) {
        QueryWrapper queryWrapper = TenantDeleteUtil.getDefaultWrapper(newEntity());
        queryWrapper.eq(FlowHisTask::getNodeCode, nodeCode)
                .eq(FlowHisTask::getTargetNodeCode, targetNodeCode, StringUtils.isNotEmpty(targetNodeCode))
                .eq(FlowHisTask::getInstanceId, instanceId)
                .eq(FlowHisTask::getFlowStatus, FlowStatus.PASS.getKey())
                .orderBy(FlowHisTask::getCreateTime, false);
        return getMapper().selectListByQuery(queryWrapper);
    }

    @Override
    public int deleteByInsIds(List<Long> instanceIds) {
        return delete(newEntity(), (uq) -> uq.in(FlowHisTask::getInstanceId, instanceIds)
                , (qw) -> qw.in(FlowHisTask::getInstanceId, instanceIds));
    }

    @Override
    public List<FlowHisTask> listByTaskIdAndCooperateTypes(Long taskId, Integer[] cooperateTypes) {
        QueryWrapper queryWrapper = TenantDeleteUtil.getDefaultWrapper(newEntity());
        queryWrapper.eq(FlowHisTask::getTaskId, taskId).in(FlowHisTask::getCooperateType, (Object) cooperateTypes);
        return getMapper().selectListByQuery(queryWrapper);
    }

}