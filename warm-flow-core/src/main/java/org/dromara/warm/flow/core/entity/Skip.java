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
    Long getId();

    @Override
    Skip setId(Long id);

    @Override
    Date getCreateTime();

    @Override
    Skip setCreateTime(Date createTime);

    @Override
    Date getUpdateTime();

    @Override
    Skip setUpdateTime(Date updateTime);

    @Override
    String getCreateBy();

    @Override
    Skip setCreateBy(String createBy);

    @Override
    String getUpdateBy();

    @Override
    Skip setUpdateBy(String updateBy);

    @Override
    String getTenantId();

    @Override
    Skip setTenantId(String tenantId);

    @Override
    String getDelFlag();

    @Override
    Skip setDelFlag(String delFlag);

    Long getDefinitionId();

    Skip setDefinitionId(Long definitionId);

    Long getNodeId();

    Skip setNodeId(Long nodeId);

    String getNowNodeCode();

    Skip setNowNodeCode(String nowNodeCode);

    Integer getNowNodeType();

    Skip setNowNodeType(Integer nowNodeType);

    String getNextNodeCode();

    Skip setNextNodeCode(String nextNodeCode);

    Integer getNextNodeType();

    Skip setNextNodeType(Integer nextNodeType);

    String getSkipName();

    Skip setSkipName(String skipName);

    String getSkipType();

    Skip setSkipType(String skipType);

    String getSkipCondition();

    Skip setSkipCondition(String skipCondition);

    String getCoordinate();

    Skip setCoordinate(String coordinate);

    default Skip copy() {
        return FlowEngine.newSkip()
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
