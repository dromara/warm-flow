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
    public Long getId();

    @Override
    public HisTask setId(Long id);

    @Override
    public Date getCreateTime();

    @Override
    public HisTask setCreateTime(Date createTime);

    @Override
    public Date getUpdateTime();

    @Override
    public HisTask setUpdateTime(Date updateTime);

    @Override
    public String getTenantId();

    @Override
    public HisTask setTenantId(String tenantId);

    @Override
    public String getDelFlag();

    @Override
    public HisTask setDelFlag(String delFlag);

    public Long getDefinitionId();

    public HisTask setDefinitionId(Long definitionId);

    public String getFlowName();

    public HisTask setFlowName(String flowName);

    public Long getInstanceId();

    public HisTask setInstanceId(Long instanceId);

    public Integer getCooperateType();

    public HisTask setCooperateType(Integer cooperateType);

    public Long getTaskId();

    public HisTask setTaskId(Long taskId);

    public String getBusinessId();

    public HisTask setBusinessId(String businessId);

    public String getNodeCode();

    public HisTask setNodeCode(String nodeCode);

    public String getNodeName();

    public HisTask setNodeName(String nodeName);

    public Integer getNodeType();

    public HisTask setNodeType(Integer nodeType);

    public String getTargetNodeCode();

    public HisTask setTargetNodeCode(String targetNodeCode);

    public String getTargetNodeName();

    public HisTask setTargetNodeName(String targetNodeName);

    public String getApprover();

    public HisTask setApprover(String approver);

    public String getCollaborator();

    public HisTask setCollaborator(String collaborator);

    public List<String> getPermissionList();

    public HisTask setPermissionList(List<String> permissionList);

    public String getSkipType();

    public HisTask setSkipType(String skipType);

    public String getFlowStatus();

    public HisTask setFlowStatus(String flowStatus);

    public String getMessage();

    public HisTask setMessage(String message);

    public String getVariable();

    public HisTask setVariable(String variable);

    default public Map<String, Object> getVariableMap() {
        return FlowEngine.jsonConvert.strToMap(this.getVariable());
    }

    public String getExt();

    public HisTask setExt(String ext);

    public String getCreateBy();

    public HisTask setCreateBy(String createBy);

    public String getFormCustom();

    public HisTask setFormCustom(String formCustom);

    public String getFormPath();

    public HisTask setFormPath(String formPath);

}
