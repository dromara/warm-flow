package com.warm.flow.core.domain.entity;

import java.util.List;

/**
 * 待办任务记录对象 flow_task
 *
 * @author warm
 * @date 2023-03-29
 */
public class FlowTask extends FlowEntity {
    private static final long serialVersionUID = 1L;

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

    public Long getDefinitionId() {
        return definitionId;
    }

    public FlowTask setDefinitionId(Long definitionId) {
        this.definitionId = definitionId;
        return this;
    }

    public Long getInstanceId() {
        return instanceId;
    }

    public FlowTask setInstanceId(Long instanceId) {
        this.instanceId = instanceId;
        return this;
    }

    public String getTenantId() {
        return tenantId;
    }

    public FlowTask setTenantId(String tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public String getFlowName() {
        return flowName;
    }

    public FlowTask setFlowName(String flowName) {
        this.flowName = flowName;
        return this;
    }

    public String getBusinessId() {
        return businessId;
    }

    public FlowTask setBusinessId(String businessId) {
        this.businessId = businessId;
        return this;
    }

    public String getNodeCode() {
        return nodeCode;
    }

    public FlowTask setNodeCode(String nodeCode) {
        this.nodeCode = nodeCode;
        return this;
    }

    public String getNodeName() {
        return nodeName;
    }

    public FlowTask setNodeName(String nodeName) {
        this.nodeName = nodeName;
        return this;
    }

    public Integer getNodeType() {
        return nodeType;
    }

    public FlowTask setNodeType(Integer nodeType) {
        this.nodeType = nodeType;
        return this;
    }

    public String getApprover() {
        return approver;
    }

    public FlowTask setApprover(String approver) {
        this.approver = approver;
        return this;
    }

    public String getAssignee() {
        return assignee;
    }

    public FlowTask setAssignee(String assignee) {
        this.assignee = assignee;
        return this;
    }

    public Integer getFlowStatus() {
        return flowStatus;
    }

    public FlowTask setFlowStatus(Integer flowStatus) {
        this.flowStatus = flowStatus;
        return this;
    }

    public String getGateWayNode() {
        return gateWayNode;
    }

    public FlowTask setGateWayNode(String gateWayNode) {
        this.gateWayNode = gateWayNode;
        return this;
    }

    public String getPermissionFlag() {
        return permissionFlag;
    }

    public FlowTask setPermissionFlag(String permissionFlag) {
        this.permissionFlag = permissionFlag;
        return this;
    }

    public List<String> getPermissionList() {
        return permissionList;
    }

    public FlowTask setPermissionList(List<String> permissionList) {
        this.permissionList = permissionList;
        return this;
    }

    public String getFromCustom() {
        return fromCustom;
    }

    public FlowTask setFromCustom(String fromCustom) {
        this.fromCustom = fromCustom;
        return this;
    }

    public String getFromPath() {
        return fromPath;
    }

    public FlowTask setFromPath(String fromPath) {
        this.fromPath = fromPath;
        return this;
    }
}
