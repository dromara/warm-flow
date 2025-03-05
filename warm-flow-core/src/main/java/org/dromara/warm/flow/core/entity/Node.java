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
    public Long getId();

    @Override
    public Node setId(Long id);

    @Override
    public Date getCreateTime();

    @Override
    public Node setCreateTime(Date createTime);

    @Override
    public Date getUpdateTime();

    @Override
    public Node setUpdateTime(Date updateTime);

    @Override
    public String getTenantId();

    @Override
    public Node setTenantId(String tenantId);

    @Override
    public String getDelFlag();

    @Override
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

    public String getPermissionFlag();

    public Node setPermissionFlag(String permissionFlag);

    public String getCoordinate();

    public Node setCoordinate(String coordinate);

    public String getAnyNodeSkip();

    public Node setAnyNodeSkip(String anyNodeSkip);

    public String getListenerType();

    public Node setListenerType(String listenerType);

    public String getListenerPath();

    public Node setListenerPath(String listenerPath);

    public String getHandlerType();

    public Node setHandlerType(String listenerType);

    public String getHandlerPath();

    public Node setHandlerPath(String listenerPath);

    public String getFormCustom();

    public Node setFormCustom(String formCustom);

    public String getFormPath();

    public Node setFormPath(String formPath);

    public String getExt();

    public Node setExt(String ext);

    /**
     * @deprecated 下个版本废弃
     */
    @Deprecated
    public String getVersion();

    /**
     * @deprecated 下个版本废弃
     */
    @Deprecated
    public Node setVersion(String version);

    public List<Skip> getSkipList();

    public Node setSkipList(List<Skip> skipList);

    default Node copy() {
        return FlowEngine.newNode()
                .setId(this.getId())
                .setCreateTime(this.getCreateTime())
                .setUpdateTime(this.getUpdateTime())
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
