package com.warm.flow.orm.entity;

import com.warm.flow.core.entity.Skip;

import java.util.Date;

/**
 * 节点跳转关联对象 flow_skip
 *
 * @author warm
 * @date 2023-03-29
 */
public class FlowSkip implements Skip {

    /**
     * 主键
     */
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
     * 流程id
     */
    private Long definitionId;

    /**
     * 节点id
     */
    private Long nodeId;

    /**
     * 当前流程节点的编码
     */
    private String nowNodeCode;

    /**
     * 当前节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关）
     */
    private Integer nowNodeType;

    /**
     * 下一个流程节点的编码
     */
    private String nextNodeCode;

    /**
     * 下一个节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关）
     */
    private Integer nextNodeType;

    /**
     * 跳转名称
     */
    private String skipName;

    /**
     * 跳转类型（PASS审批通过 REJECT退回）
     */
    private String skipType;

    /**
     * 跳转条件
     */
    private String skipCondition;

    /**
     * 流程跳转坐标
     */
    private String coordinate;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public FlowSkip setId(Long id) {
        this.id = id;
        return this;
    }

    @Override
    public Date getCreateTime() {
        return createTime;
    }

    @Override
    public FlowSkip setCreateTime(Date createTime) {
        this.createTime = createTime;
        return this;
    }

    @Override
    public Date getUpdateTime() {
        return updateTime;
    }

    @Override
    public FlowSkip setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
        return this;
    }

    @Override
    public String getTenantId() {
        return tenantId;
    }

    @Override
    public FlowSkip setTenantId(String tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    @Override
    public String getDelFlag() {
        return delFlag;
    }

    @Override
    public Skip setDelFlag(String delFlag) {
        this.delFlag = delFlag;
        return this;
    }

    @Override
    public Long getDefinitionId() {
        return definitionId;
    }

    @Override
    public FlowSkip setDefinitionId(Long definitionId) {
        this.definitionId = definitionId;
        return this;
    }

    @Override
    public Long getNodeId() {
        return nodeId;
    }

    @Override
    public FlowSkip setNodeId(Long nodeId) {
        this.nodeId = nodeId;
        return this;
    }

    @Override
    public String getNowNodeCode() {
        return nowNodeCode;
    }

    @Override
    public FlowSkip setNowNodeCode(String nowNodeCode) {
        this.nowNodeCode = nowNodeCode;
        return this;
    }

    @Override
    public Integer getNowNodeType() {
        return nowNodeType;
    }

    @Override
    public FlowSkip setNowNodeType(Integer nowNodeType) {
        this.nowNodeType = nowNodeType;
        return this;
    }

    @Override
    public String getNextNodeCode() {
        return nextNodeCode;
    }

    @Override
    public FlowSkip setNextNodeCode(String nextNodeCode) {
        this.nextNodeCode = nextNodeCode;
        return this;
    }

    @Override
    public Integer getNextNodeType() {
        return nextNodeType;
    }

    @Override
    public FlowSkip setNextNodeType(Integer nextNodeType) {
        this.nextNodeType = nextNodeType;
        return this;
    }

    @Override
    public String getSkipName() {
        return skipName;
    }

    @Override
    public FlowSkip setSkipName(String skipName) {
        this.skipName = skipName;
        return this;
    }

    @Override
    public String getSkipType() {
        return skipType;
    }

    @Override
    public FlowSkip setSkipType(String skipType) {
        this.skipType = skipType;
        return this;
    }

    @Override
    public String getSkipCondition() {
        return skipCondition;
    }

    @Override
    public FlowSkip setSkipCondition(String skipCondition) {
        this.skipCondition = skipCondition;
        return this;
    }

    @Override
    public String getCoordinate() {
        return coordinate;
    }

    @Override
    public FlowSkip setCoordinate(String coordinate) {
        this.coordinate = coordinate;
        return this;
    }

    @Override
    public String toString() {
        return "FlowSkip{" +
                "id=" + id +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", definitionId=" + definitionId +
                ", nodeId=" + nodeId +
                ", nowNodeCode='" + nowNodeCode + '\'' +
                ", nowNodeType=" + nowNodeType +
                ", nextNodeCode='" + nextNodeCode + '\'' +
                ", nextNodeType=" + nextNodeType +
                ", skipName='" + skipName + '\'' +
                ", skipType='" + skipType + '\'' +
                ", skipCondition='" + skipCondition + '\'' +
                ", coordinate='" + coordinate + '\'' +
                "} " + super.toString();
    }
}
