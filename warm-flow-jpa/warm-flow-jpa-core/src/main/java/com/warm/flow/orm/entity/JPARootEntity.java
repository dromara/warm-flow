package com.warm.flow.orm.entity;

import com.warm.flow.core.entity.RootEntity;
import com.warm.flow.orm.utils.JPAUpdateMergeFunction;
import com.warm.flow.orm.utils.JPAUtil;
import com.warm.flow.orm.utils.JPAPredicateFunction;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;

/**
 * @author vanlin
 * @className JPARootEntity
 * @description
 * @since 2024/5/10 17:59
 */
@MappedSuperclass
public abstract class JPARootEntity<T extends RootEntity> implements RootEntity {

    protected static final HashMap<String, String> JPA_ROOT_ENTITY_MAPPING = new HashMap<>();
    static {
        JPAUtil.initMapping(JPARootEntity.class, JPA_ROOT_ENTITY_MAPPING);
    }

    @Transient
    private JPAPredicateFunction<CriteriaBuilder, Root<T>, List<Predicate>> commonPredicate =
            (criteriaBuilder, root, predicates) -> {
                if (Objects.nonNull(this.id)) {
                    predicates.add(criteriaBuilder.equal(root.get("id"), this.id));
                }
                if (Objects.nonNull(this.createTime)) {
                    predicates.add(criteriaBuilder.equal(root.get("createTime"), this.createTime));
                }
                if (Objects.nonNull(this.updateTime)) {
                    predicates.add(criteriaBuilder.equal(root.get("updateTime"), this.updateTime));
                }
                if (Objects.nonNull(this.delFlag)) {
                    predicates.add(criteriaBuilder.equal(root.get("delFlag"), this.delFlag));
                }
                if (Objects.nonNull(this.tenantId)) {
                    predicates.add(criteriaBuilder.equal(root.get("tenantId"), this.tenantId));
                }
            };


    public abstract String orderByField(String orderByColumn);

    public abstract JPAPredicateFunction<CriteriaBuilder, Root<T>, List<Predicate>> entityPredicate();

    public abstract JPAUpdateMergeFunction<T> entityMerge();

    public abstract void initDefaultValue();

    public JPAPredicateFunction<CriteriaBuilder, Root<T>, List<Predicate>> commonPredicate() {
        return this.commonPredicate;
    };




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

    @SuppressWarnings("unchecked")
    @Override
    public T setId(Long id) {
        this.id = id;
        return (T)this;
    }

    @Override
    public Date getCreateTime() {
        return createTime;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T setCreateTime(Date createTime) {
        this.createTime = createTime;
        return (T)this;
    }

    @Override
    public Date getUpdateTime() {
        return updateTime;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
        return (T)this;
    }

    @Override
    public String getTenantId() {
        return tenantId;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T setTenantId(String tenantId) {
        this.tenantId = tenantId;
        return (T)this;
    }

    @Override
    public String getDelFlag() {
        return delFlag;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T setDelFlag(String delFlag) {
        this.delFlag = delFlag;
        return (T)this;
    }

    @Override
    public String toString() {
        return "JPARootEntity{" +
                "id=" + id +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", tenantId='" + tenantId + '\'' +
                ", delFlag='" + delFlag + '\'' +
                '}';
    }


}
