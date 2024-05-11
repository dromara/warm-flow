package com.warm.flow.orm.dao;

import com.warm.flow.core.dao.FlowNodeDao;
import com.warm.flow.orm.entity.FlowDefinition;
import com.warm.flow.orm.entity.FlowNode;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


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
//        return getMapper().getByNodeCodes(nodeCodes, definitionId, TenantDeleteUtil.getEntity(newEntity()));
        return null;
    }

    /**
     * 批量删除流程节点
     *
     * @param defIds 需要删除的数据主键集合
     * @return 结果
     */
    @Override
    public int deleteNodeByDefIds(Collection<? extends Serializable> defIds) {
       /* FlowNode entity = TenantDeleteUtil.getEntity(newEntity());
        if (StringUtils.isNotEmpty(entity.getDelFlag())) {
            getMapper().updateNodeByDefIdsLogic(defIds, entity, FlowFactory.getFlowConfig().getLogicDeleteValue(), entity.getDelFlag());
        }
        return getMapper().deleteNodeByDefIds(defIds, entity);*/
        return 0;
    }

}
