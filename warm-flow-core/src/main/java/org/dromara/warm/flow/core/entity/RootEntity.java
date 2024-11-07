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

import java.io.Serializable;
import java.util.Date;

/**
 * 流程基础entity
 *
 * @author warm
 * @since 2023/5/17 17:23
 */
public interface RootEntity extends Serializable {

    public Long getId();

    public RootEntity setId(Long id);

    public Date getCreateTime();

    public RootEntity setCreateTime(Date createTime);

    public Date getUpdateTime();

    public RootEntity setUpdateTime(Date updateTime);

    public String getTenantId();

    public RootEntity setTenantId(String tenantId);

    public String getDelFlag();

    public RootEntity setDelFlag(String delFlag);

}
