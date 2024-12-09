package org.dromara.warm.flow.core.service;

import org.dromara.warm.flow.core.entity.Form;
import org.dromara.warm.flow.core.orm.service.IWarmService;
import org.dromara.warm.flow.core.utils.page.Page;

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

    /**
     *
     * @param id
     * @return
     */
    Form getById(Long id);

    /**
     * 已发布表单
     * @param formName
     * @param pageNum
     * @param pageSize
     * @return
     */
    Page<Form> publishedPage(String formName, Integer pageNum, Integer pageSize);

    /**
     * 保存表单内容
     * @param id
     * @param formContent
     */
    boolean saveContent(Long id, String formContent);
}
