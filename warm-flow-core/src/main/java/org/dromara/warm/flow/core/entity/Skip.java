/*
 *    Copyright 2024-2025, Warm-Flow (290631660@qq.com).
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.dromara.warm.flow.core.entity;

import org.dromara.warm.flow.core.FlowEngine;

import java.util.Date;

/**
 * 节点跳转关联对象 flow_skip
 *
 * @author warm
 * @since 2023-03-29
 */
public interface Skip extends RootEntity {

    @Override
    public Long getId();

    @Override
    public Skip setId(Long id);

    @Override
    public Date getCreateTime();

    @Override
    public Skip setCreateTime(Date createTime);

    @Override
    public Date getUpdateTime();

    @Override
    public Skip setUpdateTime(Date updateTime);

    @Override
    public String getTenantId();

    @Override
    public Skip setTenantId(String tenantId);

    @Override
    public String getDelFlag();

    @Override
    public Skip setDelFlag(String delFlag);

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
    default Skip copy() {
        return FlowEngine.newSkip()
                .setId(getId())
                .setCreateTime(getCreateTime())
                .setUpdateTime(getUpdateTime())
                .setTenantId(getTenantId())
                .setDelFlag(getDelFlag())
                .setDefinitionId(getDefinitionId())
                .setNowNodeCode(getNowNodeCode())
                .setNowNodeType(getNowNodeType())
                .setNextNodeCode(getNextNodeCode())
                .setNextNodeType(getNextNodeType())
                .setSkipName(getSkipName())
                .setSkipType(getSkipType())
                .setSkipCondition(getSkipCondition())
                .setCoordinate(getCoordinate());
    }

}
