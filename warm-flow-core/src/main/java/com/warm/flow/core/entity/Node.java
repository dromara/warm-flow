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
package com.warm.flow.core.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 流程节点对象 flow_node
 *
 * @author warm
 * @date 2023-03-29
 */
public interface Node extends RootEntity {

    public Long getId();

    public Node setId(Long id);

    public Date getCreateTime();

    public Node setCreateTime(Date createTime);

    public Date getUpdateTime();

    public Node setUpdateTime(Date updateTime);

    public String getTenantId();

    public Node setTenantId(String tenantId);

    public String getDelFlag();

    public Node setDelFlag(String delFlag);

    public Integer getNodeType();

    public Node setNodeType(Integer nodeType);

    public Long getDefinitionId();

    public Node setDefinitionId(Long definitionId);

    public String getNodeCode();

    public Node setNodeCode(String nodeCode);

    public String getNodeName();

    public Node setNodeName(String nodeName);

    public BigDecimal getNodeRatio();

    public Node setNodeRatio(BigDecimal nodeRatio);

    public List<String> getDynamicPermissionFlagList();

    public Node setDynamicPermissionFlagList(List<String> permissionFlagList);

    public String getPermissionFlag();

    public Node setPermissionFlag(String permissionFlag);

    public String getCoordinate();

    public Node setCoordinate(String coordinate);

    public String getSkipAnyNode();

    public Node setSkipAnyNode(String skipAnyNode);

    String getListenerType();

    Node setListenerType(String listenerType);

    String getListenerPath();

    Node setListenerPath(String listenerPath);

    String getHandlerType();

    Node setHandlerType(String listenerType);

    String getHandlerPath();

    Node setHandlerPath(String listenerPath);

    public String getVersion();

    public Node setVersion(String version);

    public List<Skip> getSkipList();

    public Node setSkipList(List<Skip> skipList);
}
