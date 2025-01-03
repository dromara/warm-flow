package org.dromara.warm.flow.core.service.impl;

import org.dromara.warm.flow.core.FlowEngine;
import org.dromara.warm.flow.core.constant.ExceptionCons;
import org.dromara.warm.flow.core.entity.Definition;
import org.dromara.warm.flow.core.entity.Instance;
import org.dromara.warm.flow.core.entity.Node;
import org.dromara.warm.flow.core.orm.dao.FlowFormDao;
import org.dromara.warm.flow.core.entity.Form;
import org.dromara.warm.flow.core.enums.PublishStatus;
import org.dromara.warm.flow.core.orm.service.impl.WarmServiceImpl;
import org.dromara.warm.flow.core.service.FormService;
import org.dromara.warm.flow.core.utils.*;
import org.dromara.warm.flow.core.utils.page.Page;

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
        List<Node> nodes = FlowEngine.nodeService().list(FlowEngine.newNode().setFormPath("" + form.getId()));
        AssertUtil.isNotEmpty(nodes, ExceptionCons.EXIST_USE_FORM);
        List<Definition> definitions = FlowEngine.defService().list(FlowEngine.newDef().setFormPath("" + form.getId()));
        AssertUtil.isNotEmpty(definitions, ExceptionCons.EXIST_USE_FORM);
        AssertUtil.isTrue(form.getIsPublish().equals(PublishStatus.UNPUBLISHED.getKey()), ExceptionCons.FORM_ALREADY_UN_PUBLISH);
        form.setIsPublish(PublishStatus.UNPUBLISHED.getKey());
        return updateById(form);
    }

    @Override
    public boolean save(Form form) {
        form.setVersion(getNewVersion(form));
        return super.save(form);
    }

    @Override
    public boolean copyForm(Long id) {
        Form form = ClassUtil.clone(getById(id));
        AssertUtil.isTrue(ObjectUtil.isNull(form), ExceptionCons.NOT_FOUNT_DEF);
        FlowEngine.dataFillHandler().idFill(form.setId(null));
        form.setVersion(getNewVersion(form))
                .setIsPublish(PublishStatus.UNPUBLISHED.getKey())
                .setCreateTime(null)
                .setUpdateTime(null);
        return save(form);
    }

    @Override
    public Form getByCode(String formCode, String formVersion) {
        List<Form> list = list(FlowEngine.newForm().setFormCode(formCode).setVersion(formVersion));
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
    public Page<Form> publishedPage(String formName, Integer pageNum, Integer pageSize) {
        return page(FlowEngine.newForm().setFormName(formName).setIsPublish(1),
                Page.<Form>pageOf(pageNum, pageSize));
    }

    @Override
    public boolean saveContent(Long id, String formContent) {
        Form form = getById(id);
        AssertUtil.isTrue(form.getIsPublish().equals(PublishStatus.PUBLISHED.getKey()), ExceptionCons.FORM_ALREADY_PUBLISH);

        form.setFormContent(formContent);
        return updateById(form);
    }

    private String getNewVersion(Form form) {
        List<String> formCodeList = Collections.singletonList(form.getFormCode());
        List<Form> forms = getDao().queryByCodeList(formCodeList);
        int highestVersion = 0;
        String latestNonPositiveVersion = null;
        long latestTimestamp = Long.MIN_VALUE;

        for (Form otherForm : forms) {
            if (form.getFormCode().equals(otherForm.getFormCode())) {
                try {
                    int version = Integer.parseInt(otherForm.getVersion());
                    if (version > highestVersion) {
                        highestVersion = version;
                    }
                } catch (NumberFormatException e) {
                    long timestamp = otherForm.getCreateTime().getTime();
                    if (timestamp > latestTimestamp) {
                        latestTimestamp = timestamp;
                        latestNonPositiveVersion = otherForm.getVersion();
                    }
                }
            }
        }

        String version = "1";
        if (highestVersion > 0) {
            version = String.valueOf(highestVersion + 1);
        } else if (latestNonPositiveVersion != null) {
            version = latestNonPositiveVersion + "_1";
        }

        return version;
    }
}
