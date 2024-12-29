package org.dromara.warm.flow.orm.dao;

import org.dromara.warm.flow.core.orm.dao.FlowFormDao;
import org.dromara.warm.flow.core.invoker.FrameInvoker;
import org.dromara.warm.flow.orm.entity.FlowForm;
import org.dromara.warm.flow.orm.mapper.FlowFormMapper;
import org.dromara.warm.flow.orm.mapper.WarmMapper;

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
