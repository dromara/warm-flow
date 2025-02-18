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

/**
 * 流程用户 flow_user
 *
 * @author xiarg
 * @since 2024/5/10 10:41
 */
public interface User extends RootEntity {

    @Override
    public Long getId();

    @Override
    public User setId(Long id);

    @Override
    public Date getCreateTime();

    @Override
    public User setCreateTime(Date createTime);

    @Override
    public Date getUpdateTime();

    @Override
    public User setUpdateTime(Date updateTime);

    @Override
    public String getTenantId();

    @Override
    public User setTenantId(String tenantId);

    @Override
    public String getDelFlag();

    @Override
    public User setDelFlag(String delFlag);

    public String getCreateBy();

    public User setCreateBy(String createBy);

    public String getType();

    public User setType(String type);

    public String getProcessedBy();

    public User setProcessedBy(String processedBy);

    public Long getAssociated();

    public User setAssociated(Long associated);
}
