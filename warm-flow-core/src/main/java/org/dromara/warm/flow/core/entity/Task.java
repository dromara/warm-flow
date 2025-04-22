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

import java.util.Date;
import java.util.List;

/**
 * 待办任务记录对象 flow_task
 *
 * @author warm
 * @since 2023-03-29
 */
public interface Task extends RootEntity {

    @Override
    public Long getId();

    @Override
    public Task setId(Long id);

    @Override
    public Date getCreateTime();

    @Override
    public Task setCreateTime(Date createTime);

    @Override
    public Date getUpdateTime();

    @Override
    public Task setUpdateTime(Date updateTime);

    @Override
    public String getTenantId();

    @Override
    public Task setTenantId(String tenantId);

    @Override
    public String getDelFlag();

    @Override
    public Task setDelFlag(String delFlag);

    public Long getDefinitionId();

    public Task setDefinitionId(Long definitionId);

    public Long getInstanceId();

    public Task setInstanceId(Long instanceId);

    public String getFlowName();

    public Task setFlowName(String flowName);

    public String getBusinessId();

    public Task setBusinessId(String businessId);

    public String getNodeCode();

    public Task setNodeCode(String nodeCode);

    public String getNodeName();

    public Task setNodeName(String nodeName);

    public Integer getNodeType();

    public Task setNodeType(Integer nodeType);

    public String getFlowStatus();

    public Task setFlowStatus(String flowStatus);

    public List<String> getPermissionList();

    public Task setPermissionList(List<String> permissionList);

    public List<User> getUserList();

    public Task setUserList(List<User> userList);

    public String getFormCustom();

    public Task setFormCustom(String formCustom);

    public String getFormPath();

    public Task setFormPath(String formPath);
}
