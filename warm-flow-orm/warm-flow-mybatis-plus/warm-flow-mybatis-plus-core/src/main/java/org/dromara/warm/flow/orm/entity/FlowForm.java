package org.dromara.warm.flow.orm.entity;

import com.baomidou.mybatisplus.annotation.*;
import org.dromara.warm.flow.core.entity.Form;

import java.util.Date;

/**
 * @author vanlin
 * @className FlowForm
 * @description
 * @since 2024-9-23 16:15
 */
@TableName("flow_form")
public class FlowForm implements Form {

    /**
     * 主键
     */
    @TableId
    private Long id;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     * 删除标记
     */
    @TableLogic
    private String delFlag;

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
     * 租户ID
     */
    private String tenantId;


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
