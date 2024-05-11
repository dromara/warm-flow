package com.warm.flow.orm.dao;

import com.warm.flow.core.dao.FlowTaskDao;
import com.warm.flow.orm.entity.FlowTask;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

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
       /* FlowTask entity = TenantDeleteUtil.getEntity(newEntity());
        if (StringUtils.isNotEmpty(entity.getDelFlag())) {
            getMapper().updateByInsIdsLogic(instanceIds, entity, FlowFactory.getFlowConfig().getLogicDeleteValue(), entity.getDelFlag());
        }
        return getMapper().deleteByInsIds(instanceIds, entity);*/
        return 0;
    }

}
