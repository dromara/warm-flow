package com.monkey.flow.core.domain.entity;

import com.monkey.mybatis.core.entity.FlowEntity;

import java.util.Date;

/**
 * 历史任务记录对象 flow_his_task
 *
 * @author hh
 * @date 2023-03-29
 */
public class FlowHisTask implements FlowEntity {
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /**
     * 对应flow_definition表的id
     */
    private Long definitionId;

    /** 流程名称 */
    private String flowName;

    /**
     * 流程实例表id
     */
    private Long instanceId;

    /**
     * 业务id
     */
    private String businessId;

    /**
     * 开始结点编码
     */
    private String nodeFrom;

    /**
     * 开始结点名称
     */
    private String nodeFromName;

    /**
     * 开始结点类型,0开始结点,1中间结点,2结束结点
     */
    private Integer nodeType;

    /**
     * 目标结点编码
     */
    private String nodeTo;

    /**
     * 结束结点名称
     */
    private String nodeToName;

    /**
     * 账号编码(只记录该流程审核时用的账户)
     */
    private String userCode;

    /**
     * 流程状态
     */
    private Integer flowStatus;

    /**
     * 审批意见
     */
    private String message;

    /**
     * 跳转条件
     */
    private String conditionValue;

    /** 创建者 */
    private String createBy;

    /** 创建时间 */
    private Date createTime;

    /** 更新时间 */
    private Date updateTime;

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

    public String getNodeFrom() {
        return nodeFrom;
    }

    public void setNodeFrom(String nodeFrom) {
        this.nodeFrom = nodeFrom;
    }

    public String getNodeFromName() {
        return nodeFromName;
    }

    public void setNodeFromName(String nodeFromName) {
        this.nodeFromName = nodeFromName;
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

    public String getNodeTo() {
        return nodeTo;
    }

    public void setNodeTo(String nodeTo) {
        this.nodeTo = nodeTo;
    }

    public String getNodeToName() {
        return nodeToName;
    }

    public void setNodeToName(String nodeToName) {
        this.nodeToName = nodeToName;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public Integer getFlowStatus() {
        return flowStatus;
    }

    public void setFlowStatus(Integer flowStatus) {
        this.flowStatus = flowStatus;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getConditionValue() {
        return conditionValue;
    }

    public void setConditionValue(String conditionValue) {
        this.conditionValue = conditionValue;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
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
}
