package com.warm.flow.orm.dao;

import com.warm.flow.core.FlowFactory;
import com.warm.flow.core.dao.FlowUserDao;
import com.warm.flow.core.utils.CollUtil;
import com.warm.flow.core.utils.StringUtils;
import com.warm.flow.orm.entity.FlowUser;
import com.warm.flow.orm.utils.TenantDeleteUtil;

import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaUpdate;
import java.util.List;
import java.util.Objects;

/**
 * 流程用户Mapper接口
 *
 * @author vanlin
 * @date 2024-05-13
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
                innerCriteriaUpdate.set(root.get("delFlag"),  FlowFactory.getFlowConfig().getLogicDeleteValue());
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

            if (CollUtil.isNotEmpty(associateds) && associateds.size() == 1) {
                predicates.add(criteriaBuilder.equal(root.get("associated"), associateds.get(0)));
            } else {
                predicates.add(createIn(criteriaBuilder, root, "associated", associateds));
            }

            if (types != null && types.length > 0) {
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

            if (CollUtil.isNotEmpty(processedBys) && processedBys.size() == 1) {
                predicates.add(criteriaBuilder.equal(root.get("processed_by"), processedBys.get(0)));
            } else {
                predicates.add(createIn(criteriaBuilder, root, "processed_by", processedBys));
            }

            if (types != null && types.length > 0) {
                predicates.add(createIn(criteriaBuilder, root, "type", types));
            }

            orderBy(criteriaBuilder, root, innerCriteriaQuery, entity, null);
        });

        return entityManager.createQuery(criteriaQuery).getResultList();
    }

}
