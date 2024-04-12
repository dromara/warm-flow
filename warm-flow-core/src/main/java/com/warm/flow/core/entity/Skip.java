package com.warm.flow.core.entity;

import java.util.Date;

/**
 * 节点跳转关联对象 flow_skip
 *
 * @author warm
 * @date 2023-03-29
 */
public interface Skip extends RootEntity {

    public Long getId();

    public Skip setId(Long id);

    public Date getCreateTime();

    public Skip setCreateTime(Date createTime);

    public Date getUpdateTime();

    public Skip setUpdateTime(Date updateTime);

    public Long getDefinitionId();

    public Skip setDefinitionId(Long definitionId);

    public Long getNodeId();

    public Skip setNodeId(Long nodeId);

    public String getNowNodeCode();

    public Skip setNowNodeCode(String nowNodeCode);

    public Integer getNowNodeType();

    public Skip setNowNodeType(Integer nowNodeType);

    public String getNextNodeCode();

    public Skip setNextNodeCode(String nextNodeCode);

    public Integer getNextNodeType();

    public Skip setNextNodeType(Integer nextNodeType);

    public String getSkipName();

    public Skip setSkipName(String skipName);

    public String getSkipType();

    public Skip setSkipType(String skipType);

    public String getSkipCondition();

    public Skip setSkipCondition(String skipCondition);

    public String getCoordinate();

    public Skip setCoordinate(String coordinate);

}
