package com.warm.flow.orm.entity;

import com.warm.flow.core.entity.Skip;

import javax.persistence.*;
import java.util.Date;

/**
 * 节点跳转关联对象 flow_skip
 *
 * @author vanlin
 * @date 2024-05-08
 */
@Entity
@Table(name = "flow_skip")
public class FlowSkip extends AbstractRootEntity<FlowSkip> implements Skip {

    /**
     * 流程id
     */
    @Column(name="definition_id")
    private Long definitionId;

    /**
     * 节点id
     */
    @Transient
    private Long nodeId;

    /**
     * 当前流程节点的编码
     */
    @Column(name="now_node_code")
    private String nowNodeCode;

    /**
     * 当前节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关）
     */
    @Column(name="now_node_type")
    private Integer nowNodeType;

    /**
     * 下一个流程节点的编码
     */
    @Column(name="next_node_code")
    private String nextNodeCode;

    /**
     * 下一个节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关）
     */
    @Column(name="next_node_type")
    private Integer nextNodeType;

    /**
     * 跳转名称
     */
    @Column(name="skip_name")
    private String skipName;

    /**
     * 跳转类型（PASS审批通过 REJECT退回）
     */
    @Column(name="skip_type")
    private String skipType;

    /**
     * 跳转条件
     */
    @Column(name="skip_condition")
    private String skipCondition;

    /**
     * 流程跳转坐标
     */
    @Column(name="coordinate")
    private String coordinate;

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
                "id=" + super.getId() +
                ", createTime=" + super.getCreateTime() +
                ", updateTime=" + super.getUpdateTime() +
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
