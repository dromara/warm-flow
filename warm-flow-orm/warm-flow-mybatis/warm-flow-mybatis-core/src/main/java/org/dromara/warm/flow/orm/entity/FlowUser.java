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

import org.dromara.warm.flow.core.entity.User;

import java.util.Date;

/**
 * 流程用户对象 flow_user
 *
 * @author xiarg
 * @date 2024/5/10 10:58
 */
public class FlowUser implements User {

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
     * 人员类型（1待办任务的审批人权限 2待办任务的转办人权限 3待办任务的委托人权限）
     */
    private String type;

    /**
     * 权限人
     */
    private String processedBy;

    /**
     * 任务表id
     */
    private Long associated;

    /**
     * 创建人：比如作为委托的人保存
     */
    private String createBy;

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
    public String getCreateBy() {
        return this.createBy;
    }

    @Override
    public User setCreateBy(String createBy) {
        this.createBy = createBy;
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
