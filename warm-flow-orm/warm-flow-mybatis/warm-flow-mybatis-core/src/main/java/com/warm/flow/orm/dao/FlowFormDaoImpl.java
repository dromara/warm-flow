package com.warm.flow.orm.dao;

import com.warm.flow.core.dao.FlowDefinitionDao;
import com.warm.flow.core.dao.FlowFormDao;
import com.warm.flow.core.invoker.FrameInvoker;
import com.warm.flow.orm.entity.FlowDefinition;
import com.warm.flow.orm.entity.FlowForm;
import com.warm.flow.orm.mapper.FlowFormMapper;
import com.warm.flow.orm.mapper.WarmMapper;

/**
 * @author vanlin
 * @className FlowFormDaoImpl
 * @description
 * @since 2024/8/19 14:29
 */
public class FlowFormDaoImpl extends WarmDaoImpl<FlowForm> implements FlowFormDao<FlowForm> {
    @Override
    public WarmMapper<FlowForm> getMapper() {
        return FrameInvoker.getBean(FlowFormMapper.class);
    }

    @Override
    public FlowForm newEntity() {
        return new FlowForm();
    }
}
