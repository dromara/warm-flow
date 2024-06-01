package com.warm.flow.orm.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.warm.flow.core.dao.FlowHisTaskDao;
import com.warm.flow.core.enums.FlowStatus;
import com.warm.flow.core.invoker.FrameInvoker;
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

    /**
     * 根据nodeCode获取未退回的历史记录
     *
     * @param nodeCode
     * @param instanceId
     * @return
     */
    @Override
    public List<FlowHisTask> getNoReject(String nodeCode, Long instanceId) {
        LambdaQueryWrapper<FlowHisTask> queryWrapper = TenantDeleteUtil.getLambdaWrapperDefault(newEntity());
        queryWrapper.eq(FlowHisTask::getNodeCode, nodeCode)
                .eq(FlowHisTask::getInstanceId, instanceId)
                .ne(FlowHisTask::getFlowStatus, FlowStatus.REJECT.getKey())
                .orderByDesc(FlowHisTask::getCreateTime);
        return getMapper().selectList(queryWrapper);
    }

    /**
     * 根据instanceIds删除
     *
     * @param instanceIds 主键
     * @return 结果
     */
    @Override
    public int deleteByInsIds(List<Long> instanceIds) {
        return delete(newEntity(), (luw) -> luw.in(FlowHisTask::getInstanceId, instanceIds)
                , (lqw) -> lqw.in(FlowHisTask::getInstanceId, instanceIds));
    }

}
