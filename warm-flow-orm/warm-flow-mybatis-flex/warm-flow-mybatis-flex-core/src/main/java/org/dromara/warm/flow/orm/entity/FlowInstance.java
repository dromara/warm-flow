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

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import org.dromara.warm.flow.core.FlowFactory;
import org.dromara.warm.flow.core.entity.Instance;

import java.util.Date;
import java.util.Map;

/**
 * 流程实例对象 flow_instance
 *
 * @author warm
 * @since 2023-03-29
 */
@Table("flow_instance")
public class FlowInstance implements Instance {

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
     * 对应flow_definition表的id
     */
    private Long definitionId;

    /**
     * 流程名称
     */
    @Column(ignore = true)
    private String flowName;

    /**
     * 业务id
     */
    private String businessId;

    /**
     * 节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关）
     */
    private Integer nodeType;

    /**
     * 流程节点编码   每个流程的nodeCode是唯一的,即definitionId+nodeCode唯一,在数据库层面做了控制
     */
    private String nodeCode;

    /**
     * 流程节点名称
     */
    private String nodeName;

    /**
     * 流程变量
     */
    private String variable;

    /**
     * 流程状态（0待提交 1审批中 2 审批通过 3自动通过 4终止 5作废 6撤销 7取回  8已完成 9已退回 10失效）
     */
    private String flowStatus;

    /**
     * 流程激活状态（0挂起 1激活）
     */
    private Integer activityStatus;

    /**
     * 创建者
     */
    private String createBy;

    /**
     * 审批表单是否自定义（Y是 2否）
     */
    @Column(ignore = true)
    private String formCustom;

    /**
     * 审批表单是否自定义（Y是 2否）
     */
    @Column(ignore = true)
    private String formPath;

    /**
     * 扩展字段，预留给业务系统使用
     */
    private String ext;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public FlowInstance setId(Long id) {
        this.id = id;
        return this;
    }

    @Override
    public Date getCreateTime() {
        return createTime;
    }

    @Override
    public FlowInstance setCreateTime(Date createTime) {
        this.createTime = createTime;
        return this;
    }

    @Override
    public Date getUpdateTime() {
        return updateTime;
    }

    @Override
    public FlowInstance setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
        return this;
    }

    @Override
    public Long getDefinitionId() {
        return definitionId;
    }

    @Override
    public FlowInstance setDefinitionId(Long definitionId) {
        this.definitionId = definitionId;
        return this;
    }

    @Override
    public String getFlowName() {
        return flowName;
    }

    @Override
    public FlowInstance setFlowName(String flowName) {
        this.flowName = flowName;
        return this;
    }

    @Override
    public String getBusinessId() {
        return businessId;
    }

    @Override
    public FlowInstance setBusinessId(String businessId) {
        this.businessId = businessId;
        return this;
    }

    @Override
    public String getTenantId() {
        return tenantId;
    }

    @Override
    public FlowInstance setTenantId(String tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    @Override
    public String getDelFlag() {
        return delFlag;
    }

    @Override
    public FlowInstance setDelFlag(String delFlag) {
        this.delFlag = delFlag;
        return this;
    }

    @Override
    public Integer getNodeType() {
        return nodeType;
    }

    @Override
    public FlowInstance setNodeType(Integer nodeType) {
        this.nodeType = nodeType;
        return this;
    }

    @Override
    public String getNodeCode() {
        return nodeCode;
    }

    @Override
    public FlowInstance setNodeCode(String nodeCode) {
        this.nodeCode = nodeCode;
        return this;
    }

    @Override
    public String getNodeName() {
        return nodeName;
    }

    @Override
    public FlowInstance setNodeName(String nodeName) {
        this.nodeName = nodeName;
        return this;
    }

    @Override
    public String getVariable() {
        return variable;
    }

    @Override
    public FlowInstance setVariable(String variable) {
        this.variable = variable;
        return this;
    }

    @Override
    public String getFlowStatus() {
        return flowStatus;
    }

    @Override
    public FlowInstance setFlowStatus(String flowStatus) {
        this.flowStatus = flowStatus;
        return this;
    }

    @Override
    public Integer getActivityStatus() {
        return activityStatus;
    }

    @Override
    public Instance setActivityStatus(Integer activityStatus) {
        this.activityStatus = activityStatus;
        return this;
    }

    @Override
    public String getCreateBy() {
        return createBy;
    }

    @Override
    public FlowInstance setCreateBy(String createBy) {
        this.createBy = createBy;
        return this;
    }

    @Override
    public String getFormCustom() {
        return formCustom;
    }

    @Override
    public FlowInstance setFormCustom(String formCustom) {
        this.formCustom = formCustom;
        return this;
    }

    @Override
    public String getFormPath() {
        return formPath;
    }

    @Override
    public FlowInstance setFormPath(String formPath) {
        this.formPath = formPath;
        return this;
    }

    @Override
    public String getExt() {
        return ext;
    }

    @Override
    public FlowInstance setExt(String ext) {
        this.ext = ext;
        return this;
    }

    @Override
    public String toString() {
        return "FlowInstance{" +
                "id=" + id +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", definitionId=" + definitionId +
                ", flowName='" + flowName + '\'' +
                ", businessId='" + businessId + '\'' +
                ", tenantId='" + tenantId + '\'' +
                ", nodeType=" + nodeType +
                ", nodeCode='" + nodeCode + '\'' +
                ", nodeName='" + nodeName + '\'' +
                ", variable='" + variable + '\'' +
                ", flowStatus=" + flowStatus +
                ", activityStatus=" + activityStatus +
                ", createBy='" + createBy + '\'' +
                ", formCustom='" + formCustom + '\'' +
                ", formPath='" + formPath + '\'' +
                ", ext='" + ext + '\'' +
                "} " + super.toString();
    }
}
