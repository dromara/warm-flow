package com.warm.flow.core.entity;

import java.util.Date;
import java.util.List;

/**
 * 历史任务记录对象 flow_his_task
 *
 * @author warm
 * @date 2023-03-29
 */
public interface HisTask extends RootEntity {

    public Long getId();

    public HisTask setId(Long id);

    public Date getCreateTime();

    public HisTask setCreateTime(Date createTime);

    public Date getUpdateTime();

    public HisTask setUpdateTime(Date updateTime);

    public String getTenantId();

    public HisTask setTenantId(String tenantId);

    public String getDelFlag();

    public HisTask setDelFlag(String delFlag);

    public Long getDefinitionId();

    public HisTask setDefinitionId(Long definitionId);

    public String getFlowName();

    public HisTask setFlowName(String flowName);

    public Long getInstanceId();

    public HisTask setInstanceId(Long instanceId);

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

    public List<String> getPermissionList();

    public HisTask setPermissionList(List<String> permissionList);

    public Integer getFlowStatus();

    public HisTask setFlowStatus(Integer flowStatus);

    public String getMessage();

    public HisTask setMessage(String message);

    public String getCreateBy();

    public HisTask setCreateBy(String createBy);

    public String getFromCustom();

    public HisTask setFromCustom(String fromCustom);

    public String getFromPath();

    public HisTask setFromPath(String fromPath);
}
