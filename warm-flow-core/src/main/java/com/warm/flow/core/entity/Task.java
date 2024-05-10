package com.warm.flow.core.entity;

import java.util.Date;
import java.util.List;

/**
 * 待办任务记录对象 flow_task
 *
 * @author warm
 * @date 2023-03-29
 */
public interface Task extends RootEntity {

    public Long getId();

    public Task setId(Long id);

    public Date getCreateTime();

    public Task setCreateTime(Date createTime);

    public Date getUpdateTime();

    public Task setUpdateTime(Date updateTime);

    public String getTenantId();

    public Task setTenantId(String tenantId);

    public String getDelFlag();

    public Task setDelFlag(String delFlag);

    public Long getDefinitionId();

    public Task setDefinitionId(Long definitionId);

    public Long getInstanceId();

    public Task setInstanceId(Long instanceId);

    public String getFlowName();

    public Task setFlowName(String flowName);

    public String getBusinessId();

    public Task setBusinessId(String businessId);

    public String getNodeCode();

    public Task setNodeCode(String nodeCode);

    public String getNodeName();

    public Task setNodeName(String nodeName);

    public Integer getNodeType();

    public Task setNodeType(Integer nodeType);

    public Integer getFlowStatus();

    public Task setFlowStatus(Integer flowStatus);

    public List<String> getPermissionList();

    public Task setPermissionList(List<String> permissionList);

    public String getFromCustom();

    public Task setFromCustom(String fromCustom);

    public String getFromPath();

    public Task setFromPath(String fromPath);
}
