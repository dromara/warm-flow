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

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 流程节点对象 flow_node
 *
 * @author warm
 * @since 2023-03-29
 */
public interface Node extends RootEntity {

    @Override
    Long getId();

    @Override
    Node setId(Long id);

    @Override
    Date getCreateTime();

    @Override
    Node setCreateTime(Date createTime);

    @Override
    Date getUpdateTime();

    @Override
    Node setUpdateTime(Date updateTime);

    @Override
    String getTenantId();

    @Override
    Node setTenantId(String tenantId);

    @Override
    String getDelFlag();

    @Override
    Node setDelFlag(String delFlag);

    Integer getNodeType();

    Node setNodeType(Integer nodeType);

    Long getDefinitionId();

    Node setDefinitionId(Long definitionId);

    String getNodeCode();

    Node setNodeCode(String nodeCode);

    String getNodeName();

    Node setNodeName(String nodeName);

    BigDecimal getNodeRatio();

    Node setNodeRatio(BigDecimal nodeRatio);

    String getPermissionFlag();

    Node setPermissionFlag(String permissionFlag);

    String getCoordinate();

    Node setCoordinate(String coordinate);

    String getAnyNodeSkip();

    Node setAnyNodeSkip(String anyNodeSkip);

    String getListenerType();

    Node setListenerType(String listenerType);

    String getListenerPath();

    Node setListenerPath(String listenerPath);

    String getHandlerType();

    Node setHandlerType(String listenerType);

    String getHandlerPath();

    Node setHandlerPath(String listenerPath);

    String getFormCustom();

    Node setFormCustom(String formCustom);

    String getFormPath();

    Node setFormPath(String formPath);

    String getExt();

    Node setExt(String ext);

    /**
     * @deprecated 下个版本废弃
     */
    @Deprecated
    String getVersion();

    /**
     * @deprecated 下个版本废弃
     */
    @Deprecated
    Node setVersion(String version);

    List<Skip> getSkipList();

    Node setSkipList(List<Skip> skipList);

    default Node copy() {
        return FlowEngine.newNode()
                .setTenantId(this.getTenantId())
                .setDelFlag(this.getDelFlag())
                .setNodeType(this.getNodeType())
                .setDefinitionId(this.getDefinitionId())
                .setNodeCode(this.getNodeCode())
                .setNodeName(this.getNodeName())
                .setNodeRatio(this.getNodeRatio())
                .setPermissionFlag(this.getPermissionFlag())
                .setCoordinate(this.getCoordinate())
                .setVersion(this.getVersion())
                .setAnyNodeSkip(this.getAnyNodeSkip())
                .setListenerType(this.getListenerType())
                .setListenerPath(this.getListenerPath())
                .setHandlerType(this.getHandlerType())
                .setHandlerPath(this.getHandlerPath())
                .setFormCustom(this.getFormCustom())
                .setFormPath(this.getFormPath())
                .setExt(this.getExt());
    }
}
