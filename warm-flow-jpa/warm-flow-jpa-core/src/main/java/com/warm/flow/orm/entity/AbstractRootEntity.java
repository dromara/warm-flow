package com.warm.flow.orm.entity;

import com.warm.flow.core.entity.RootEntity;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.util.Date;

/**
 * @author vanlin
 * @className BaseEntity
 * @description
 * @since 2024/5/10 17:59
 */
@MappedSuperclass
public abstract class AbstractRootEntity<T extends RootEntity> implements RootEntity {

    /**
     * 主键
     */
    @Id
    private Long id;

    /**
     * 创建时间
     */
    @Column(name="create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @Column(name="update_time")
    private Date updateTime;

    /**
     * 租户ID
     */
    @Column(name="tenant_id")
    private String tenantId;

    /**
     * 删除标记
     */
    @Column(name="del_flag")
    private String delFlag;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public T setId(Long id) {
        this.id = id;
        return (T)this;
    }

    @Override
    public Date getCreateTime() {
        return createTime;
    }

    @Override
    public T setCreateTime(Date createTime) {
        this.createTime = createTime;
        return (T)this;
    }

    @Override
    public Date getUpdateTime() {
        return updateTime;
    }

    @Override
    public T setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
        return (T)this;
    }

    @Override
    public String getTenantId() {
        return tenantId;
    }

    @Override
    public T setTenantId(String tenantId) {
        this.tenantId = tenantId;
        return (T)this;
    }

    @Override
    public String getDelFlag() {
        return delFlag;
    }

    @Override
    public T setDelFlag(String delFlag) {
        this.delFlag = delFlag;
        return (T)this;
    }

    @Override
    public String toString() {
        return "AbstractRootEntity{" +
                "id=" + id +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", tenantId='" + tenantId + '\'' +
                ", delFlag='" + delFlag + '\'' +
                '}';
    }
}
