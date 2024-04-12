package com.warm.flow.core.entity;

import java.util.Date;

/**
 * 流程实例对象 flow_instance
 *
 * @author warm
 * @date 2023-03-29
 */
public interface Instance extends RootEntity {

    public Long getId();

    public Instance setId(Long id);

    public Date getCreateTime();

    public Instance setCreateTime(Date createTime);

    public Date getUpdateTime();

    public Instance setUpdateTime(Date updateTime);

    public Long getDefinitionId();

    public Instance setDefinitionId(Long definitionId);

    public String getFlowName();

    public Instance setFlowName(String flowName);

    public String getBusinessId();

    public Instance setBusinessId(String businessId);

    String getTenantId();

    Instance setTenantId(String tenantId);

    public Integer getNodeType();

    public Instance setNodeType(Integer nodeType);

    public String getNodeCode();

    public Instance setNodeCode(String nodeCode);

    public String getNodeName();

    public Instance setNodeName(String nodeName);

    String getVariable();

    Instance setVariable(String variable);

    public Integer getFlowStatus();

    public Instance setFlowStatus(Integer flowStatus);

    public String getCreateBy();

    public Instance setCreateBy(String createBy);

    public String getFromCustom();

    public Instance setFromCustom(String fromCustom);

    public String getFromPath();

    public Instance setFromPath(String fromPath);

    public String getExt();

    public Instance setExt(String ext);

}
