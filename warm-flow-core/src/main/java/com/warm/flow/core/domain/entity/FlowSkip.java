package com.warm.flow.core.domain.entity;

import com.warm.mybatis.core.entity.WarmEntity;

import java.util.Date;

/**
 * 结点跳转关联对象 flow_skip
 *
 * @author warm
 * @date 2023-03-29
 */
public class FlowSkip implements WarmEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 流程id
     */
    private Long definitionId;

    /**
     * 结点id
     */
    private Long nodeId;

    /**
     * 当前流程结点的编码
     */
    private String nowNodeCode;

    /**
     * 下一个流程结点的编码
     */
    private String nextNodeCode;

    /**
     * 跳转类型（PASS审批通过 REJECT驳回）
     */
    private String skipType;

    /**
     * 跳转条件
     */
    private String skipCondition;


    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getDefinitionId() {
        return definitionId;
    }

    public void setDefinitionId(Long definitionId) {
        this.definitionId = definitionId;
    }

    public Long getNodeId() {
        return nodeId;
    }

    public void setNodeId(Long nodeId) {
        this.nodeId = nodeId;
    }

    public String getNowNodeCode() {
        return nowNodeCode;
    }

    public void setNowNodeCode(String nowNodeCode) {
        this.nowNodeCode = nowNodeCode;
    }

    public String getNextNodeCode() {
        return nextNodeCode;
    }

    public void setNextNodeCode(String nextNodeCode) {
        this.nextNodeCode = nextNodeCode;
    }

    public String getSkipType() {
        return skipType;
    }

    public void setSkipType(String skipType) {
        this.skipType = skipType;
    }

    public String getSkipCondition() {
        return skipCondition;
    }

    public void setSkipCondition(String skipCondition) {
        this.skipCondition = skipCondition;
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
}
