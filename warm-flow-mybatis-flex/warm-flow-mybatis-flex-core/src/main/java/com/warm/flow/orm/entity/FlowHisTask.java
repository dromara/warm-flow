package com.warm.flow.orm.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import com.warm.flow.core.entity.HisTask;

import java.util.Date;
import java.util.List;

/**
 * 历史任务记录对象 flow_his_task
 *
 * @author warm
 * @date 2023-03-29
 */
@Table("flow_his_task")
public class FlowHisTask implements HisTask {

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
    @Column(ignore = true)
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
    @Column(ignore = true)
    private List<String> permissionList;

    /**
     * 流程状态（1审批中 2 审批通过 9已退回 10失效）
     */
    private Integer flowStatus;

    /**
     * 审批意见
     */
    private String message;

    /**
     * 审批意见
     */
    private String ext;

    /**
     * 创建者
     */
    @Column(ignore = true)
    private String createBy;


    /**
     * 审批表单是否自定义（Y是 2否）
     */
    @Column(ignore = true)
    private String fromCustom;

    /**
     * 审批表单是否自定义（Y是 2否）
     */
    @Column(ignore = true)
    private String fromPath;

    @Override
    public String getExt(){
        return ext;
    }

    public HisTask setExt(String ext){
        this.ext = ext;
        return this;
    }
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
    public Long getTaskId() {
        return taskId;
    }

    @Override
    public FlowHisTask setTaskId(Long taskId) {
        this.taskId = taskId;
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
    public FlowHisTask setTargetNodeName(String targetNodeName) {
        this.targetNodeName = targetNodeName;
        return this;
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
    public List<String> getPermissionList() {
        return permissionList;
    }

    @Override
    public FlowHisTask setPermissionList(List<String> permissionList) {
        this.permissionList = permissionList;
        return this;
    }

    @Override
    public Integer getFlowStatus() {
        return flowStatus;
    }

    @Override
    public FlowHisTask setFlowStatus(Integer flowStatus) {
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
    public String getCreateBy() {
        return createBy;
    }

    @Override
    public FlowHisTask setCreateBy(String createBy) {
        this.createBy = createBy;
        return this;
    }

    @Override
    public String getFromCustom() {
        return fromCustom;
    }

    @Override
    public FlowHisTask setFromCustom(String fromCustom) {
        this.fromCustom = fromCustom;
        return this;
    }

    @Override
    public String getFromPath() {
        return fromPath;
    }

    @Override
    public FlowHisTask setFromPath(String fromPath) {
        this.fromPath = fromPath;
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
                ", flowStatus=" + flowStatus +
                ", message='" + message + '\'' +
                ", createBy='" + createBy + '\'' +
                ", fromCustom='" + fromCustom + '\'' +
                ", fromPath='" + fromPath + '\'' +
                "} " + super.toString();
    }
}
