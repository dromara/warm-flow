package org.dromara.warm.flow.orm.dao;

import org.dromara.warm.flow.core.dao.FlowFormDao;
import org.dromara.warm.flow.core.invoker.FrameInvoker;
import org.dromara.warm.flow.orm.entity.FlowForm;
import org.dromara.warm.flow.orm.mapper.FlowFormMapper;
import org.dromara.warm.flow.orm.mapper.WarmMapper;

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
