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
package org.dromara.warm.flow.orm.dao;

import org.dromara.warm.flow.core.FlowFactory;
import org.dromara.warm.flow.core.dao.WarmDao;
import org.dromara.warm.flow.core.orm.agent.WarmQuery;
import org.dromara.warm.flow.core.utils.AssertUtil;
import org.dromara.warm.flow.core.utils.CollUtil;
import org.dromara.warm.flow.core.utils.ObjectUtil;
import org.dromara.warm.flow.core.utils.StringUtils;
import org.dromara.warm.flow.core.utils.page.OrderBy;
import org.dromara.warm.flow.core.utils.page.Page;
import org.dromara.warm.flow.orm.entity.JPARootEntity;
import org.dromara.warm.flow.orm.utils.JPAPredicateFunction;
import org.dromara.warm.flow.orm.utils.JPAQueryFunction;
import org.dromara.warm.flow.orm.utils.JPAUpdateFunction;
import org.dromara.warm.flow.orm.utils.TenantDeleteUtil;

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
 * @since 2024-05-12
 */
public abstract class WarmDaoImpl<T extends JPARootEntity<T>> implements WarmDao<T> {

    @PersistenceContext(unitName = "warm-flow-jpa")
    protected EntityManager entityManager;

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
                        .setFirstResult((page.getPageNum() - 1) *page.getPageSize())
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
        final CriteriaQuery<Long> criteriaCountQuery = createCriteriaCountQuery((criteriaBuilder, root, predicates) -> {
            entity.commonPredicate().process(criteriaBuilder, root, predicates);
            entity.entityPredicate().process(criteriaBuilder, root, predicates);
        });
        return entityManager.createQuery(criteriaCountQuery).getSingleResult();
    }

    @Override
    public int save(T entity) {
        TenantDeleteUtil.getEntity(entity);
        entityManager.persist(entity);
        return 1;
    }

    @Override
    public int updateById(T entity) {
        T originEntity = selectById(entity.getId());

        // 此处为兼容整体框架 updateById 处理逻辑,获取原始Entity,并进行更新合并操作
        originEntity.entityMerge().merge(entity);

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
    public void saveBatch(List<T> list) {
        for (T record : list) {
            save(record);
        }
    }

    @Override
    public void updateBatch(List<T> list) {
        for (T record : list) {
            updateById(record);
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
                AssertUtil.isEmpty(field, "OrderBy 字段不能为空");
                if (orderBy.getIsAsc().equals(OrderBy.ASC)) {
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

    protected <F extends Serializable> CriteriaBuilder.In<F> createIn(CriteriaBuilder criteriaBuilder, Root<T> root,
                                                                      String fieldName, F[] values) {
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
}
