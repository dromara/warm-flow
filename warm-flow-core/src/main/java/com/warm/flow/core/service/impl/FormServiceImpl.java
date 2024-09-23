package com.warm.flow.core.service.impl;

import com.warm.flow.core.FlowFactory;
import com.warm.flow.core.constant.ExceptionCons;
import com.warm.flow.core.dao.FlowFormDao;
import com.warm.flow.core.entity.Form;
import com.warm.flow.core.enums.PublishStatus;
import com.warm.flow.core.orm.service.impl.WarmServiceImpl;
import com.warm.flow.core.service.FormService;
import com.warm.flow.core.utils.AssertUtil;
import com.warm.flow.core.utils.ClassUtil;
import com.warm.flow.core.utils.CollUtil;
import com.warm.flow.core.utils.ObjectUtil;

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

    public FormService setDao(FlowFormDao<Form> warmDao) {
        this.warmDao = warmDao;
        return this;
    }

    @Override
    public boolean publish(Long id) {
        Form form = getById(id);
        AssertUtil.isTrue(form.getIsPublish().equals(PublishStatus.PUBLISHED.getKey()), ExceptionCons.FORM_ALREADY_PUBLISH);
        form.setIsPublish(PublishStatus.PUBLISHED.getKey());
        return updateById(form);
    }

    @Override
    public boolean unPublish(Long id) {
        Form form = getById(id);
        AssertUtil.isTrue(form.getIsPublish().equals(PublishStatus.UNPUBLISHED.getKey()), ExceptionCons.FORM_ALREADY_UN_PUBLISH);
        form.setIsPublish(PublishStatus.UNPUBLISHED.getKey());
        return updateById(form);
    }

    @Override
    public boolean copyForm(Long id) {
        Form form = ClassUtil.clone(getById(id));
        AssertUtil.isTrue(ObjectUtil.isNull(form), ExceptionCons.NOT_FOUNT_DEF);
        FlowFactory.dataFillHandler().idFill(form.setId(null));
        form.setVersion(form.getVersion() + "_copy")
                .setIsPublish(PublishStatus.UNPUBLISHED.getKey())
                .setCreateTime(null)
                .setUpdateTime(null);
        return save(form);
    }

    @Override
    public Form getByCode(String formCode, String formVersion) {
        List<Form> list = FlowFactory.formService().list(FlowFactory.newForm().setFormCode(formCode).setVersion(formVersion));
        AssertUtil.isTrue(CollUtil.isEmpty(list), ExceptionCons.NOT_FOUNT_TASK);
        AssertUtil.isTrue(list.size() > 1, ExceptionCons.FORM_NOT_ONE);
        return list.get(0);
    }

    @Override
    public Form getById(Long id) {
        AssertUtil.isNull(id, ExceptionCons.ID_EMPTY);
        return super.getById(id);
    }

    @Override
    public List<Form> list(String name) {
        List<Form> list = FlowFactory.formService().list(FlowFactory.newForm().setFormName(name));
        AssertUtil.isTrue(CollUtil.isEmpty(list), ExceptionCons.NOT_FOUNT_TASK);
        return list;
    }
}
