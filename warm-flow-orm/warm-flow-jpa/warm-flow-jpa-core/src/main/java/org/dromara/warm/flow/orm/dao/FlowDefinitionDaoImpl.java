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

import org.dromara.warm.flow.core.dao.FlowDefinitionDao;
import org.dromara.warm.flow.core.enums.PublishStatus;
import org.dromara.warm.flow.orm.entity.FlowDefinition;
import org.dromara.warm.flow.orm.utils.TenantDeleteUtil;

import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import java.util.List;

/**
 * 流程定义Mapper接口
 *
 * @author vanlin
 * @since 2024-05-12
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

    @Override
    public void updatePublishStatus(List<Long> ids, Integer publishStatus) {
        final FlowDefinition entity = TenantDeleteUtil.getEntity(newEntity());

        final CriteriaUpdate<FlowDefinition> criteriaUpdate = createCriteriaUpdate((criteriaBuilder, root, predicates, innerCriteriaUpdate) -> {
            entity.commonPredicate().process(criteriaBuilder, root, predicates);

            predicates.add(createIn(criteriaBuilder, root, "id", ids));

            // 是否发布（0未发布 1已发布 9失效）
            innerCriteriaUpdate.set(root.get("isPublish"), publishStatus);
        });

        entityManager.createQuery(criteriaUpdate).executeUpdate();
    }

}
