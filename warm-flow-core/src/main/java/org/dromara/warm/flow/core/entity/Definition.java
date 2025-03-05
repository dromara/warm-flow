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
import java.util.List;

/**
 * 流程定义对象 flow_definition
 *
 * @author warm
 * @since 2023-03-29
 */
public interface Definition extends RootEntity {
    @Override
    public Long getId();

    @Override
    public Definition setId(Long id);

    @Override
    public Date getCreateTime();

    @Override
    public Definition setCreateTime(Date createTime);

    @Override
    public Date getUpdateTime();

    @Override
    public Definition setUpdateTime(Date updateTime);

    @Override
    public String getTenantId();

    @Override
    public Definition setTenantId(String tenantId);

    @Override
    public String getDelFlag();

    @Override
    public Definition setDelFlag(String delFlag);

    public String getFlowCode();

    public Definition setFlowCode(String flowCode);

    public String getFlowName();

    public Definition setFlowName(String flowName);

    public String getCategory();

    public Definition setCategory(String category);

    public String getVersion();

    public Definition setVersion(String version);

    public Integer getIsPublish();

    public Definition setIsPublish(Integer isPublish);

    public String getFormCustom();

    public Definition setFormCustom(String formCustom);

    public String getFormPath();

    public Definition setFormPath(String formPath);

    public String getExt();

    public Definition setExt(String ext);

    public List<Node> getNodeList();

    public Definition setNodeList(List<Node> nodeList);

    public List<User> getUserList();

    public Definition setUserList(List<User> userList);

    public Integer getActivityStatus();

    public Definition setActivityStatus(Integer activityStatus);

    String getListenerType();

    Definition setListenerType(String listenerType);

    String getListenerPath();

    Definition setListenerPath(String listenerPath);

    default Definition copy() {
        return FlowEngine.newDef()
                .setId(this.getId())
                .setCreateTime(this.getCreateTime())
                .setUpdateTime(this.getUpdateTime())
                .setTenantId(this.getTenantId())
                .setDelFlag(this.getDelFlag())
                .setFlowCode(this.getFlowCode())
                .setFlowName(this.getFlowName())
                .setCategory(this.getCategory())
                .setVersion(this.getVersion())
                .setIsPublish(this.getIsPublish())
                .setFormCustom(this.getFormCustom())
                .setFormPath(this.getFormPath())
                .setActivityStatus(this.getActivityStatus())
                .setListenerType(this.getListenerType())
                .setListenerPath(this.getListenerPath())
                .setExt(this.getExt());

    }
}
