package com.warm.flow.orm.dao;

import com.warm.flow.core.FlowFactory;
import com.warm.flow.core.dao.FlowNodeDao;
import com.warm.flow.orm.entity.FlowNode;
import com.warm.flow.orm.utils.TenantDeleteUtil;
import com.warm.flow.core.utils.CollUtil;
import com.warm.flow.core.utils.StringUtils;

import javax.persistence.criteria.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Objects;


/**
 * 流程节点Mapper接口
 *
 * @author vanlin
 * @date 2024-05-12
 */
public class FlowNodeDaoImpl extends WarmDaoImpl<FlowNode> implements FlowNodeDao<FlowNode> {

    @Override
    public FlowNode newEntity() {
        return new FlowNode();
    }

    @Override
    public Class<FlowNode> entityClass() {
        return FlowNode.class;
    }

    @Override
    public List<FlowNode> getByNodeCodes(List<String> nodeCodes, Long definitionId) {
        FlowNode entity = TenantDeleteUtil.getEntity(newEntity());

        final CriteriaQuery<FlowNode> criteriaQuery = createCriteriaQuery((criteriaBuilder, root, predicates, innerCriteriaQuery) -> {
            entity.commonPredicate().process(criteriaBuilder, root, predicates);

            predicates.add(criteriaBuilder.equal(root.get("definitionId"), definitionId));

            if (CollUtil.isNotEmpty(nodeCodes)) {
                predicates.add(createIn(criteriaBuilder, root, "nodeCode", nodeCodes));
            }
        });
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    /**
     * 批量删除流程节点
     *
     * @param defIds 需要删除的数据主键集合
     * @return 结果
     */
    @Override
    public int deleteNodeByDefIds(Collection<? extends Serializable> defIds) {
        FlowNode entity = TenantDeleteUtil.getEntity(newEntity());
        if (StringUtils.isNotEmpty(entity.getDelFlag())) {
            CriteriaUpdate<FlowNode> criteriaUpdate = createCriteriaUpdate((criteriaBuilder, root, predicates, innerCriteriaUpdate) -> {

                entity.commonPredicate().process(criteriaBuilder, root, predicates);

                predicates.add(createIn(criteriaBuilder, root, "definitionId", defIds));

                // 更新值
                innerCriteriaUpdate.set(root.get("delFlag"),  FlowFactory.getFlowConfig().getLogicDeleteValue());
            });

            return entityManager.createQuery(criteriaUpdate).executeUpdate();
        } else {
            CriteriaDelete<FlowNode> criteriaDelete = createCriteriaDelete((criteriaBuilder, root, predicates) -> {
                predicates.add(createIn(criteriaBuilder, root, "definitionId", defIds));

                if (Objects.nonNull(entity.getTenantId())) {
                    predicates.add(criteriaBuilder.equal(root.get("tenantId"), entity.getTenantId()));
                }
            });
            return entityManager.createQuery(criteriaDelete).executeUpdate();
        }
    }

}
