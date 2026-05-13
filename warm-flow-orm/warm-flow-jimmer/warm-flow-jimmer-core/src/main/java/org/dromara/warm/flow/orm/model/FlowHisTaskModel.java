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
@Table(name = "flow_his_task")
public interface FlowHisTaskModel {

    @Id
    long id();

    @Column(name = "create_time")
    @org.jetbrains.annotations.Nullable
    Date createTime();

    @Column(name = "update_time")
    @org.jetbrains.annotations.Nullable
    Date updateTime();

    @Column(name = "tenant_id")
    @org.jetbrains.annotations.Nullable
    String tenantId();

    @Column(name = "del_flag")
    @org.jetbrains.annotations.Nullable
    String delFlag();

    @Column(name = "definition_id")
    @org.jetbrains.annotations.Nullable
    Long definitionId();

    @Column(name = "instance_id")
    @org.jetbrains.annotations.Nullable
    Long instanceId();

    @Column(name = "task_id")
    @org.jetbrains.annotations.Nullable
    Long taskId();

    @Column(name = "cooperate_type")
    @org.jetbrains.annotations.Nullable
    Integer cooperateType();

    @Column(name = "node_code")
    @org.jetbrains.annotations.Nullable
    String nodeCode();

    @Column(name = "node_name")
    @org.jetbrains.annotations.Nullable
    String nodeName();

    @Column(name = "node_type")
    @org.jetbrains.annotations.Nullable
    Integer nodeType();

    @Column(name = "target_node_code")
    @org.jetbrains.annotations.Nullable
    String targetNodeCode();

    @Column(name = "target_node_name")
    @org.jetbrains.annotations.Nullable
    String targetNodeName();

    @org.jetbrains.annotations.Nullable
    String approver();

    @org.jetbrains.annotations.Nullable
    String collaborator();

    @Column(name = "skip_type")
    @org.jetbrains.annotations.Nullable
    String skipType();

    @Column(name = "flow_status")
    @org.jetbrains.annotations.Nullable
    String flowStatus();

    @org.jetbrains.annotations.Nullable
    String message();

    @org.jetbrains.annotations.Nullable
    String variable();

    @org.jetbrains.annotations.Nullable
    String ext();

    @Column(name = "form_custom")
    @org.jetbrains.annotations.Nullable
    String formCustom();

    @Column(name = "form_path")
    @org.jetbrains.annotations.Nullable
    String formPath();

}
