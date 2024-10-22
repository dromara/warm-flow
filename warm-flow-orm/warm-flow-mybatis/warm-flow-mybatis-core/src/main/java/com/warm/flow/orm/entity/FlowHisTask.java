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
package com.warm.flow.orm.entity;

import com.warm.flow.core.entity.HisTask;

import java.util.Date;
import java.util.List;

/**
 * 历史任务记录对象 flow_his_task
 *
 * @author warm
 * @date 2023-03-29
 */
public class FlowHisTask implements HisTask {

    /**
     * 主键
     */
    private Long id;

    /**
     * 任务开始时间
     */
    private Date createTime;

    /**
     * 审批完成时间
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
    private String flowName;

    /**
     * 流程实例表id
     */
    private Long instanceId;

    /**
     * 任务表id
     */
    private Long taskId;

    /**
     * 协作方式(1审批 2转办 3委派 4会签 5票签 6加签 7减签)
     */
    private Integer cooperateType;

    /**
     * 业务id
     */
    private String businessId;

    /**
     * 开始节点编码
     */
    private String nodeCode;

    /**
     * 开始节点名称
     */
    private String nodeName;

    /**
     * 开始节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关）
     */
    private Integer nodeType;

    /**
     * 目标节点编码
     */
    private String targetNodeCode;

    /**
     * 结束节点名称
     */
    private String targetNodeName;

    /**
     * 审批者
     */
    private String approver;

    /**
     * 协作人(只有转办、会签、票签、委派)
     */
    private String collaborator;

    /**
     * 权限标识 permissionFlag的list形式
     */
    private List<String> permissionList;

    /**
     * 跳转类型（PASS通过 REJECT退回 NONE无动作）
     */
    private String skipType;

    /**
     * 流程状态（1审批中 2 审批通过 9已退回 10失效）
     */
    private String flowStatus;

    /**
     * 审批意见
     */
    private String message;

    /**
     * 业务详情 存业务类的json
     */
    private String ext;

    /**
     * 创建者
     */
    private String createBy;


    /**
     * 审批表单是否自定义（Y是 2否）
     */
    private String formCustom;

    /**
     * 审批表单是否自定义（Y是 2否）
     */
    private String formPath;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public FlowHisTask setId(Long id) {
        this.id = id;
        return this;
    }

    @Override
    public Date getCreateTime() {
        return createTime;
    }

    @Override
    public FlowHisTask setCreateTime(Date createTime) {
        this.createTime = createTime;
        return this;
    }

    @Override
    public Date getUpdateTime() {
        return updateTime;
    }

