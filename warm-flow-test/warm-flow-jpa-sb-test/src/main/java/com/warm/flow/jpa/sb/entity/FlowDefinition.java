package com.warm.flow.jpa.sb.entity;

import javax.persistence.*;
import java.util.Date;

/**
 * 流程定义对象 flow_definition
 *
 * @author warm
 * @date 2023-03-29
 */
@Entity
@Table(name = "flow_definition")
public class FlowDefinition {

    /**
     * 主键
     */
    @Id
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

    /**
     * 流程编码
     */
    private String flowCode;

    /**
     * 流程名称
     */
    private String flowName;

    /**
     * 流程版本
     */
    private String version;

    /**
     * 是否发布（0未开启 1开启）
     */
    private Integer isPublish;

    /**
     * 审批表单是否自定义（Y是 2否）
     */
    private String fromCustom;

    /**
     * 审批表单是否自定义（Y是 2否）
     */
    private String fromPath;

    public Long getId() {
        return id;
    }

    public FlowDefinition setId(Long id) {
        this.id = id;
        return this;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public FlowDefinition setCreateTime(Date createTime) {
        this.createTime = createTime;
        return this;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public FlowDefinition setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
        return this;
    }

    public String getTenantId() {
        return tenantId;
    }

    public FlowDefinition setTenantId(String tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public FlowDefinition setDelFlag(String delFlag) {
        this.delFlag = delFlag;
        return this;
    }

    public String getFlowCode() {
        return flowCode;
    }

    public FlowDefinition setFlowCode(String flowCode) {
        this.flowCode = flowCode;
        return this;
    }

    public String getFlowName() {
        return flowName;
    }

    public FlowDefinition setFlowName(String flowName) {
        this.flowName = flowName;
        return this;
    }

    public String getVersion() {
        return version;
    }

    public FlowDefinition setVersion(String version) {
        this.version = version;
        return this;
    }

    public Integer getIsPublish() {
        return isPublish;
    }

    public FlowDefinition setIsPublish(Integer isPublish) {
        this.isPublish = isPublish;
        return this;
    }

    public String getFromCustom() {
        return fromCustom;
    }

    public FlowDefinition setFromCustom(String fromCustom) {
        this.fromCustom = fromCustom;
        return this;
    }

    public String getFromPath() {
        return fromPath;
    }

    public FlowDefinition setFromPath(String fromPath) {
        this.fromPath = fromPath;
        return this;
    }


    @Override
    public String toString() {
        return "FlowDefinition{" +
                "id=" + id +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", flowCode='" + flowCode + '\'' +
                ", flowName='" + flowName + '\'' +
                ", version='" + version + '\'' +
                ", isPublish=" + isPublish +
                ", fromCustom='" + fromCustom + '\'' +
                ", fromPath='" + fromPath + '\'' +
                '}';
    }
}
