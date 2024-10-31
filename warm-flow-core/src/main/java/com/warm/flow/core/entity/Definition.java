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

import com.warm.flow.core.FlowFactory;

import java.util.Date;
import java.util.List;

/**
 * 流程定义对象 flow_definition
 *
 * @author warm
 * @date 2023-03-29
 */
public interface Definition extends RootEntity {
    public Long getId();

    public Definition setId(Long id);

    public Date getCreateTime();

    public Definition setCreateTime(Date createTime);

    public Date getUpdateTime();

    public Definition setUpdateTime(Date updateTime);

    public String getTenantId();

    public Definition setTenantId(String tenantId);

    public String getDelFlag();

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

    public String getXmlString();

    public Definition setXmlString(String xmsString);

    public Integer getActivityStatus();

    public Definition setActivityStatus(Integer activityStatus);

    String getListenerType();

    Definition setListenerType(String listenerType);

    String getListenerPath();

    Definition setListenerPath(String listenerPath);

    default Definition copy() {
        Definition definition = FlowFactory.newDef();
        definition.setId(this.getId());
        definition.setCreateTime(this.getCreateTime());
        definition.setUpdateTime(this.getUpdateTime());
        definition.setTenantId(this.getTenantId());
        definition.setDelFlag(this.getDelFlag());
        definition.setFlowCode(this.getFlowCode());
        definition.setFlowName(this.getFlowName());
        definition.setCategory(this.getCategory());
        definition.setVersion(this.getVersion());
        definition.setIsPublish(this.getIsPublish());
        definition.setFormCustom(this.getFormCustom());
        definition.setFormPath(this.getFormPath());
        definition.setActivityStatus(this.getActivityStatus());
        definition.setListenerType(this.getListenerType());
        definition.setListenerPath(this.getListenerPath());
        definition.setExt(this.getExt());

        return definition;
    }
}
