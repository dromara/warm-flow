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

import org.dromara.warm.flow.core.FlowEngine;
import org.dromara.warm.flow.core.orm.dao.FlowUserDao;
import org.dromara.warm.flow.core.utils.ArrayUtil;
import org.dromara.warm.flow.core.utils.CollUtil;
import org.dromara.warm.flow.core.utils.StringUtils;
import org.dromara.warm.flow.orm.entity.FlowUser;
import org.dromara.warm.flow.orm.utils.TenantDeleteUtil;

import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import java.util.List;
import java.util.Objects;

/**
 * 流程用户Mapper接口
 *
 * @author vanlin
 * @since 2024-05-13
 */
public class FlowUserDaoImpl extends WarmDaoImpl<FlowUser> implements FlowUserDao<FlowUser> {

    @Override
    public FlowUser newEntity() {
        return new FlowUser();
    }

    @Override
    public Class<FlowUser> entityClass() {
        return FlowUser.class;
    }

    /**
     * 根据taskIdList删除
     *
     * @param taskIdList 主键
     * @return 结果
     */
    @Override
    public int deleteByTaskIds(List<Long> taskIdList) {
        FlowUser entity = TenantDeleteUtil.getEntity(newEntity());
        if (StringUtils.isNotEmpty(entity.getDelFlag())) {
            CriteriaUpdate<FlowUser> criteriaUpdate = createCriteriaUpdate((criteriaBuilder, root, predicates, innerCriteriaUpdate) -> {

                entity.commonPredicate().process(criteriaBuilder, root, predicates);

                predicates.add(createIn(criteriaBuilder, root, "associated", taskIdList));

                // 更新值
                innerCriteriaUpdate.set(root.get("delFlag"), FlowEngine.getFlowConfig().getLogicDeleteValue());
            });

            return entityManager.createQuery(criteriaUpdate).executeUpdate();
        } else {
            CriteriaDelete<FlowUser> criteriaDelete = createCriteriaDelete((criteriaBuilder, root, predicates) -> {
                predicates.add(createIn(criteriaBuilder, root, "associated", taskIdList));

                if (Objects.nonNull(entity.getTenantId())) {
                    predicates.add(criteriaBuilder.equal(root.get("tenantId"), entity.getTenantId()));
                }
            });
            return entityManager.createQuery(criteriaDelete).executeUpdate();
        }
    }

    @Override
    public List<FlowUser> listByAssociatedAndTypes(List<Long> associateds, String[] types) {

        FlowUser entity = TenantDeleteUtil.getEntity(newEntity());

        final CriteriaQuery<FlowUser> criteriaQuery = createCriteriaQuery((criteriaBuilder, root, predicates, innerCriteriaQuery) -> {
            entity.commonPredicate().process(criteriaBuilder, root, predicates);

            if (CollUtil.isNotEmpty(associateds)) {
                if (associateds.size() == 1) {
                    predicates.add(criteriaBuilder.equal(root.get("associated"), associateds.get(0)));
                } else {
                    predicates.add(createIn(criteriaBuilder, root, "associated", associateds));
                }
            }

            if (ArrayUtil.isNotEmpty(types)) {
                predicates.add(createIn(criteriaBuilder, root, "type", types));
            }

            orderBy(criteriaBuilder, root, innerCriteriaQuery, entity, null);
        });

        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    @Override
    public List<FlowUser> listByProcessedBys(Long associated, List<String> processedBys, String[] types) {

        FlowUser entity = TenantDeleteUtil.getEntity(newEntity());

        final CriteriaQuery<FlowUser> criteriaQuery = createCriteriaQuery((criteriaBuilder, root, predicates, innerCriteriaQuery) -> {
            entity.commonPredicate().process(criteriaBuilder, root, predicates);

            if (associated != null) {
                predicates.add(criteriaBuilder.equal(root.get("associated"), associated));
            }

            if (CollUtil.isNotEmpty(processedBys)) {
                if (processedBys.size() == 1) {
                    predicates.add(criteriaBuilder.equal(root.get("processed_by"), processedBys.get(0)));
                } else {
                    predicates.add(createIn(criteriaBuilder, root, "processed_by", processedBys));
                }
            }

            if (ArrayUtil.isNotEmpty(types)) {
                predicates.add(createIn(criteriaBuilder, root, "type", types));
            }

            orderBy(criteriaBuilder, root, innerCriteriaQuery, entity, null);
        });

        return entityManager.createQuery(criteriaQuery).getResultList();
    }

}
