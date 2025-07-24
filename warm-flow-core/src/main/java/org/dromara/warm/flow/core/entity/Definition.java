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

import java.time.Instant;
import java.util.List;

/**
 * 流程定义对象 flow_definition
 *
 * @author warm
 * @since 2023-03-29
 */
public interface Definition extends RootEntity {

    @Override
    Long getId();

    @Override
    Definition setId(Long id);

    @Override
    Instant getCreateTime();

    @Override
    Definition setCreateTime(Instant createTime);

    @Override
    Instant getUpdateTime();

    @Override
    Definition setUpdateTime(Instant updateTime);

    @Override
    String getTenantId();

    @Override
    Definition setTenantId(String tenantId);

    @Override
    String getDelFlag();

    @Override
    Definition setDelFlag(String delFlag);

    String getFlowCode();

    Definition setFlowCode(String flowCode);

    String getFlowName();

    public String getModelValue();

    public Definition setModelValue(String modelValue);

    Definition setMode(String mode);

    String getCategory();

    Definition setCategory(String category);

    String getVersion();

    Definition setVersion(String version);

    Integer getIsPublish();

    Definition setIsPublish(Integer isPublish);

    String getFormCustom();

    Definition setFormCustom(String formCustom);

    String getFormPath();

    Definition setFormPath(String formPath);

    String getExt();

    Definition setExt(String ext);

    List<Node> getNodeList();

    Definition setNodeList(List<Node> nodeList);

    List<User> getUserList();

    Definition setUserList(List<User> userList);

    Integer getActivityStatus();

    Definition setActivityStatus(Integer activityStatus);

    String getListenerType();

    Definition setListenerType(String listenerType);

    String getListenerPath();

    Definition setListenerPath(String listenerPath);

    default Definition copy() {
        return FlowEngine.newDef()
                .setTenantId(this.getTenantId())
                .setDelFlag(this.getDelFlag())
                .setFlowCode(this.getFlowCode())
                .setFlowName(this.getFlowName())
                .setModelValue(this.getModelValue())
                .setCategory(this.getCategory())
                .setVersion(this.getVersion())
                .setFormCustom(this.getFormCustom())
                .setFormPath(this.getFormPath())
                .setListenerType(this.getListenerType())
                .setListenerPath(this.getListenerPath())
                .setExt(this.getExt());

    }
}
