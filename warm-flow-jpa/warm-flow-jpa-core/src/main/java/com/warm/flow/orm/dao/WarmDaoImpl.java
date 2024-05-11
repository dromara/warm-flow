package com.warm.flow.orm.dao;

import com.warm.flow.core.FlowFactory;
import com.warm.flow.core.dao.WarmDao;
import com.warm.flow.core.handler.DataFillHandler;
import com.warm.flow.core.orm.agent.WarmQuery;
import com.warm.flow.orm.entity.JPARootEntity;
import com.warm.flow.orm.utils.TenantDeleteUtil;
import com.warm.tools.utils.CollUtil;
import com.warm.tools.utils.ObjectUtil;
import com.warm.tools.utils.page.Page;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

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
        final CriteriaQuery<T> criteriaQuery = entity.criteriaQuery(entityManager, null);

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

        final CriteriaQuery<T> criteriaQuery = entity.criteriaQuery(entityManager, null,
                (criteriaBuilder, root, predicates) -> {
                    predicates.add(criteriaBuilder.in(root.get("id").in(ids)));
                });
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    @Override
    public Page<T> selectPage(T entity, Page<T> page) {
        TenantDeleteUtil.getEntity(entity);
        final CriteriaQuery<Long> criteriaCountQuery = entity.criteriaCountQuery(entityManager, page);
        Long total = entityManager.createQuery(criteriaCountQuery).getSingleResult();
        if (ObjectUtil.isNotNull(total) && total > 0) {
            final CriteriaQuery<T> criteriaQuery = entity.criteriaQuery(entityManager, page);
            return new Page<>(entityManager.createQuery(criteriaQuery).getResultList(), total);
        }

        return Page.empty();
    }

    @Override
    public List<T> selectList(T entity, WarmQuery<T> query) {
        TenantDeleteUtil.getEntity(entity);
        final CriteriaQuery<T> criteriaQuery = entity.criteriaQuery(entityManager, query);

        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    @Override
    public long selectCount(T entity) {
        TenantDeleteUtil.getEntity(entity);
        final CriteriaQuery<Long> criteriaCountQuery = entity.criteriaCountQuery(entityManager, null);
        return entityManager.createQuery(criteriaCountQuery).getSingleResult();
    }

    @Override
    public int save(T entity) {
        insertFill(entity);
        entityManager.persist(entity);
        return 1;
    }

    public int insert(T entity) {
        TenantDeleteUtil.getEntity(entity);
        entityManager.persist(entity);
        return 1;
    }

    @Override
    public int modifyById(T entity) {
        updateFill(entity);
        entityManager.persist(entity);
        return 1;
    }

    public int updateById(T entity) {
        TenantDeleteUtil.getEntity(entity);
        entityManager.persist(entity);
        return 1;
    }

    @Override
    public int delete(T entity) {
        /*TenantDeleteUtil.getEntity(entity);
        if (StringUtils.isNotEmpty(entity.getDelFlag())) {
            getMapper().updateLogic(entity, FlowFactory.getFlowConfig().getLogicDeleteValue(), entity.getDelFlag());
        }
        return getMapper().delete(entity);*/
        return 0;
    }

    @Override
    public int deleteById(Serializable id) {
       /* T entity = TenantDeleteUtil.getEntity(newEntity());
        if (StringUtils.isNotEmpty(entity.getDelFlag())) {
            getMapper().updateByIdLogic(id, entity, FlowFactory.getFlowConfig().getLogicDeleteValue(), entity.getDelFlag());
        }
        return getMapper().deleteById(id, entity);*/
        return 0;
    }

    @Override
    public int deleteByIds(Collection<? extends Serializable> ids) {
       /* T entity = TenantDeleteUtil.getEntity(newEntity());
        if (StringUtils.isNotEmpty(entity.getDelFlag())) {
            getMapper().updateByIdsLogic(ids, entity, FlowFactory.getFlowConfig().getLogicDeleteValue(), entity.getDelFlag());
        }
        return getMapper().deleteByIds(ids, entity);*/
        return 0;
    }

    public void insertFill(T entity) {
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
