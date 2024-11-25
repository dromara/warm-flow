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

import org.dromara.warm.flow.core.dao.FlowInstanceDao;
import org.dromara.warm.flow.orm.entity.FlowInstance;
import org.dromara.warm.flow.orm.utils.TenantDeleteUtil;

import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

/**
 * 流程实例Mapper接口
 *
 * @author vanlin
 * @since 2024-05-12
 */
public class FlowInstanceDaoImpl extends WarmDaoImpl<FlowInstance> implements FlowInstanceDao<FlowInstance> {
    @Override
    public FlowInstance newEntity() {
        return new FlowInstance();
    }

    @Override
    public Class<FlowInstance> entityClass() {
        return FlowInstance.class;
    }

    @Override
    public List<FlowInstance> getByDefIds(List<Long> defIds) {
        final FlowInstance entity = TenantDeleteUtil.getEntity(newEntity());

        final CriteriaQuery<FlowInstance> criteriaQuery = createCriteriaQuery((criteriaBuilder, root, predicates, innerCriteriaQuery) -> {
            entity.commonPredicate().process(criteriaBuilder, root, predicates);

            predicates.add(createIn(criteriaBuilder, root, "definitionId", defIds));
        });
        return entityManager.createQuery(criteriaQuery).getResultList();
    }
}
