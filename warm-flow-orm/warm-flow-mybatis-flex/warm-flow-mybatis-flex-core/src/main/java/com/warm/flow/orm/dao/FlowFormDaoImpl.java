package com.warm.flow.orm.dao;

import com.warm.flow.core.dao.FlowFormDao;
import com.warm.flow.core.invoker.FrameInvoker;
import com.warm.flow.orm.entity.FlowForm;
import com.warm.flow.orm.mapper.FlowDefinitionMapper;
import com.warm.flow.orm.mapper.FlowFormMapper;
import com.warm.flow.orm.mapper.WarmMapper;

/**
 * @author vanlin
 * @className FlowFormDaoImpl
 * @description
 * @since 2024-9-23 16:17
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
