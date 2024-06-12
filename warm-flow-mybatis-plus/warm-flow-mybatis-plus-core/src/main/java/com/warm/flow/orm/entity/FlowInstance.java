package com.warm.flow.orm.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.warm.flow.core.entity.Instance;

import java.util.Date;

/**
 * 流程实例对象 flow_instance
 *
 * @author warm
 * @date 2023-03-29
 */
@TableName("flow_instance")
public class FlowInstance implements Instance {

    /**
     * 主键
     */
    @TableId
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
    @TableField(exist = false)
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
     * 流程状态（0待提交 1审批中 2 审批通过 3自动通过 8已完成 9已退回 10失效）
     */
    private Integer flowStatus;

    /**
     * 创建者
     */
    private String createBy;

    /**
     * 审批表单是否自定义（Y是 2否）
     */
    @TableField(exist = false)
    private String fromCustom;

    /**
     * 审批表单是否自定义（Y是 2否）
     */
    @TableField(exist = false)
    private String fromPath;

    /**
     * 扩展字段
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
    public Integer getFlowStatus() {
        return flowStatus;
    }

    @Override
    public FlowInstance setFlowStatus(Integer flowStatus) {
        this.flowStatus = flowStatus;
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
    public String getFromCustom() {
        return fromCustom;
    }

    @Override
    public FlowInstance setFromCustom(String fromCustom) {
        this.fromCustom = fromCustom;
        return this;
    }

    @Override
    public String getFromPath() {
        return fromPath;
    }

    @Override
    public FlowInstance setFromPath(String fromPath) {
        this.fromPath = fromPath;
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
                ", createBy='" + createBy + '\'' +
                ", fromCustom='" + fromCustom + '\'' +
                ", fromPath='" + fromPath + '\'' +
                ", ext='" + ext + '\'' +
                "} " + super.toString();
    }
}
