package com.warm.flow.orm.dao;

import com.warm.flow.core.FlowFactory;
import com.warm.flow.core.dao.FlowTaskDao;
import com.warm.flow.core.utils.StringUtils;
import com.warm.flow.orm.entity.FlowTask;
import com.warm.flow.orm.utils.TenantDeleteUtil;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaUpdate;

import java.util.List;
import java.util.Objects;

/**
 * 待办任务Mapper接口
 *
 * @author vanlin
 * @date 2024-05-12
 */
public class FlowTaskDaoImpl extends WarmDaoImpl<FlowTask> implements FlowTaskDao<FlowTask> {

    @Override
    public FlowTask newEntity() {
        return new FlowTask();
    }

    @Override
    public Class<FlowTask> entityClass() {
        return FlowTask.class;
    }

    /**
     * 根据instanceIds删除
     *
     * @param instanceIds 主键
     * @return 结果
     */
    @Override
    public int deleteByInsIds(List<Long> instanceIds) {
        FlowTask entity = TenantDeleteUtil.getEntity(newEntity());
        if (StringUtils.isNotEmpty(entity.getDelFlag())) {
            CriteriaUpdate<FlowTask> criteriaUpdate = createCriteriaUpdate((criteriaBuilder, root, predicates, innerCriteriaUpdate) -> {

                entity.commonPredicate().process(criteriaBuilder, root, predicates);

                predicates.add(createIn(criteriaBuilder, root, "instanceId", instanceIds));

                // 更新值
                innerCriteriaUpdate.set(root.get("delFlag"), FlowFactory.getFlowConfig().getLogicDeleteValue());
            });

            return entityManager.createQuery(criteriaUpdate).executeUpdate();
        } else {
            CriteriaDelete<FlowTask> criteriaDelete = createCriteriaDelete((criteriaBuilder, root, predicates) -> {
                predicates.add(createIn(criteriaBuilder, root, "instanceId", instanceIds));

                if (Objects.nonNull(entity.getTenantId())) {
                    predicates.add(criteriaBuilder.equal(root.get("tenantId"), entity.getTenantId()));
                }
            });
            return entityManager.createQuery(criteriaDelete).executeUpdate();
        }
    }

}
