package com.warm.flow.core.domain.entity;

/**
 * 流程实例对象 flow_instance
 *
 * @author warm
 * @date 2023-03-29
 */
public class FlowInstance extends FlowEntity {
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
     * 业务id
     */
    private String businessId;

    /**
     * 结点类型（0开始结点 1中间结点 2结束结点 3互斥网关 4并行网关）
     */
    private Integer nodeType;

    /**
     * 流程结点编码   每个流程的nodeCode是唯一的,即definitionId+nodeCode唯一,在数据库层面做了控制
     */
    private String nodeCode;

    /**
     * 流程结点名称
     */
    private String nodeName;

    /**
     * 流程状态（0待提交 1审批中 2 审批通过 8已完成 9已驳回 10失效）
     */
    private Integer flowStatus;

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

    /**
     * 扩展字段
     */
    private String ext;

    public Long getDefinitionId() {
        return definitionId;
    }

    public FlowInstance setDefinitionId(Long definitionId) {
        this.definitionId = definitionId;
        return this;
    }

    public String getFlowName() {
        return flowName;
    }

    public FlowInstance setFlowName(String flowName) {
        this.flowName = flowName;
        return this;
    }

    public String getBusinessId() {
        return businessId;
    }

    public FlowInstance setBusinessId(String businessId) {
        this.businessId = businessId;
        return this;
    }

    public Integer getNodeType() {
        return nodeType;
    }

    public FlowInstance setNodeType(Integer nodeType) {
        this.nodeType = nodeType;
        return this;
    }

    public String getNodeCode() {
        return nodeCode;
    }

    public FlowInstance setNodeCode(String nodeCode) {
        this.nodeCode = nodeCode;
        return this;
    }

    public String getNodeName() {
        return nodeName;
    }

    public FlowInstance setNodeName(String nodeName) {
        this.nodeName = nodeName;
        return this;
    }

    public Integer getFlowStatus() {
        return flowStatus;
    }

    public FlowInstance setFlowStatus(Integer flowStatus) {
        this.flowStatus = flowStatus;
        return this;
    }

    public String getCreateBy() {
        return createBy;
    }

    public FlowInstance setCreateBy(String createBy) {
        this.createBy = createBy;
        return this;
    }

    public String getFromCustom() {
        return fromCustom;
    }

    public FlowInstance setFromCustom(String fromCustom) {
        this.fromCustom = fromCustom;
        return this;
    }

    public String getFromPath() {
        return fromPath;
    }

    public FlowInstance setFromPath(String fromPath) {
        this.fromPath = fromPath;
        return this;
    }

    public String getExt() {
        return ext;
    }

    public FlowInstance setExt(String ext) {
        this.ext = ext;
        return this;
    }
}
