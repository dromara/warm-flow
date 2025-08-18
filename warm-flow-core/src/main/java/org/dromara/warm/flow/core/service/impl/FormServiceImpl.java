/*
 *    Copyright 2024-2025, Warm-Flow (290631660@qq.com).
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.dromara.warm.flow.core.service.impl;

import org.dromara.warm.flow.core.FlowEngine;
import org.dromara.warm.flow.core.constant.ExceptionCons;
import org.dromara.warm.flow.core.entity.Definition;
import org.dromara.warm.flow.core.entity.Form;
import org.dromara.warm.flow.core.entity.Node;
import org.dromara.warm.flow.core.enums.PublishStatus;
import org.dromara.warm.flow.core.orm.dao.FlowFormDao;
import org.dromara.warm.flow.core.orm.service.impl.WarmServiceImpl;
import org.dromara.warm.flow.core.service.FormService;
import org.dromara.warm.flow.core.utils.AssertUtil;
import org.dromara.warm.flow.core.utils.ClassUtil;
import org.dromara.warm.flow.core.utils.CollUtil;
import org.dromara.warm.flow.core.utils.ObjectUtil;
import org.dromara.warm.flow.core.utils.page.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

/**
 * 流程表单Service业务层处理
 *
 * @author vanlin
 * @since 2024/8/19 10:07
 */
public class FormServiceImpl extends WarmServiceImpl<FlowFormDao<Form>, Form> implements FormService {

    public static final Logger LOGGER = LoggerFactory.getLogger(FormServiceImpl.class);

    @Override
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

        for (Form otherForm : forms) {
            if (form.getFormCode().equals(otherForm.getFormCode())) {
                try {
                    int version = Integer.parseInt(otherForm.getVersion());
                    if (version > highestVersion) {
                        highestVersion = version;
                    }
                } catch (NumberFormatException e) {
                    LOGGER.error("版本格式化异常 - {}", e.getLocalizedMessage());
                }
            }
        }

        String version = "1";
        if (highestVersion > 0) {
            version = String.valueOf(highestVersion + 1);
        }

        return version;
    }
}
