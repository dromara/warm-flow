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
    Long getId();

    @Override
    Instance setId(Long id);

    @Override
    Date getCreateTime();

    @Override
    Instance setCreateTime(Date createTime);

    @Override
    Date getUpdateTime();

    @Override
    Instance setUpdateTime(Date updateTime);

    @Override
    String getCreateBy();

    @Override
    Instance setCreateBy(String createBy);

    @Override
    String getUpdateBy();

    @Override
    Instance setUpdateBy(String updateBy);

    @Override
    String getTenantId();

    @Override
    Instance setTenantId(String tenantId);

    @Override
    String getDelFlag();

    @Override
    Instance setDelFlag(String delFlag);

    /**
     * flow_definition.id
     * @return flow_definition.id
     */
    Long getDefinitionId();

    Instance setDefinitionId(Long definitionId);

    /**
     * 流程名称
     * @return 流程名称
     */
    String getFlowName();

    Instance setFlowName(String flowName);

    /**
     * 业务ID
     * @return 业务ID
     */
    String getBusinessId();

    Instance setBusinessId(String businessId);

    /**
     * @see org.dromara.warm.flow.core.enums.NodeType
     * @return 节点类型
     */
    Integer getNodeType();

    Instance setNodeType(Integer nodeType);

    String getNodeCode();

    Instance setNodeCode(String nodeCode);

    /**
     * 流程节点名称
     * @return 节点名称
     */
    String getNodeName();

    Instance setNodeName(String nodeName);

    /**
     * 流程变量
     * @return 流程变量
     */
    String getVariable();

    Instance setVariable(String variable);

    default Map<String, Object> getVariableMap() {
        return FlowEngine.jsonConvert.strToMap(getVariable());
    }

    /**
     * @see org.dromara.warm.flow.core.enums.FlowStatus
     * @return 流程状态
     */
    String getFlowStatus();

    Instance setFlowStatus(String flowStatus);

    /**
     * 审批表单是否自定义（Y是 N否）
     * @return  （Y是 N否）
     */
    String getFormCustom();

    Instance setFormCustom(String formCustom);

    String getFormPath();

    Instance setFormPath(String formPath);

    String getDefJson();

    Instance setDefJson(String defJson);

    String getExt();

    Instance setExt(String ext);

    /**
     * @see org.dromara.warm.flow.core.enums.ActivityStatus
     * @return 激活状态
     */
    Integer getActivityStatus();

    Instance setActivityStatus(Integer activityStatus);

}
