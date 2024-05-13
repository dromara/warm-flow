package com.warm.flow.core.entity;

import java.util.Date;

/**
 * 流程用户 flow_user
 *
 * @author xiarg
 * @date 2024/5/10 10:41
 */
public interface User extends RootEntity{

    public Long getId();

    public User setId(Long id);

    public Date getCreateTime();

    public User setCreateTime(Date createTime);

    public Date getUpdateTime();

    public User setUpdateTime(Date updateTime);

    public String getTenantId();

    public User setTenantId(String tenantId);

    public String getDelFlag();

    public User setDelFlag(String delFlag);

    public String getType();

    public User setType(String type);

    public String getProcessedBy();

    public User setProcessedBy(String processedBy);

    public Long getAssociated();

    public User setAssociated(Long associated);
}