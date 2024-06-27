package com.warm.flow.orm.dao;

import com.warm.flow.core.FlowFactory;
import com.warm.flow.core.dao.FlowHisTaskDao;
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
        return getMapper().getNoReject(nodeCode, targetNodeCode, instanceId, TenantDeleteUtil.getEntity(newEntity()));
    }

    @Override
    public int deleteByInsIds(List<Long> instanceIds) {
        FlowHisTask entity = TenantDeleteUtil.getEntity(newEntity());
        if (StringUtils.isNotEmpty(entity.getDelFlag())) {
            return getMapper().updateByInsIdsLogic(instanceIds, entity, FlowFactory.getFlowConfig().getLogicDeleteValue(), entity.getDelFlag());
        }
        return getMapper().deleteByInsIds(instanceIds, entity);
    }

    @Override
    public List<FlowHisTask> listByTaskIdAndCooperateTypes(Long taskId, Integer[] cooperateTypes) {
        return getMapper().listByTaskIdAndCooperateTypes(cooperateTypes, TenantDeleteUtil.getEntity(newEntity()).setTaskId(taskId));
    }

}
