package com.warm.flow.core.domain.entity;

/**
 * 节点跳转关联对象 flow_skip
 *
 * @author warm
 * @date 2023-03-29
 */
public class FlowSkip extends FlowEntity {
    private static final long serialVersionUID = 1L;

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
     * 跳转类型（PASS审批通过 REJECT驳回）
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

    public Long getDefinitionId() {
        return definitionId;
    }

    public FlowSkip setDefinitionId(Long definitionId) {
        this.definitionId = definitionId;
        return this;
    }

    public Long getNodeId() {
        return nodeId;
    }

    public FlowSkip setNodeId(Long nodeId) {
        this.nodeId = nodeId;
        return this;
    }

    public String getNowNodeCode() {
        return nowNodeCode;
    }

    public FlowSkip setNowNodeCode(String nowNodeCode) {
        this.nowNodeCode = nowNodeCode;
        return this;
    }

    public Integer getNowNodeType() {
        return nowNodeType;
    }

    public FlowSkip setNowNodeType(Integer nowNodeType) {
        this.nowNodeType = nowNodeType;
        return this;
    }

    public String getNextNodeCode() {
        return nextNodeCode;
    }

    public FlowSkip setNextNodeCode(String nextNodeCode) {
        this.nextNodeCode = nextNodeCode;
        return this;
    }

    public Integer getNextNodeType() {
        return nextNodeType;
    }

    public FlowSkip setNextNodeType(Integer nextNodeType) {
        this.nextNodeType = nextNodeType;
        return this;
    }

    public String getSkipType() {
        return skipType;
    }

    public FlowSkip setSkipType(String skipType) {
        this.skipType = skipType;
        return this;
    }

    public String getSkipCondition() {
        return skipCondition;
    }

    public FlowSkip setSkipCondition(String skipCondition) {
        this.skipCondition = skipCondition;
        return this;
    }

    public String getCoordinate() {
        return coordinate;
    }

    public FlowSkip setCoordinate(String coordinate) {
        this.coordinate = coordinate;
        return this;
    }

}
