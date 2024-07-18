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
package com.warm.flow.orm.dao;

import com.warm.flow.core.FlowFactory;
import com.warm.flow.core.dao.FlowHisTaskDao;
import com.warm.flow.core.enums.FlowStatus;
import com.warm.flow.core.enums.SkipType;
import com.warm.flow.core.utils.StringUtils;
import com.warm.flow.orm.entity.FlowHisTask;
import com.warm.flow.orm.utils.TenantDeleteUtil;

import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import java.util.List;
import java.util.Objects;

/**
 * 历史任务记录Mapper接口
 *
 * @author vanlin
 * @date 2024-05-12
 */
public class FlowHisTaskDaoImpl extends WarmDaoImpl<FlowHisTask> implements FlowHisTaskDao<FlowHisTask> {

    @Override
    public FlowHisTask newEntity() {
        return new FlowHisTask();
    }

    @Override
    public Class<FlowHisTask> entityClass() {
        return FlowHisTask.class;
    }

    /**
     * 根据nodeCode获取未退回的历史记录
     *
     * @param nodeCode
     * @param instanceId
     * @return
     */
    @Override
    public List<FlowHisTask> getNoReject(String nodeCode, String targetNodeCode, Long instanceId) {
        FlowHisTask entity = TenantDeleteUtil.getEntity(newEntity());

        final CriteriaQuery<FlowHisTask> criteriaQuery = createCriteriaQuery((criteriaBuilder, root, predicates, innerCriteriaQuery) -> {
            entity.commonPredicate().process(criteriaBuilder, root, predicates);

            predicates.add(criteriaBuilder.equal(root.get("nodeCode"), nodeCode));
            if (StringUtils.isNotEmpty(targetNodeCode)) {
                predicates.add(criteriaBuilder.equal(root.get("targetNodeCode"), targetNodeCode));
            }
            predicates.add(criteriaBuilder.equal(root.get("instanceId"), instanceId));

            // 流程流转（PASS REJECT NONE）
            predicates.add(criteriaBuilder.equal(root.get("skipType"), SkipType.PASS.getKey()));

            // orderBy
            innerCriteriaQuery.orderBy(criteriaBuilder.desc(root.get("createTime")));
        });
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    /**
     * 根据instanceIds删除
     *
     * @param instanceIds 主键
     * @return 结果
     */
    @Override
    public int deleteByInsIds(List<Long> instanceIds) {

        FlowHisTask entity = TenantDeleteUtil.getEntity(newEntity());
        if (StringUtils.isNotEmpty(entity.getDelFlag())) {
            CriteriaUpdate<FlowHisTask> criteriaUpdate = createCriteriaUpdate((criteriaBuilder, root, predicates, innerCriteriaUpdate) -> {

                entity.commonPredicate().process(criteriaBuilder, root, predicates);
                predicates.add(createIn(criteriaBuilder, root, "instanceId", instanceIds));

                // 更新值
                innerCriteriaUpdate.set(root.get("delFlag"), FlowFactory.getFlowConfig().getLogicDeleteValue());
            });

            return entityManager.createQuery(criteriaUpdate).executeUpdate();
        } else {
            CriteriaDelete<FlowHisTask> criteriaDelete = createCriteriaDelete((criteriaBuilder, root, predicates) -> {
                predicates.add(createIn(criteriaBuilder, root, "instanceId", instanceIds));

                if (Objects.nonNull(entity.getTenantId())) {
                    predicates.add(criteriaBuilder.equal(root.get("tenantId"), entity.getTenantId()));
                }
            });
            return entityManager.createQuery(criteriaDelete).executeUpdate();
        }

    }

    @Override
    public List<FlowHisTask> listByTaskIdAndCooperateTypes(Long taskId, Integer[] cooperateTypes) {
        FlowHisTask entity = TenantDeleteUtil.getEntity(newEntity());

        final CriteriaQuery<FlowHisTask> criteriaQuery = createCriteriaQuery((criteriaBuilder, root, predicates, innerCriteriaQuery) -> {
            entity.commonPredicate().process(criteriaBuilder, root, predicates);

            predicates.add(criteriaBuilder.equal(root.get("taskId"), taskId));
            predicates.add(createIn(criteriaBuilder, root, "cooperateType", cooperateTypes));
        });
        return entityManager.createQuery(criteriaQuery).getResultList();
    }


}
