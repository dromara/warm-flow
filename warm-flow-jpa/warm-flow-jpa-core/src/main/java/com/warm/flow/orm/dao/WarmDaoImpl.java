package com.warm.flow.orm.dao;

import com.warm.flow.core.FlowFactory;
import com.warm.flow.core.dao.WarmDao;
import com.warm.flow.core.handler.DataFillHandler;
import com.warm.flow.core.orm.agent.WarmQuery;
import com.warm.flow.core.utils.AssertUtil;
import com.warm.flow.orm.entity.JPARootEntity;
import com.warm.flow.orm.utils.JPAQueryFunction;
import com.warm.flow.orm.utils.JPAPredicateFunction;
import com.warm.flow.orm.utils.TenantDeleteUtil;
import com.warm.flow.orm.utils.JPAUpdateFunction;
import com.warm.flow.core.utils.CollUtil;
import com.warm.flow.core.utils.ObjectUtil;
import com.warm.flow.core.utils.StringUtils;
import com.warm.flow.core.utils.page.OrderBy;
import com.warm.flow.core.utils.page.Page;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * BaseMapper接口
 *
 * @author vanlin
 * @date 2024-05-12
 */
public abstract class WarmDaoImpl<T extends JPARootEntity<T>> implements WarmDao<T> {

    @PersistenceContext(unitName = "warm-flow-jpa")
    protected EntityManager entityManager;

    public abstract T newEntity();

    public abstract Class<T> entityClass();

    /**
     * 根据id查询
     *
     * @param id 主键
     * @return 实体
     */
    @Override
    public T selectById(Serializable id) {
        final T entity = TenantDeleteUtil.getEntity(newEntity());
        entity.setId((Long) id);

        final CriteriaQuery<T> criteriaQuery = createCriteriaQuery((criteriaBuilder, root, predicates, innerCriteriaQuery) -> {
            entity.commonPredicate().process(criteriaBuilder, root, predicates);
        });

        return CollUtil.getOne(entityManager.createQuery(criteriaQuery).getResultList());
    }

