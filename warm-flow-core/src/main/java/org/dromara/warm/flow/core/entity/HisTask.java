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
import java.util.List;
import java.util.Map;

/**
 * 历史任务记录对象 flow_his_task
 *
 * @author warm
 * @since 2023-03-29
 */
public interface HisTask extends RootEntity {

    @Override
    Long getId();

    @Override
    HisTask setId(Long id);

    @Override
    Instant getCreateTime();

    @Override
    HisTask setCreateTime(Instant createTime);

    @Override
    Instant getUpdateTime();

    @Override
    HisTask setUpdateTime(Instant updateTime);

    @Override
    String getTenantId();

    @Override
    HisTask setTenantId(String tenantId);

    @Override
    String getDelFlag();

    @Override
    HisTask setDelFlag(String delFlag);

    Long getDefinitionId();

    HisTask setDefinitionId(Long definitionId);

    String getFlowName();

    HisTask setFlowName(String flowName);

    Long getInstanceId();

    HisTask setInstanceId(Long instanceId);

    Integer getCooperateType();

    HisTask setCooperateType(Integer cooperateType);

    Long getTaskId();

    HisTask setTaskId(Long taskId);

    String getBusinessId();

    HisTask setBusinessId(String businessId);

    String getNodeCode();

    HisTask setNodeCode(String nodeCode);

    String getNodeName();

    HisTask setNodeName(String nodeName);

    Integer getNodeType();

    HisTask setNodeType(Integer nodeType);

    String getTargetNodeCode();

    HisTask setTargetNodeCode(String targetNodeCode);

    String getTargetNodeName();

    HisTask setTargetNodeName(String targetNodeName);

    String getApprover();

    HisTask setApprover(String approver);

    String getCollaborator();

    HisTask setCollaborator(String collaborator);

    List<String> getPermissionList();

    HisTask setPermissionList(List<String> permissionList);

    String getSkipType();

    HisTask setSkipType(String skipType);

    String getFlowStatus();

    HisTask setFlowStatus(String flowStatus);

    String getMessage();

    HisTask setMessage(String message);

    String getVariable();

    HisTask setVariable(String variable);

    default Map<String, Object> getVariableMap() {
        return FlowEngine.jsonConvert.strToMap(this.getVariable());
    }

    String getExt();

    HisTask setExt(String ext);

    String getCreateBy();

    HisTask setCreateBy(String createBy);

    String getFormCustom();

    HisTask setFormCustom(String formCustom);

    String getFormPath();

    HisTask setFormPath(String formPath);

}
