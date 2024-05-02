package com.warm.flow.orm.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.warm.flow.core.dao.FlowTaskDao;
import com.warm.flow.core.invoker.FrameInvoker;
import com.warm.flow.orm.entity.FlowNode;
import com.warm.flow.orm.entity.FlowTask;
import com.warm.flow.orm.mapper.FlowTaskMapper;
import com.warm.flow.orm.utils.TenantDeleteUtil;

import java.util.List;

/**
 * 待办任务Mapper接口
 *
 * @author warm
 * @date 2023-03-29
 */
public class FlowTaskDaoImpl extends WarmDaoImpl<FlowTask> implements FlowTaskDao<FlowTask> {

    @Override
    public FlowTaskMapper getMapper() {
        return FrameInvoker.getBean(FlowTaskMapper.class);
    }

    @Override
    public FlowTask newEntity() {
        return new FlowTask();
    }

    /**
     * 根据instanceIds删除
     *
     * @param instanceIds 主键
     * @return 结果
     */
    @Override
    public int deleteByInsIds(List<Long> instanceIds) {
        return delete(newEntity(), (luw) -> luw.in(FlowTask::getInstanceId, instanceIds)
                , (lqw) -> lqw.in(FlowTask::getInstanceId, instanceIds));
    }
}