    /**
     * 根据ids查询
     *
     * @param ids 主键
     * @return 实体
     */
    @Override
    public List<T> selectByIds(Collection<? extends Serializable> ids) {
        final T entity = TenantDeleteUtil.getEntity(newEntity());

        final CriteriaQuery<T> criteriaQuery = createCriteriaQuery((criteriaBuilder, root, predicates, innerCriteriaQuery) -> {
            entity.commonPredicate().process(criteriaBuilder, root, predicates);

            predicates.add(criteriaBuilder.in(root.get("id").in(ids)));
        });

        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    @Override
    public Page<T> selectPage(T entity, Page<T> page) {
        TenantDeleteUtil.getEntity(entity);

        JPAPredicateFunction<CriteriaBuilder, Root<T>, List<Predicate>> predicateFunction = (criteriaBuilder, root, predicates) -> {
            entity.commonPredicate().process(criteriaBuilder, root, predicates);
            entity.entityPredicate().process(criteriaBuilder, root, predicates);
        };

        final CriteriaQuery<Long> criteriaCountQuery = createCriteriaCountQuery(predicateFunction);
        Long total = entityManager.createQuery(criteriaCountQuery).getSingleResult();
        if (ObjectUtil.isNotNull(total) && total > 0) {
            final CriteriaQuery<T> criteriaQuery = createCriteriaQuery((criteriaBuilder, root, predicates, innerCriteriaQuery) -> {
                predicateFunction.process(criteriaBuilder, root, predicates);

                orderBy(criteriaBuilder, root, innerCriteriaQuery, entity, page);
            });
            // page 处理
            if (ObjectUtil.isNotNull(page)) {
                return new Page<>(entityManager.createQuery(criteriaQuery)
                        .setFirstResult(page.getPageNum())
                        .setMaxResults(page.getPageSize())
                        .getResultList(), total);
            }
            return new Page<>(entityManager.createQuery(criteriaQuery).getResultList(), total);
        }

        return Page.empty();
    }

    @Override
    public List<T> selectList(T entity, WarmQuery<T> query) {
        TenantDeleteUtil.getEntity(entity);

        final CriteriaQuery<T> criteriaQuery = createCriteriaQuery((criteriaBuilder, root, predicates, innerCriteriaQuery) -> {
            entity.commonPredicate().process(criteriaBuilder, root, predicates);
            entity.entityPredicate().process(criteriaBuilder, root, predicates);

            orderBy(criteriaBuilder, root, innerCriteriaQuery, entity, query);
        });

        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    @Override
    public long selectCount(T entity) {
        TenantDeleteUtil.getEntity(entity);
        final CriteriaQuery<Long> criteriaCountQuery =createCriteriaCountQuery((criteriaBuilder, root, predicates) -> {
            entity.commonPredicate().process(criteriaBuilder, root, predicates);
            entity.entityPredicate().process(criteriaBuilder, root, predicates);
        });
        return entityManager.createQuery(criteriaCountQuery).getSingleResult();
    }

    @Override
    public int save(T entity) {
        insertFill(entity);
        return insert(entity);
    }

    public int insert(T entity) {
        TenantDeleteUtil.getEntity(entity);
        entityManager.persist(entity);
        return 1;
    }

    @Override
    public int modifyById(T entity) {
        updateFill(entity);
        return updateById(entity);
    }

    public int updateById(T entity) {
        // 此处为兼容框架 updateById 处理模式,将原始Entity查询,并进行更新合并操作
        T originEntity = selectById(entity.getId());
        originEntity.mergeUpdate(entity);
        entityManager.persist(originEntity);
        return 1;
    }

    @Override
    public int delete(T entity) {
        if (StringUtils.isNotEmpty(entity.getDelFlag())) {
            CriteriaUpdate<T> criteriaUpdate = createCriteriaUpdate((criteriaBuilder, root, predicates, innerCriteriaUpdate) -> {
                entity.commonPredicate().process(criteriaBuilder, root, predicates);
                entity.entityPredicate().process(criteriaBuilder, root, predicates);

                // 更新值
                innerCriteriaUpdate.set(root.get("delFlag"), FlowFactory.getFlowConfig().getLogicDeleteValue());
            });

            return entityManager.createQuery(criteriaUpdate).executeUpdate();
        } else {
            CriteriaDelete<T> criteriaDelete = createCriteriaDelete((criteriaBuilder, root, predicates) -> {
                entity.commonPredicate().process(criteriaBuilder, root, predicates);
                entity.entityPredicate().process(criteriaBuilder, root, predicates);
            });

            return entityManager.createQuery(criteriaDelete).executeUpdate();
        }
    }

    @Override
    public int deleteById(Serializable id) {
        T entity = TenantDeleteUtil.getEntity(newEntity());
        entity.setId((Long) id);
        if (StringUtils.isNotEmpty(entity.getDelFlag())) {
            CriteriaUpdate<T> criteriaUpdate = createCriteriaUpdate((criteriaBuilder, root, predicates, innerCriteriaUpdate) -> {
                predicates.add(criteriaBuilder.equal(root.get("id"), id));
                if (StringUtils.isNotEmpty(entity.getTenantId())) {
                    predicates.add(criteriaBuilder.equal(root.get("tenantId"), entity.getTenantId()));
                }

                // 更新值
                innerCriteriaUpdate.set(root.get("delFlag"), FlowFactory.getFlowConfig().getLogicDeleteValue());
            });

            return entityManager.createQuery(criteriaUpdate).executeUpdate();
        } else {
            CriteriaDelete<T> criteriaDelete = createCriteriaDelete((criteriaBuilder, root, predicates) -> {
                predicates.add(criteriaBuilder.equal(root.get("id"), id));

                if (StringUtils.isNotEmpty(entity.getTenantId())) {
                    predicates.add(criteriaBuilder.equal(root.get("tenantId"), entity.getTenantId()));
                }
            });

            return entityManager.createQuery(criteriaDelete).executeUpdate();
        }
    }

    @Override
    public int deleteByIds(Collection<? extends Serializable> ids) {
        T entity = TenantDeleteUtil.getEntity(newEntity());
        if (StringUtils.isNotEmpty(entity.getDelFlag())) {
            CriteriaUpdate<T> criteriaUpdate = createCriteriaUpdate((criteriaBuilder, root, predicates, innerCriteriaUpdate) -> {
                predicates.add(createIn(criteriaBuilder, root, "id", ids));

                if (StringUtils.isNotEmpty(entity.getTenantId())) {
                    predicates.add(criteriaBuilder.equal(root.get("tenantId"), entity.getTenantId()));
                }

                // 更新值
                innerCriteriaUpdate.set(root.get("delFlag"), FlowFactory.getFlowConfig().getLogicDeleteValue());
            });

            return entityManager.createQuery(criteriaUpdate).executeUpdate();
        } else {
            CriteriaDelete<T> criteriaDelete = createCriteriaDelete((criteriaBuilder, root, predicates) -> {
                predicates.add(createIn(criteriaBuilder, root, "id", ids));

                if (StringUtils.isNotEmpty(entity.getTenantId())) {
                    predicates.add(criteriaBuilder.equal(root.get("tenantId"), entity.getTenantId()));
                }
            });

            return entityManager.createQuery(criteriaDelete).executeUpdate();
        }
    }

    protected CriteriaQuery<T> createCriteriaQuery(JPAQueryFunction<CriteriaBuilder, Root<T>, List<Predicate>, CriteriaQuery<T>> orderByQueryFunction) {
        final Class<T> entityClass = entityClass();
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(entityClass);
        final Root<T> root = criteriaQuery.from(entityClass);

        final List<Predicate> predicates = new ArrayList<Predicate>();

        orderByQueryFunction.process(criteriaBuilder, root, predicates, criteriaQuery);

        criteriaQuery.where(predicates.toArray(new Predicate[0]));
        return criteriaQuery;
    }

    protected CriteriaQuery<Long> createCriteriaCountQuery(JPAPredicateFunction<CriteriaBuilder, Root<T>, List<Predicate>> predicateFunction) {
        final Class<T> entityClass = entityClass();
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        final Root<T> rootCount = criteriaQuery.from(entityClass);

        final List<Predicate> predicates = new ArrayList<Predicate>();

        predicateFunction.process(criteriaBuilder, rootCount, predicates);

        criteriaQuery.select(criteriaBuilder.count(rootCount));
        criteriaQuery.where(predicates.toArray(new Predicate[0]));

        return criteriaQuery;
    }

    protected void orderBy(CriteriaBuilder criteriaBuilder,
                         Root<T> root,
                         CriteriaQuery<T> criteriaQuery,
                         T entity,
                         OrderBy orderBy) {
        if (Objects.nonNull(orderBy)) {
            if (StringUtils.isNotEmpty(orderBy.getOrderBy())) {
                String field = entity.orderByField(orderBy.getOrderBy());
                AssertUtil.isTrue(StringUtils.isEmpty(field), "OrderBy 字段不能为空");
                if(orderBy.getIsAsc().equals(OrderBy.ASC)) {
                    criteriaQuery.orderBy(criteriaBuilder.asc(root.get(field)));
                } else {
                    criteriaQuery.orderBy(criteriaBuilder.desc(root.get(field)));
                }
            }
        }
    }

    protected <F extends Serializable> CriteriaBuilder.In<F> createIn(CriteriaBuilder criteriaBuilder, Root<T> root,
                                            String fieldName, Collection<F> values) {
        CriteriaBuilder.In<F> in = criteriaBuilder.in(root.get(fieldName));
        for (F value : values) {
            in.value(value);
        }
        return in;
    }

    protected CriteriaUpdate<T> createCriteriaUpdate(JPAUpdateFunction<CriteriaBuilder, Root<T>, List<Predicate>, CriteriaUpdate<T>> updateFunction) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        Class<T> entityClass = entityClass();
        CriteriaUpdate<T> criteriaUpdate = criteriaBuilder.createCriteriaUpdate(entityClass);
        Root<T> root = criteriaUpdate.from(entityClass);
        List<Predicate> predicates = new ArrayList<>();

        updateFunction.process(criteriaBuilder, root, predicates, criteriaUpdate);

        criteriaUpdate.where(predicates.toArray(new Predicate[0]));
        return criteriaUpdate;
    }

    protected CriteriaDelete<T> createCriteriaDelete(JPAPredicateFunction<CriteriaBuilder, Root<T>, List<Predicate>> predicateFunction) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        Class<T> entityClass = entityClass();
        CriteriaDelete<T> criteriaDelete = criteriaBuilder.createCriteriaDelete(entityClass);
        Root<T> root = criteriaDelete.from(entityClass);
        List<Predicate> predicates = new ArrayList<>();

        predicateFunction.process(criteriaBuilder, root, predicates);

        criteriaDelete.where(predicates.toArray(new Predicate[0]));
        return criteriaDelete;
    }

    public void insertFill(T entity) {
        // 新增时处理默认值填充
        entity.initDefaultValue();
        DataFillHandler dataFillHandler = FlowFactory.dataFillHandler();
        if (ObjectUtil.isNotNull(dataFillHandler)) {
            dataFillHandler.idFill(entity);
            dataFillHandler.insertFill(entity);
        }
    }

    public void updateFill(T entity) {
        DataFillHandler dataFillHandler = FlowFactory.dataFillHandler();
        if (ObjectUtil.isNotNull(dataFillHandler)) {
            dataFillHandler.updateFill(entity);
        }
    }
}
