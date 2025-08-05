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
package org.dromara.warm.flow.orm.entity;

import lombok.Data;
import lombok.experimental.Accessors;
import org.dromara.warm.flow.core.entity.Form;

import java.util.Date;


/**
 * @author vanlin
 * @since 2024/8/19 10:30
 */
@Data
@Accessors(chain = true)
public class FlowForm implements Form {

    /**
     * 主键
     */
    private Long id;

    /**
     * 表单编码
     */
    private String formCode;

    /**
     * 表单名称
     */
    private String formName;

    /**
     * 表单版本
     */
    private String version;

    /**
     * 是否发布（0未发布 1已发布 9失效）
     */
    private Integer isPublish;

    /**
     * 表单类型（0内置表单 存 form_content        1外挂表单 存form_path）
     */
    private Integer formType;

    /**
     * 表单路径
     */
    private String formPath;

    /**
     * 表单内容
     */
    private String formContent;

    /**
     * 表单扩展，用户自行使用
     */
    private String ext;
    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 删除标记
     */
    private String delFlag;

}
