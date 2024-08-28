package com.warm.flow.core.service.impl;

import com.warm.flow.core.dao.FlowDefinitionDao;
import com.warm.flow.core.dao.FlowFormDao;
import com.warm.flow.core.entity.Definition;
import com.warm.flow.core.entity.Form;
import com.warm.flow.core.orm.service.IWarmService;
import com.warm.flow.core.orm.service.impl.WarmServiceImpl;
import com.warm.flow.core.service.FormService;

import java.util.Collections;
import java.util.List;

/**
 * 流程表单Service业务层处理
 *
 * @author vanlin
 * @className FormServiceImpl
 * @description
 * @since 2024/8/19 10:07
 */
public class FormServiceImpl extends WarmServiceImpl<FlowFormDao<Form>, Form> implements FormService {

    @Override
    protected FormService setDao(FlowFormDao<Form> warmDao) {
        this.warmDao = warmDao;
        return this;
    }

    @Override
    public boolean publish(Long id) {
        return false;
    }

    @Override
    public boolean unPublish(Long id) {
        return false;
    }

    @Override
    public boolean copyForm(Long id) {
        return false;
    }

    @Override
    public Form getByCode(String formCode, String formVersion) {
        return null;
    }

    @Override
    public Form getById(Long id) {
        return null;
    }

    @Override
    public List<Form> list(String name) {
        return Collections.emptyList();
    }
}
