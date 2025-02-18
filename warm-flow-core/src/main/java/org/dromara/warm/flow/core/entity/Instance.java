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

import java.util.Date;
import java.util.Map;

/**
 * 流程实例对象 flow_instance
 *
 * @author warm
 * @since 2023-03-29
 */
public interface Instance extends RootEntity {

    @Override
    public Long getId();

    @Override
    public Instance setId(Long id);

    @Override
    public Date getCreateTime();

    @Override
    public Instance setCreateTime(Date createTime);

    @Override
    public Date getUpdateTime();

    @Override
    public Instance setUpdateTime(Date updateTime);

    @Override
    public String getTenantId();

    @Override
    public Instance setTenantId(String tenantId);

    @Override
    public String getDelFlag();

    @Override
    public Instance setDelFlag(String delFlag);

    public Long getDefinitionId();

    public Instance setDefinitionId(Long definitionId);

    public String getFlowName();

    public Instance setFlowName(String flowName);

    public String getBusinessId();

    public Instance setBusinessId(String businessId);

    public Integer getNodeType();

    public Instance setNodeType(Integer nodeType);

    public String getNodeCode();

    public Instance setNodeCode(String nodeCode);

    public String getNodeName();

    public Instance setNodeName(String nodeName);

    String getVariable();

    Instance setVariable(String variable);

    default Map<String, Object> getVariableMap()  {
        return FlowEngine.jsonConvert.strToMap(getVariable());
    }

    public String getFlowStatus();

    public Instance setFlowStatus(String flowStatus);

    public String getCreateBy();

    public Instance setCreateBy(String createBy);

    public String getFormCustom();

    public Instance setFormCustom(String formCustom);

    public String getFormPath();

    public Instance setFormPath(String formPath);

    public String getDefJson();

    public Instance setDefJson(String defJson);

    public String getExt();

    public Instance setExt(String ext);

    public Integer getActivityStatus();

    public Instance setActivityStatus(Integer activityStatus);

}
