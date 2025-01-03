package org.dromara.warm.flow.orm.dao;

import org.dromara.warm.flow.core.orm.dao.FlowFormDao;
import org.dromara.warm.flow.orm.entity.FlowDefinition;
import org.dromara.warm.flow.orm.entity.FlowForm;
import org.dromara.warm.flow.orm.utils.TenantDeleteUtil;

import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

/**
 * @author vanlin
 * @className FlowFormDaoImpl
 * @description
 * @since 2024/8/19 10:29
 */
public class FlowFormDaoImpl extends WarmDaoImpl<FlowForm> implements FlowFormDao<FlowForm> {

    @Override
    public List<FlowForm> queryByCodeList(List<String> formCodeList) {
        final FlowForm entity = TenantDeleteUtil.getEntity(newEntity());

        final CriteriaQuery<FlowForm> criteriaQuery = createCriteriaQuery((criteriaBuilder, root, predicates, innerCriteriaQuery) -> {
            entity.commonPredicate().process(criteriaBuilder, root, predicates);

            predicates.add(createIn(criteriaBuilder, root, "formCode", formCodeList));
        });
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    @Override
    public Class<FlowForm> entityClass() {
        return FlowForm.class;
    }

    @Override
    public FlowForm newEntity() {
        return new FlowForm();
    }
}
