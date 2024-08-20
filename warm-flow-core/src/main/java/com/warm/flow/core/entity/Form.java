package com.warm.flow.core.entity;

import java.util.Date;

/**
 * 流程表单 flow_form
 *
 * @author vanlin
 * @className Form
 * @description
 * @since 2024/8/19 9:59
 */
public interface Form extends RootEntity {
    public Long getId();

    public Form setId(Long id);

    public Date getCreateTime();

    public Form setCreateTime(Date createTime);

    public Date getUpdateTime();

    public Form setUpdateTime(Date updateTime);

    public String getTenantId();

    public Form setTenantId(String tenantId);

    public String getDelFlag();

    public Form setDelFlag(String delFlag);


    public String getFormCode();

    public Form setFormCode(String formCode);

    public String getFormName();

    public Form setFormName(String formName);

    public String getVersion();

    public Form setVersion(String version);

    /**
     * 是否发布（0未发布 1已发布 9失效）
     */
    public Integer getIsPublish();

    public Form setIsPublish(Integer isPublish);

    /**
     * 表单类型（0内置表单 存 form_content        1外挂表单 存form_path）
     */
    public Integer getFormType();

    public Form setFormType(Integer formType);

    public String getFormContent();

    public Form setFormContent(String formContent);

    public String getFormPath();

    public Form setFormPath(String formPath);

    public String getExt();

    public Form setExt(String ext);
}
