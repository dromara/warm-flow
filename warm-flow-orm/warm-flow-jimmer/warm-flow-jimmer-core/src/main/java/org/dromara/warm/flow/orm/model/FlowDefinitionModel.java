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
package org.dromara.warm.flow.orm.model;

import org.babyfish.jimmer.sql.Column;
import org.babyfish.jimmer.sql.Entity;
import org.babyfish.jimmer.sql.Id;
import org.babyfish.jimmer.sql.Table;

import java.util.Date;

@Entity
@Table(name = "flow_definition")
public interface FlowDefinitionModel {

    @Id
    long id();

    @Column(name = "create_time")
    @org.jetbrains.annotations.Nullable
    Date createTime();

    @Column(name = "update_time")
    @org.jetbrains.annotations.Nullable
    Date updateTime();

    @Column(name = "create_by")
    @org.jetbrains.annotations.Nullable
    String createBy();

    @Column(name = "update_by")
    @org.jetbrains.annotations.Nullable
    String updateBy();

    @Column(name = "tenant_id")
    @org.jetbrains.annotations.Nullable
    String tenantId();

    @Column(name = "del_flag")
    @org.jetbrains.annotations.Nullable
    String delFlag();

    @Column(name = "flow_code")
    String flowCode();

    @Column(name = "flow_name")
    String flowName();

    @Column(name = "model_value")
    String modelValue();

    @org.jetbrains.annotations.Nullable
    String category();

    @Column(name = "\"version\"")
    String version();

    @Column(name = "is_publish")
    int publishStatus();

    @Column(name = "form_custom")
    @org.jetbrains.annotations.Nullable
    String formCustom();

    @Column(name = "form_path")
    @org.jetbrains.annotations.Nullable
    String formPath();

    @Column(name = "activity_status")
    int activityStatus();

    @Column(name = "listener_type")
    @org.jetbrains.annotations.Nullable
    String listenerType();

    @Column(name = "listener_path")
    @org.jetbrains.annotations.Nullable
    String listenerPath();

    @org.jetbrains.annotations.Nullable
    String ext();

}
