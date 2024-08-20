package com.warm.flow.orm.dao;

import com.warm.flow.core.dao.FlowDefinitionDao;
import com.warm.flow.core.dao.FlowFormDao;
import com.warm.flow.core.entity.Form;
import com.warm.flow.orm.entity.FlowDefinition;
import com.warm.flow.orm.entity.FlowForm;

/**
 * @author vanlin
 * @className FlowFormDaoImpl
 * @description
 * @since 2024/8/19 10:29
 */
public class FlowFormDaoImpl extends WarmDaoImpl<FlowForm> implements FlowFormDao<FlowForm> {

    @Override
    public Class<FlowForm> entityClass() {
        return FlowForm.class;
    }

    @Override
    public FlowForm newEntity() {
        return new FlowForm();
    }
}
