package com.warm.flow.core.entity;

/**
 * 流程用户 flow_user
 *
 * @author xiarg
 * @date 2024/5/10 10:41
 */
public interface User extends RootEntity{

    public String getType();

    public User setType(String type);

    public String getProcessedBy();

    public User setProcessedBy(String processedBy);

    public long getAssociated();

    public User setAssociated(long associated);
}