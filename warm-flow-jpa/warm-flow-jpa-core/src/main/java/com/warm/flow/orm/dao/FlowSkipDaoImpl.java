package com.warm.flow.orm.dao;

import com.warm.flow.core.FlowFactory;
import com.warm.flow.core.dao.FlowSkipDao;
import com.warm.flow.core.utils.StringUtils;
import com.warm.flow.orm.entity.FlowSkip;
import com.warm.flow.orm.utils.TenantDeleteUtil;

import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaUpdate;
import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;

/**
 * 节点跳转关联Mapper接口
 *
 * @author vanlin
 * @date 2024-05-12
 */
public class FlowSkipDaoImpl extends WarmDaoImpl<FlowSkip> implements FlowSkipDao<FlowSkip> {

    @Override
    public FlowSkip newEntity() {
        return new FlowSkip();
    }

    @Override
    public Class<FlowSkip> entityClass() {
        return FlowSkip.class;
    }

    /**
     * 批量删除节点跳转关联
     *
     * @param defIds 需要删除的数据主键集合
     * @return 结果
     */
    @Override
    public int deleteSkipByDefIds(Collection<? extends Serializable> defIds) {
        FlowSkip entity = TenantDeleteUtil.getEntity(newEntity());
        if (StringUtils.isNotEmpty(entity.getDelFlag())) {
            CriteriaUpdate<FlowSkip> criteriaUpdate = createCriteriaUpdate((criteriaBuilder, root, predicates, innerCriteriaUpdate) -> {

                entity.commonPredicate().process(criteriaBuilder, root, predicates);

                predicates.add(createIn(criteriaBuilder, root, "definitionId", defIds));

                // 更新值
                innerCriteriaUpdate.set(root.get("delFlag"), FlowFactory.getFlowConfig().getLogicDeleteValue());
            });

            return entityManager.createQuery(criteriaUpdate).executeUpdate();
        } else {
            CriteriaDelete<FlowSkip> criteriaDelete = createCriteriaDelete((criteriaBuilder, root, predicates) -> {
                predicates.add(createIn(criteriaBuilder, root, "definitionId", defIds));

                if (Objects.nonNull(entity.getTenantId())) {
                    predicates.add(criteriaBuilder.equal(root.get("tenantId"), entity.getTenantId()));
                }
            });
            return entityManager.createQuery(criteriaDelete).executeUpdate();
        }
    }

}
