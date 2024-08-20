package com.warm.flow.orm.entity;

import com.warm.flow.core.entity.Form;
import java.util.Date;

/**
 * @author vanlin
 * @className FlowForm
 * @description
 * @since 2024/8/19 10:30
 */
public class FlowForm implements Form {

    /**
     * 主键
     */
    private Long id;

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

    private String formCode;

    private String formName;

    private String version;

    private Integer isPublish;

    private Integer formType;

    private String formPath;

    private String formContent;

    private String ext;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public FlowForm setId(Long id) {
        this.id = id;
        return this;
    }

    @Override
    public Date getCreateTime() {
        return createTime;
    }

    @Override
    public FlowForm setCreateTime(Date createTime) {
        this.createTime = createTime;
        return this;
    }

    @Override
    public Date getUpdateTime() {
        return updateTime;
    }

    @Override
    public FlowForm setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
        return this;
    }

    @Override
    public String getTenantId() {
        return tenantId;
    }

    @Override
    public FlowForm setTenantId(String tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    @Override
    public String getDelFlag() {
        return delFlag;
    }

    @Override
    public FlowForm setDelFlag(String delFlag) {
        this.delFlag = delFlag;
        return this;
    }

    @Override
    public String getFormCode() {
        return formCode;
    }

    @Override
    public FlowForm setFormCode(String formCode) {
        this.formCode = formCode;
        return this;
    }

    @Override
    public String getFormName() {
        return formName;
    }

    @Override
    public FlowForm setFormName(String formName) {
        this.formName = formName;
        return this;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public FlowForm setVersion(String version) {
        this.version = version;
        return this;
    }

    @Override
    public Integer getIsPublish() {
        return isPublish;
    }

    @Override
    public FlowForm setIsPublish(Integer isPublish) {
        this.isPublish = isPublish;
        return this;
    }

    @Override
    public Integer getFormType() {
        return formType;
    }

    @Override
    public FlowForm setFormType(Integer formType) {
        this.formType = formType;
        return this;
    }

    @Override
    public String getFormPath() {
        return formPath;
    }

    @Override
    public FlowForm setFormPath(String formPath) {
        this.formPath = formPath;
        return this;
    }

    @Override
    public String getFormContent() {
        return formContent;
    }

    @Override
    public FlowForm setFormContent(String formContent) {
        this.formContent = formContent;
        return this;
    }

    @Override
    public String getExt() {
        return ext;
    }

    @Override
    public FlowForm setExt(String ext) {
        this.ext = ext;
        return this;
    }

    @Override
    public String toString() {
        return "FlowForm{" +
                "id=" + id +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", tenantId='" + tenantId + '\'' +
                ", delFlag='" + delFlag + '\'' +
                ", formCode='" + formCode + '\'' +
                ", formName='" + formName + '\'' +
                ", version='" + version + '\'' +
                ", isPublish=" + isPublish +
                ", formType=" + formType +
                ", formPath='" + formPath + '\'' +
                ", formContent='" + formContent + '\'' +
                ", ext='" + ext + '\'' +
                '}';
    }
}
