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

import java.util.Date;

/**
 * 流程用户 flow_user
 *
 * @author xiarg
 * @date 2024/5/10 10:41
 */
public interface User extends RootEntity {

    public Long getId();

    public User setId(Long id);

    public Date getCreateTime();

    public User setCreateTime(Date createTime);

    public String getCreateBy();

    public User setCreateBy(String createBy);

    public Date getUpdateTime();

    public User setUpdateTime(Date updateTime);

    public String getTenantId();

    public User setTenantId(String tenantId);

    public String getDelFlag();

    public User setDelFlag(String delFlag);

    public String getType();

    public User setType(String type);

    public String getProcessedBy();

    public User setProcessedBy(String processedBy);

    public Long getAssociated();

    public User setAssociated(Long associated);
}