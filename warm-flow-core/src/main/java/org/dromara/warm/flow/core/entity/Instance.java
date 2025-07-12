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

import org.dromara.warm.flow.core.FlowEngine;

import java.time.Instant;
import java.util.Map;

/**
 * 流程实例对象 flow_instance
 *
 * @author warm
 * @since 2023-03-29
 */
public interface Instance extends RootEntity {

    @Override
    Long getId();

    @Override
    Instance setId(Long id);

    @Override
    Instant getCreateTime();

    @Override
    Instance setCreateTime(Instant createTime);

    @Override
    Instant getUpdateTime();

    @Override
    Instance setUpdateTime(Instant updateTime);

    @Override
    String getTenantId();

    @Override
    Instance setTenantId(String tenantId);

    @Override
    String getDelFlag();

    @Override
    Instance setDelFlag(String delFlag);

    Long getDefinitionId();

    Instance setDefinitionId(Long definitionId);

    String getFlowName();

    Instance setFlowName(String flowName);

    String getBusinessId();

    Instance setBusinessId(String businessId);

    Integer getNodeType();

    Instance setNodeType(Integer nodeType);

    String getNodeCode();

    Instance setNodeCode(String nodeCode);

    String getNodeName();

    Instance setNodeName(String nodeName);

    String getVariable();

    Instance setVariable(String variable);

    default Map<String, Object> getVariableMap()  {
        return FlowEngine.jsonConvert.strToMap(getVariable());
    }

    String getFlowStatus();

    Instance setFlowStatus(String flowStatus);

    String getCreateBy();

    Instance setCreateBy(String createBy);

    String getFormCustom();

    Instance setFormCustom(String formCustom);

    String getFormPath();

    Instance setFormPath(String formPath);

    String getDefJson();

    Instance setDefJson(String defJson);

    String getExt();

    Instance setExt(String ext);

    Integer getActivityStatus();

    Instance setActivityStatus(Integer activityStatus);

}
