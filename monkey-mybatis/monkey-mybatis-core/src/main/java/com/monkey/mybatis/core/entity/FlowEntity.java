package com.monkey.mybatis.core.entity;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @description:  流程基础entity
 * @author minliuhua
 * @date: 2023/5/17 17:23
 */
public interface FlowEntity {

    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    public static final SimpleDateFormat dateFormat = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS);

    public Long getId();

    public void setId(Long id);

    public Date getCreateTime();

    public void setCreateTime(Date createTime);

    public Date getUpdateTime() ;

    public void setUpdateTime(Date updateTime);
}
