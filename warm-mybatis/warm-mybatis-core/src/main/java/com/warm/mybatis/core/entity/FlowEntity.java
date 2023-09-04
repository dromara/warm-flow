package com.warm.mybatis.core.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * @author minliuhua
 * @description: 流程基础entity
 * @date: 2023/5/17 17:23
 */
public interface FlowEntity extends Serializable {

    public Long getId();

    public void setId(Long id);

    public Date getCreateTime();

    public void setCreateTime(Date createTime);

    public Date getUpdateTime();

    public void setUpdateTime(Date updateTime);
}
