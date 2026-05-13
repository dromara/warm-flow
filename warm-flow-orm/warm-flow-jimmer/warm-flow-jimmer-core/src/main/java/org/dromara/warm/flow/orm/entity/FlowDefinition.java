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
import org.dromara.warm.flow.core.entity.Definition;
import org.dromara.warm.flow.core.entity.Node;
import org.dromara.warm.flow.core.entity.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Accessors(chain = true)
public class FlowDefinition implements Definition {

    private Long id;

    private Date createTime;

    private Date updateTime;

    private String createBy;

    private String updateBy;

    private String tenantId;

    private String delFlag;

    private String flowCode;

    private String flowName;

    private String modelValue;

    private String category;

    private String version;

    private Integer isPublish;

    private String formCustom;

    private String formPath;

    private Integer activityStatus;

    private String listenerType;

    private String listenerPath;

    private String ext;

    private List<Node> nodeList = new ArrayList<>();

    private List<User> userList = new ArrayList<>();

}
