package com.warm.flow.core.domain.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * @author minliuhua
 * @description: 流程基础entity
 * @date: 2023/5/17 17:23
 */
public class FlowEntity implements Serializable {

    /**
     * 主键
     */
    private Long id;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;


    public Long getId() {
        return id;
    }

    public FlowEntity setId(Long id) {
        this.id = id;
        return this;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public FlowEntity setCreateTime(Date createTime) {
        this.createTime = createTime;
        return this;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public FlowEntity setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
        return this;
    }

}
