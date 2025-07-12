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

import java.time.Instant;

/**
 * 流程用户 flow_user
 *
 * @author xiarg
 * @since 2024/5/10 10:41
 */
public interface User extends RootEntity {

    @Override
    Long getId();

    @Override
    User setId(Long id);

    @Override
    Instant getCreateTime();

    @Override
    User setCreateTime(Instant createTime);

    @Override
    Instant getUpdateTime();

    @Override
    User setUpdateTime(Instant updateTime);

    @Override
    String getTenantId();

    @Override
    User setTenantId(String tenantId);

    @Override
    String getDelFlag();

    @Override
    User setDelFlag(String delFlag);

    String getCreateBy();

    User setCreateBy(String createBy);

    String getType();

    User setType(String type);

    String getProcessedBy();

    User setProcessedBy(String processedBy);

    Long getAssociated();

    User setAssociated(Long associated);
}
