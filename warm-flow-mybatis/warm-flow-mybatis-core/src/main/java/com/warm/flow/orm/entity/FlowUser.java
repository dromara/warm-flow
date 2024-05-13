package com.warm.flow.orm.entity;

import com.warm.flow.core.entity.User;

import java.util.Date;

/**
 * 流程用户对象 flow_user
 *
 * @author xiarg
 * @date 2024/5/10 10:58
 */
public class FlowUser implements User{

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

    /**
     * 1-审批人权限 2-转办人权限 3-抄送人权限 4-已审批人
     */
    private String type;

    /**
     * 权限(role:1/user:1)/已审批人(用户id)
     */
    private String processedBy;

    /**
     * 关联id（审批人和转办人是代办任务id，抄送人是实例id，已审批人是历史表id）
     */
    private long associated;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public FlowUser setId(Long id) {
        this.id = id;
        return this;
    }

    @Override
    public Date getCreateTime() {
        return createTime;
    }

    @Override
    public FlowUser setCreateTime(Date createTime) {
        this.createTime = createTime;
        return this;
    }

    @Override
    public Date getUpdateTime() {
        return updateTime;
    }

    @Override
    public FlowUser setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
        return this;
    }

    @Override
    public String getTenantId() {
        return tenantId;
    }

    @Override
    public FlowUser setTenantId(String tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    @Override
    public String getDelFlag() {
        return delFlag;
    }

    @Override
    public FlowUser setDelFlag(String delFlag) {
        this.delFlag = delFlag;
        return this;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public FlowUser setType(String type) {
        this.type = type;
        return this;
    }

    @Override
    public String getProcessedBy() {
        return processedBy;
    }

    @Override
    public FlowUser setProcessedBy(String processedBy) {
        this.processedBy = processedBy;
        return this;
    }

    @Override
    public Long getAssociated() {
        return associated;
    }

    @Override
    public FlowUser setAssociated(Long associated) {
        this.associated = associated;
        return this;
    }

    @Override
    public String toString() {
        return "FlowUser{" +
                "id=" + id +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", tenantId='" + tenantId + '\'' +
                ", delFlag='" + delFlag + '\'' +
                ", type='" + type + '\'' +
                ", processed_by='" + processedBy + '\'' +
                ", associated=" + associated +
                '}';
    }
}
