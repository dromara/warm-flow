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
package org.dromara.warm.flow.orm.entity;

import lombok.Data;
import lombok.experimental.Accessors;
import org.dromara.warm.flow.core.entity.HisTask;

import java.util.Date;
import java.util.List;

@Data
@Accessors(chain = true)
public class FlowHisTask implements HisTask {

    private Long id;

    private Date createTime;

    private Date updateTime;

    private String tenantId;

    private String delFlag;

    private Long definitionId;

    private String flowName;

    private Long instanceId;

    private Long taskId;

    private Integer cooperateType;

    private String businessId;

    private String nodeCode;

    private String nodeName;

    private Integer nodeType;

    private String targetNodeCode;

    private String targetNodeName;

    private String approver;

    private String collaborator;

    private String skipType;

    private String flowStatus;

    private String message;

    private String variable;

    private String ext;

    private String formCustom;

    private String formPath;

    private List<String> permissionList;

}
