/*
 *    Copyright 2024-2025, Warm-Flow (290631660@qq.com).
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.dromara.warm.flow.orm.entity;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.dromara.warm.flow.core.entity.RootEntity;
import org.dromara.warm.flow.orm.utils.JPAPredicateFunction;
import org.dromara.warm.flow.orm.utils.JPAUpdateMergeFunction;
import org.dromara.warm.flow.orm.utils.JPAUtil;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * @author vanlin
 * @className JPARootEntity
 * @description
 * @since 2024/5/10 17:59
 */
@ToString
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
    }

    /**
     * 主键
     */
    @Id
    private Long id;

    /**
     * 任务开始时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 审批完成时间
     */
    @Column(name = "update_time")
    private Date updateTime;

    /**
     * 租户ID
     */
    @Column(name = "tenant_id")
    private String tenantId;

    /**
     * 删除标记
     */
    @Column(name = "del_flag")
    private String delFlag;

    @Override
    public Long getId() {
        return id;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T setId(Long id) {
        this.id = id;
        return (T) this;
    }

    @Override
    public Date getCreateTime() {
        return createTime;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T setCreateTime(Date createTime) {
        this.createTime = createTime;
        return (T) this;
    }

    @Override
    public Date getUpdateTime() {
        return updateTime;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
        return (T) this;
    }

    @Override
    public String getTenantId() {
        return tenantId;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T setTenantId(String tenantId) {
        this.tenantId = tenantId;
        return (T) this;
    }

    @Override
    public String getDelFlag() {
        return delFlag;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T setDelFlag(String delFlag) {
        this.delFlag = delFlag;
        return (T) this;
    }

}
