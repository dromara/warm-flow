package com.warm.flow.orm.dao;

import com.warm.flow.core.dao.FlowDefinitionDao;
import com.warm.flow.orm.entity.FlowDefinition;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

/**
 * 流程定义Mapper接口
 *
 * @author vanlin
 * @date 2024-05-12
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
//        return getMapper().queryByCodeList(flowCodeList, TenantDeleteUtil.getEntity(newEntity()));
        return null;
    }

    public void closeFlowByCodeList(List<String> flowCodeList) {
//        getMapper().closeFlowByCodeList(flowCodeList, TenantDeleteUtil.getEntity(newEntity()));
    }

}
