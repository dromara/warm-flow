package com.warm.flow.core.service;

import com.warm.flow.core.entity.Definition;
import com.warm.flow.core.entity.Form;
import com.warm.flow.core.orm.service.IWarmService;

import java.util.List;

/**
 *  流程表单Service接口
 *
 * @author vanlin
 * @className FormService
 * @description
 * @since 2024/8/19 10:06
 */
public interface FormService extends IWarmService<Form> {

    /**
     * 保存流程表单
     *
     * @param form
     * @return
     */
    boolean save(Form form);

    /**
     * 发布流程表单
     *
     * @param id
     * @return
     */
    boolean publish(Long id);


    /**
     * 取消发布流程表单
     *
     * @param id
     * @return
     */
    boolean unPublish(Long id);

    /**
     * 复制流程表单
     *
     * @param id
     * @return
     */
    boolean copyForm(Long id);

    /**
     * 读取流程表单
     *
     * @param formCode
     * @param formVersion
     * @return
     */
    Form getByCode(String formCode, String formVersion);

    Form getById(Long id);

    List<Form> list(String name);
}