package com.warm.flow.orm.dao;

import com.warm.flow.core.dao.FlowDefinitionDao;
import com.warm.flow.orm.entity.FlowDefinition;
import com.warm.flow.orm.utils.TenantDeleteUtil;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 流程定义Mapper接口
 *
 * @author vanlin
 * @date 2024-05-12
 */
public class FlowDefinitionDaoImpl extends WarmDaoImpl<FlowDefinition> implements FlowDefinitionDao<FlowDefinition> {

    @Override
    public FlowDefinition newEntity() {
        return new FlowDefinition();
    }

    @Override
    public Class<FlowDefinition> entityClass() {
        return FlowDefinition.class;
    }

    @Override
    public List<FlowDefinition> queryByCodeList(List<String> flowCodeList) {
        final FlowDefinition entity = TenantDeleteUtil.getEntity(newEntity());

        final CriteriaQuery<FlowDefinition> criteriaQuery = createCriteriaQuery((criteriaBuilder, root, predicates, innerCriteriaQuery) -> {
            entity.commonPredicate().process(criteriaBuilder, root, predicates);

            predicates.add(createIn(criteriaBuilder, root, "flowCode", flowCodeList));
        });
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    public void closeFlowByCodeList(List<String> flowCodeList) {
        final FlowDefinition entity = TenantDeleteUtil.getEntity(newEntity());

        final CriteriaUpdate<FlowDefinition> criteriaUpdate = createCriteriaUpdate((criteriaBuilder, root, predicates, innerCriteriaUpdate) -> {
            entity.commonPredicate().process(criteriaBuilder, root, predicates);

            predicates.add(createIn(criteriaBuilder, root, "flowCode", flowCodeList));

            // 是否发布（0未发布 1已发布 9失效）
            innerCriteriaUpdate.set(root.get("isPublish"), 9);
        });

        entityManager.createQuery(criteriaUpdate).executeUpdate();
    }

}
