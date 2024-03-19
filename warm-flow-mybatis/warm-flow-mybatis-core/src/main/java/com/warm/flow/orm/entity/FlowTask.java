package com.warm.flow.orm.entity;

import com.warm.flow.core.entity.Task;

import java.util.List;

/**
 * 待办任务记录对象 flow_task
 *
 * @author warm
 * @date 2023-03-29
 */
public class FlowTask extends FlowEntity implements Task {

    /**
     * 对应flow_definition表的id
     */
    private Long definitionId;

    /**
     * 流程实例表id
     */
    private Long instanceId;

    /**
     * 所属租住（企业）id
     */
    private String tenantId;

    /**
     * 流程名称
     */
    private String flowName;

    /**
     * 业务id
     */
    private String businessId;

    /**
     * 节点编码
     */
    private String nodeCode;

    /**
     * 节点名称
     */
    private String nodeName;

    /**
     * 节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关）
     */
    private Integer nodeType;


    /**
     * 审批者
     */
    private String approver;

    /**
     * 转办人
     */
    private String assignee;

    /**
     * 流程状态（0待提交 1审批中 2 审批通过 8已完成 9已驳回 10失效）
     */
    private Integer flowStatus;

    /**
     * 所属并行网关节点编码
     */
    private String gateWayNode;

    /**
     * 任务变量
     */
    private String variable;

    /**
     * 权限标识（权限类型:权限标识，可以多个，如role:1,role:2)
     */
    private String permissionFlag;

    /**
     * 权限标识 permissionFlag的list形式
     */
    private List<String> permissionList;

    /**
     * 审批表单是否自定义（Y是 2否）
     */
    private String fromCustom;

    /**
     * 审批表单是否自定义（Y是 2否）
     */
    private String fromPath;

    @Override
    public Long getDefinitionId() {
        return definitionId;
    }

    public FlowTask setDefinitionId(Long definitionId) {
        this.definitionId = definitionId;
        return this;
    }

    @Override
    public Long getInstanceId() {
        return instanceId;
    }

    @Override
    public FlowTask setInstanceId(Long instanceId) {
        this.instanceId = instanceId;
        return this;
    }

    @Override
    public String getTenantId() {
        return tenantId;
    }

    @Override
    public FlowTask setTenantId(String tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    @Override
    public String getFlowName() {
        return flowName;
    }

    @Override
    public FlowTask setFlowName(String flowName) {
        this.flowName = flowName;
        return this;
    }

    @Override
    public String getBusinessId() {
        return businessId;
    }

    @Override
    public FlowTask setBusinessId(String businessId) {
        this.businessId = businessId;
        return this;
    }

    @Override
    public String getNodeCode() {
        return nodeCode;
    }

    @Override
    public FlowTask setNodeCode(String nodeCode) {
        this.nodeCode = nodeCode;
        return this;
    }

    @Override
    public String getNodeName() {
        return nodeName;
    }

    @Override
    public FlowTask setNodeName(String nodeName) {
        this.nodeName = nodeName;
        return this;
    }

    @Override
    public Integer getNodeType() {
        return nodeType;
    }

    @Override
    public FlowTask setNodeType(Integer nodeType) {
        this.nodeType = nodeType;
        return this;
    }

    @Override
    public String getApprover() {
        return approver;
    }

    @Override
    public FlowTask setApprover(String approver) {
        this.approver = approver;
        return this;
    }

    @Override
    public String getAssignee() {
        return assignee;
    }

    @Override
    public FlowTask setAssignee(String assignee) {
        this.assignee = assignee;
        return this;
    }

    @Override
    public Integer getFlowStatus() {
        return flowStatus;
    }

    @Override
    public FlowTask setFlowStatus(Integer flowStatus) {
        this.flowStatus = flowStatus;
        return this;
    }

    @Override
    public String getGateWayNode() {
        return gateWayNode;
    }

    @Override
    public FlowTask setGateWayNode(String gateWayNode) {
        this.gateWayNode = gateWayNode;
        return this;
    }

    @Override
    public String getVariable() {
        return variable;
    }

    @Override
    public FlowTask setVariable(String variable) {
        this.variable = variable;
        return this;
    }

    @Override
    public String getPermissionFlag() {
        return permissionFlag;
    }

    @Override
    public FlowTask setPermissionFlag(String permissionFlag) {
        this.permissionFlag = permissionFlag;
        return this;
    }

    @Override
    public List<String> getPermissionList() {
        return permissionList;
    }

    @Override
    public FlowTask setPermissionList(List<String> permissionList) {
        this.permissionList = permissionList;
        return this;
    }

    @Override
    public String getFromCustom() {
        return fromCustom;
    }

    @Override
    public FlowTask setFromCustom(String fromCustom) {
        this.fromCustom = fromCustom;
        return this;
    }

    @Override
    public String getFromPath() {
        return fromPath;
    }

    @Override
    public FlowTask setFromPath(String fromPath) {
        this.fromPath = fromPath;
        return this;
    }

    @Override
    public String toString() {
        return "FlowTask{" +
                "definitionId=" + definitionId +
                ", instanceId=" + instanceId +
                ", tenantId='" + tenantId + '\'' +
                ", flowName='" + flowName + '\'' +
                ", businessId='" + businessId + '\'' +
                ", nodeCode='" + nodeCode + '\'' +
                ", nodeName='" + nodeName + '\'' +
                ", nodeType=" + nodeType +
                ", approver='" + approver + '\'' +
                ", assignee='" + assignee + '\'' +
                ", flowStatus=" + flowStatus +
                ", gateWayNode='" + gateWayNode + '\'' +
                ", permissionFlag='" + permissionFlag + '\'' +
                ", permissionList=" + permissionList +
                ", fromCustom='" + fromCustom + '\'' +
                ", fromPath='" + fromPath + '\'' +
                "} " + super.toString();
    }
}
