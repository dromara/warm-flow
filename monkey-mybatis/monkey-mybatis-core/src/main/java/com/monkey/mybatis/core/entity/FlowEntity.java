package com.monkey.mybatis.core.entity;

import java.util.Date;

/**
 * @description:  流程基础entity
 * @author minliuhua
 * @date: 2023/5/17 17:23
 */
public interface FlowEntity {

    public Long getId();

    public void setId(Long id);

    public Date getCreateTime();

    public void setCreateTime(Date createTime);

    public Date getUpdateTime() ;

    public void setUpdateTime(Date updateTime);
}
