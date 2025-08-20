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
    Long getId();

    @Override
    Task setId(Long id);

    @Override
    Date getCreateTime();

    @Override
    Task setCreateTime(Date createTime);

    @Override
    Date getUpdateTime();

    @Override
    Task setUpdateTime(Date updateTime);

    @Override
    String getTenantId();

    @Override
    Task setTenantId(String tenantId);

    @Override
    String getDelFlag();

    @Override
    Task setDelFlag(String delFlag);

    Long getDefinitionId();

    Task setDefinitionId(Long definitionId);

    Long getInstanceId();

    Task setInstanceId(Long instanceId);

    String getFlowName();

    Task setFlowName(String flowName);

    String getBusinessId();

    Task setBusinessId(String businessId);

    String getNodeCode();

    Task setNodeCode(String nodeCode);

    String getNodeName();

    Task setNodeName(String nodeName);

    Integer getNodeType();

    Task setNodeType(Integer nodeType);

    String getFlowStatus();

    Task setFlowStatus(String flowStatus);

    List<String> getPermissionList();

    Task setPermissionList(List<String> permissionList);

    List<User> getUserList();

    Task setUserList(List<User> userList);

    String getFormCustom();

    Task setFormCustom(String formCustom);

    String getFormPath();

    Task setFormPath(String formPath);
}
