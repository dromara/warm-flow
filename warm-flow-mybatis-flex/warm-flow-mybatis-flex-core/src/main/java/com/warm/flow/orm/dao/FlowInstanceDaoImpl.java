package com.warm.flow.orm.dao;

import com.warm.flow.core.dao.FlowInstanceDao;
import com.warm.flow.core.invoker.FrameInvoker;
import com.warm.flow.orm.entity.FlowInstance;
import com.warm.flow.orm.mapper.FlowInstanceMapper;

/**
 * 流程实例Mapper接口
 *
 * @author warm
 * @date 2023-03-29
 */
public class FlowInstanceDaoImpl extends WarmDaoImpl<FlowInstance> implements FlowInstanceDao<FlowInstance> {

    @Override
    public FlowInstanceMapper getMapper() {
        return FrameInvoker.getBean(FlowInstanceMapper.class);
    }

    @Override
    public FlowInstance newEntity() {
        return new FlowInstance();
    }

}
