package com.warm.flow.core.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * @author minliuhua
 * @description: 流程基础entity
 * @date: 2023/5/17 17:23
 */
public interface RootEntity extends Serializable {

    public Long getId();

    public RootEntity setId(Long id);

    public Date getCreateTime();

    public RootEntity setCreateTime(Date createTime);

    public Date getUpdateTime();

    public RootEntity setUpdateTime(Date updateTime);

}
