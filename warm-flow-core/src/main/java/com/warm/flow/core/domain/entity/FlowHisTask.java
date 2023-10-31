package com.warm.flow.core.domain.entity;

import java.util.List;

/**
 * 历史任务记录对象 flow_his_task
 *
 * @author warm
 * @date 2023-03-29
 */
public class FlowHisTask extends FlowEntity {
    private static final long serialVersionUID = 1L;

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
     * 所属租住（企业）id
     */
    private String tenantId;

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
     * 权限标识（权限类型:权限标识，可以多个，如role:1,role:2)
     */
    private String permissionFlag;

    /**
     * 权限标识 permissionFlag的list形式
     */
    private List<String> permissionList;

    /**
     * 流程状态（0待提交 1审批中 2 审批通过 8已完成 9已驳回 10失效）
     */
    private Integer flowStatus;

    /**
     * 所属并行网关节点编码
     */
    private String gateWayNode;

    /**
     * 审批意见
     */
    private String message;

    /**
     * 创建者
     */
    private String createBy;


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

    public FlowHisTask setDefinitionId(Long definitionId) {
        this.definitionId = definitionId;
        return this;
    }

    public String getFlowName() {
        return flowName;
    }

    public FlowHisTask setFlowName(String flowName) {
        this.flowName = flowName;
        return this;
    }

    public Long getInstanceId() {
        return instanceId;
    }

    public FlowHisTask setInstanceId(Long instanceId) {
        this.instanceId = instanceId;
        return this;
    }

    public String getTenantId() {
        return tenantId;
    }

    public FlowHisTask setTenantId(String tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public String getBusinessId() {
        return businessId;
    }

    public FlowHisTask setBusinessId(String businessId) {
        this.businessId = businessId;
        return this;
    }

    public String getNodeCode() {
        return nodeCode;
    }

    public FlowHisTask setNodeCode(String nodeCode) {
        this.nodeCode = nodeCode;
        return this;
    }

    public String getNodeName() {
        return nodeName;
    }

    public FlowHisTask setNodeName(String nodeName) {
        this.nodeName = nodeName;
        return this;
    }

    public Integer getNodeType() {
        return nodeType;
    }

    public FlowHisTask setNodeType(Integer nodeType) {
        this.nodeType = nodeType;
        return this;
    }

    public String getTargetNodeCode() {
        return targetNodeCode;
    }

    public FlowHisTask setTargetNodeCode(String targetNodeCode) {
        this.targetNodeCode = targetNodeCode;
        return this;
    }

    public String getTargetNodeName() {
        return targetNodeName;
    }

    public FlowHisTask setTargetNodeName(String targetNodeName) {
        this.targetNodeName = targetNodeName;
        return this;
    }

    public String getApprover() {
        return approver;
    }

    public FlowHisTask setApprover(String approver) {
        this.approver = approver;
        return this;
    }

    public String getPermissionFlag() {
        return permissionFlag;
    }

    public FlowHisTask setPermissionFlag(String permissionFlag) {
        this.permissionFlag = permissionFlag;
        return this;
    }

    public List<String> getPermissionList() {
        return permissionList;
    }

    public FlowHisTask setPermissionList(List<String> permissionList) {
        this.permissionList = permissionList;
        return this;
    }

    public Integer getFlowStatus() {
        return flowStatus;
    }

    public FlowHisTask setFlowStatus(Integer flowStatus) {
        this.flowStatus = flowStatus;
        return this;
    }

    public String getGateWayNode() {
        return gateWayNode;
    }

    public FlowHisTask setGateWayNode(String gateWayNode) {
        this.gateWayNode = gateWayNode;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public FlowHisTask setMessage(String message) {
        this.message = message;
        return this;
    }

    public String getCreateBy() {
        return createBy;
    }

    public FlowHisTask setCreateBy(String createBy) {
        this.createBy = createBy;
        return this;
    }

    public String getFromCustom() {
        return fromCustom;
    }

    public FlowHisTask setFromCustom(String fromCustom) {
        this.fromCustom = fromCustom;
        return this;
    }

    public String getFromPath() {
        return fromPath;
    }

    public FlowHisTask setFromPath(String fromPath) {
        this.fromPath = fromPath;
        return this;
    }
}
