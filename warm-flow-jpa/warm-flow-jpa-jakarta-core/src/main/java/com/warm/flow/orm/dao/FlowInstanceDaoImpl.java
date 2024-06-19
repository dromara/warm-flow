package com.warm.flow.orm.dao;

import com.warm.flow.core.dao.FlowInstanceDao;
import com.warm.flow.orm.entity.FlowInstance;

/**
 * 流程实例Mapper接口
 *
 * @author vanlin
 * @date 2024-05-12
 */
public class FlowInstanceDaoImpl extends WarmDaoImpl<FlowInstance> implements FlowInstanceDao<FlowInstance> {
    @Override
    public FlowInstance newEntity() {
        return new FlowInstance();
    }

    @Override
    public Class<FlowInstance> entityClass() {
        return FlowInstance.class;
    }
}
