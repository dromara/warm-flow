package com.warm.flow.core.domain.entity;

import com.warm.mybatis.core.entity.FlowEntity;

import java.util.Date;

/**
 * 结点跳转关联对象 flow_skip
 *
 * @author hh
 * @date 2023-03-29
 */
public class FlowSkip implements FlowEntity {
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
     * 跳转条件(非空情况下才做条件校验)
     */
    private String conditionValue;

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

    public String getConditionValue() {
        return conditionValue;
    }

    public void setConditionValue(String conditionValue) {
        this.conditionValue = conditionValue;
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
