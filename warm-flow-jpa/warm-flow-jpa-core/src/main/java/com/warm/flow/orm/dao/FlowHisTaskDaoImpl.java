package com.warm.flow.orm.dao;

import com.warm.flow.core.dao.FlowHisTaskDao;
import com.warm.flow.orm.entity.FlowHisTask;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

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
    public List<FlowHisTask> getNoReject(String nodeCode, Long instanceId) {
//        return getMapper().getNoReject(nodeCode, instanceId, TenantDeleteUtil.getEntity(newEntity()));
        return null;
    }

    /**
     * 根据instanceIds删除
     *
     * @param instanceIds 主键
     * @return 结果
     */
    @Override
    public int deleteByInsIds(List<Long> instanceIds) {
        /*FlowHisTask entity = TenantDeleteUtil.getEntity(newEntity());
        if (StringUtils.isNotEmpty(entity.getDelFlag())) {
            getMapper().updateByInsIdsLogic(instanceIds, entity, FlowFactory.getFlowConfig().getLogicDeleteValue(), entity.getDelFlag());
        }
        return getMapper().deleteByInsIds(instanceIds, entity);*/
        return 0;
    }


}
