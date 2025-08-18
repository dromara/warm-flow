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
package org.dromara.warm.flow.core.service;

import org.dromara.warm.flow.core.entity.Form;
import org.dromara.warm.flow.core.orm.service.IWarmService;
import org.dromara.warm.flow.core.utils.page.Page;

/**
 * 流程表单Service接口
 *
 * @author vanlin
 * @since 2024/8/19 10:06
 */
public interface FormService extends IWarmService<Form> {

    /**
     * 保存流程表单
     *
     * @param form form
     * @return 保存情况
     */
    @Override
    boolean save(Form form);

    /**
     * 发布流程表单
     *
     * @param id id
     * @return 发布结果
     */
    boolean publish(Long id);


    /**
     * 取消发布流程表单
     *
     * @param id id
     * @return 取消发布
     */
    boolean unPublish(Long id);

    /**
     * 复制流程表单
     *
     * @param id id
     * @return 复制表单结果
     */
    boolean copyForm(Long id);

    /**
     * 读取流程表单
     *
     * @param formCode    表单编码
     * @param formVersion 版本
     * @return 表单信息
     */
    Form getByCode(String formCode, String formVersion);

    /**
     *
     * @param id id
     * @return 表单信息
     */
    Form getById(Long id);

    /**
     * 已发布表单
     *
     * @param formName 表单名
     * @param pageNum  页码
     * @param pageSize 每页记录
     * @return 已发布记录
     */
    Page<Form> publishedPage(String formName, Integer pageNum, Integer pageSize);

    /**
     * 保存表单内容
     *
     * @param id          id
     * @param formContent 表单内容
     * @return 保存结果
     */
    boolean saveContent(Long id, String formContent);
}