    @Override
    public FlowHisTask setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
        return this;
    }

    @Override
    public Long getDefinitionId() {
        return definitionId;
    }

    @Override
    public FlowHisTask setDefinitionId(Long definitionId) {
        this.definitionId = definitionId;
        return this;
    }

    @Override
    public String getFlowName() {
        return flowName;
    }

    @Override
    public FlowHisTask setFlowName(String flowName) {
        this.flowName = flowName;
        return this;
    }

    @Override
    public Long getInstanceId() {
        return instanceId;
    }

    @Override
    public FlowHisTask setInstanceId(Long instanceId) {
        this.instanceId = instanceId;
        return this;
    }

    @Override
    public Integer getCooperateType() {
        return cooperateType;
    }

    @Override
    public FlowHisTask setCooperateType(Integer cooperateType) {
        this.cooperateType = cooperateType;
        return this;
    }

    @Override
    public Long getTaskId() {
        return taskId;
    }

    @Override
    public FlowHisTask setTaskId(Long taskId) {
        this.taskId = taskId;
        return this;
    }

    @Override
    public String getTenantId() {
        return tenantId;
    }

    @Override
    public FlowHisTask setTenantId(String tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    @Override
    public String getDelFlag() {
        return delFlag;
    }

    @Override
    public FlowHisTask setDelFlag(String delFlag) {
        this.delFlag = delFlag;
        return this;
    }

    @Override
    public String getBusinessId() {
        return businessId;
    }

    @Override
    public FlowHisTask setBusinessId(String businessId) {
        this.businessId = businessId;
        return this;
    }

    @Override
    public String getNodeCode() {
        return nodeCode;
    }

    @Override
    public FlowHisTask setNodeCode(String nodeCode) {
        this.nodeCode = nodeCode;
        return this;
    }

    @Override
    public String getNodeName() {
        return nodeName;
    }

    @Override
    public FlowHisTask setNodeName(String nodeName) {
        this.nodeName = nodeName;
        return this;
    }

    @Override
    public Integer getNodeType() {
        return nodeType;
    }

    @Override
    public FlowHisTask setNodeType(Integer nodeType) {
        this.nodeType = nodeType;
        return this;
    }

    @Override
    public String getTargetNodeCode() {
        return targetNodeCode;
    }

    @Override
    public FlowHisTask setTargetNodeCode(String targetNodeCode) {
        this.targetNodeCode = targetNodeCode;
        return this;
    }

    @Override
    public String getTargetNodeName() {
        return targetNodeName;
    }

    @Override
    public String getApprover() {
        return approver;
    }

    @Override
    public FlowHisTask setApprover(String approver) {
        this.approver = approver;
        return this;
    }

    @Override
    public String getCollaborator() {
        return collaborator;
    }

    @Override
    public HisTask setCollaborator(String collaborator) {
        this.collaborator = collaborator;
        return this;
    }

    @Override
    public FlowHisTask setTargetNodeName(String targetNodeName) {
        this.targetNodeName = targetNodeName;
        return this;
    }

    @Override
    public List<String> getPermissionList() {
        return permissionList;
    }

    @Override
    public FlowHisTask setPermissionList(List<String> permissionList) {
        this.permissionList = permissionList;
        return this;
    }

    @Override
    public String getSkipType() {
        return this.skipType;
    }

    @Override
    public HisTask setSkipType(String skipType) {
        this.skipType = skipType;
        return this;
    }

    @Override
    public String getFlowStatus() {
        return flowStatus;
    }

    @Override
    public FlowHisTask setFlowStatus(String flowStatus) {
        this.flowStatus = flowStatus;
        return this;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public FlowHisTask setMessage(String message) {
        this.message = message;
        return this;
    }

    @Override
    public String getExt() {
        return ext;
    }

    @Override
    public FlowHisTask setExt(String ext) {
        this.ext = ext;
        return this;
    }

    @Override
    public String getCreateBy() {
        return createBy;
    }

    @Override
    public FlowHisTask setCreateBy(String createBy) {
        this.createBy = createBy;
        return this;
    }

    @Override
    public String getFormCustom() {
        return formCustom;
    }

    @Override
    public FlowHisTask setFormCustom(String formCustom) {
        this.formCustom = formCustom;
        return this;
    }

    @Override
    public String getFormPath() {
        return formPath;
    }

    @Override
    public FlowHisTask setFormPath(String formPath) {
        this.formPath = formPath;
        return this;
    }

    @Override
    public String toString() {
        return "FlowHisTask{" +
                "id=" + id +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", definitionId=" + definitionId +
                ", flowName='" + flowName + '\'' +
                ", instanceId=" + instanceId +
                ", taskId=" + taskId +
                ", cooperateType=" + cooperateType +
                ", tenantId='" + tenantId + '\'' +
                ", businessId='" + businessId + '\'' +
                ", nodeCode='" + nodeCode + '\'' +
                ", nodeName='" + nodeName + '\'' +
                ", nodeType=" + nodeType +
                ", targetNodeCode='" + targetNodeCode + '\'' +
                ", targetNodeName='" + targetNodeName + '\'' +
                ", approver='" + approver + '\'' +
                ", collaborator='" + collaborator + '\'' +
                ", permissionList=" + permissionList +
                ", skipType=" + skipType +
                ", flowStatus=" + flowStatus +
                ", message='" + message + '\'' +
                ", ext='" + ext + '\'' +
                ", createBy='" + createBy + '\'' +
                ", formCustom='" + formCustom + '\'' +
                ", formPath='" + formPath + '\'' +
                "} " + super.toString();
    }
}
