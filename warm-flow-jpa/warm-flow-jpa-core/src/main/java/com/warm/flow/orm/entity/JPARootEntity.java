package com.warm.flow.orm.entity;

import com.warm.flow.core.entity.RootEntity;
import com.warm.flow.core.orm.agent.WarmQuery;
import com.warm.flow.core.utils.AssertUtil;
import com.warm.flow.orm.utils.JPAUtil;
import com.warm.flow.orm.utils.PredictesFunction;
import com.warm.tools.utils.StringUtils;
import com.warm.tools.utils.page.OrderBy;

import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
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

    private void commonPredicates(CriteriaBuilder criteriaBuilder,
                                     Root<T> root,
                                     List<Predicate> predicates) {
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
    }

    protected abstract void customPredicates(CriteriaBuilder criteriaBuilder,
                                    Root<T> root,
                                    List<Predicate> predicates);

    protected abstract String customOrderByField(String orderByColumn);

    public CriteriaQuery<T> criteriaQuery(EntityManager entityManager, OrderBy orderBy) {
        return criteriaQuery(entityManager, orderBy, null);
    }
    @SuppressWarnings("unchecked")
    public CriteriaQuery<T> criteriaQuery(EntityManager entityManager, OrderBy orderBy,
                                          PredictesFunction<CriteriaBuilder, Root<T>, List<Predicate>> outerPredictesFunction) {
        final Class<T> entityClass = (Class<T>) this.getClass();
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(entityClass);
        final Root<T> root = criteriaQuery.from(entityClass);

        final List<Predicate> predicates = new ArrayList<Predicate>();

        this.commonPredicates(criteriaBuilder, root, predicates);
        this.customPredicates(criteriaBuilder, root, predicates);

        if (Objects.nonNull(outerPredictesFunction)) {
            outerPredictesFunction.predictes(criteriaBuilder, root, predicates);
        }

        criteriaQuery.where(predicates.toArray(new Predicate[0]));
        if (Objects.nonNull(orderBy)) {
            if (StringUtils.isNotEmpty(orderBy.getOrderBy())) {
                String field = customOrderByField(orderBy.getOrderBy());
                AssertUtil.isTrue(StringUtils.isNotEmpty(field), "OrderBy 字段不能为空");
                if(orderBy.getIsAsc().equals(OrderBy.ASC)) {
                    criteriaQuery.orderBy(criteriaBuilder.asc(root.get(field)));
                } else {
                    criteriaQuery.orderBy(criteriaBuilder.desc(root.get(field)));
                }
            }
        }
        return criteriaQuery;
    }
    public CriteriaQuery<Long> criteriaCountQuery(EntityManager entityManager, OrderBy orderBy) {
        return criteriaCountQuery(entityManager, orderBy, null);
    }
    @SuppressWarnings("unchecked")
    public CriteriaQuery<Long> criteriaCountQuery(EntityManager entityManager, OrderBy orderBy,
                                                  PredictesFunction<CriteriaBuilder, Root<T>, List<Predicate>> outerPredictesFunction) {
        final Class<T> entityClass = (Class<T>) this.getClass();
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        final Root<T> rootCount = criteriaQuery.from(entityClass);

        final List<Predicate> predicates = new ArrayList<Predicate>();

        this.commonPredicates(criteriaBuilder, rootCount, predicates);
        this.customPredicates(criteriaBuilder, rootCount, predicates);
        if (Objects.nonNull(outerPredictesFunction)) {
            outerPredictesFunction.predictes(criteriaBuilder, rootCount, predicates);
        }

        criteriaQuery.select(criteriaBuilder.count(rootCount));
        criteriaQuery.where(predicates.toArray(new Predicate[0]));

        return criteriaQuery;
    }

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
