package com.warm.flow.core.domain.entity;

import com.warm.mybatis.core.entity.WarmEntity;

import java.util.Date;
import java.util.List;

/**
 * 待办任务记录对象 flow_task
 *
 * @author warm
 * @date 2023-03-29
 */
public class FlowTask implements WarmEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;

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
    private Long tenantId;

    /**
     * 流程名称
     */
    private String flowName;

    /**
     * 业务id
     */
    private String businessId;

    /**
     * 结点编码
     */
    private String nodeCode;

    /**
     * 结点名称
     */
    private String nodeName;

    /**
     * 结点类型,0开始结点,1中间结点,2结束结点
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
     * 流程状态（0待提交 1审批中 2 通过 8已完成 9已驳回 10失效）
     */
    private Integer flowStatus;

    /**
     * 所属并行网关结点编码
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
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 审批表单是否自定义（Y是 2否）
     */
    private String fromCustom;

    /**
     * 审批表单是否自定义（Y是 2否）
     */
    private String fromPath;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(Long instanceId) {
        this.instanceId = instanceId;
    }

    public String getNodeCode() {
        return nodeCode;
    }

    public void setNodeCode(String nodeCode) {
        this.nodeCode = nodeCode;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public Long getDefinitionId() {
        return definitionId;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public void setDefinitionId(Long definitionId) {
        this.definitionId = definitionId;
    }

    public Integer getNodeType() {
        return nodeType;
    }

    public void setNodeType(Integer nodeType) {
        this.nodeType = nodeType;
    }

    public String getApprover() {
        return approver;
    }

    public void setApprover(String approver) {
        this.approver = approver;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public Integer getFlowStatus() {
        return flowStatus;
    }

    public void setFlowStatus(Integer flowStatus) {
        this.flowStatus = flowStatus;
    }

    public String getGateWayNode() {
        return gateWayNode;
    }

    public void setGateWayNode(String gateWayNode) {
        this.gateWayNode = gateWayNode;
    }

    public String getPermissionFlag() {
        return permissionFlag;
    }

    public void setPermissionFlag(String permissionFlag) {
        this.permissionFlag = permissionFlag;
    }

    public List<String> getPermissionList() {
        return permissionList;
    }

    public void setPermissionList(List<String> permissionList) {
        this.permissionList = permissionList;
    }

    @Override
    public Date getCreateTime() {
        return createTime;
    }

    @Override
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public Date getUpdateTime() {
        return updateTime;
    }

    @Override
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getFlowName() {
        return flowName;
    }

    public void setFlowName(String flowName) {
        this.flowName = flowName;
    }

    public String getFromCustom() {
        return fromCustom;
    }

    public void setFromCustom(String fromCustom) {
        this.fromCustom = fromCustom;
    }

    public String getFromPath() {
        return fromPath;
    }

    public void setFromPath(String fromPath) {
        this.fromPath = fromPath;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }
}
