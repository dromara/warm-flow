package com.warm.flow.orm.dao;

import com.warm.flow.core.FlowFactory;
import com.warm.flow.core.dao.FlowUserDao;
import com.warm.flow.core.invoker.FrameInvoker;
import com.warm.flow.orm.entity.FlowUser;
import com.warm.flow.orm.mapper.FlowUserMapper;
import com.warm.flow.orm.utils.TenantDeleteUtil;
import com.warm.flow.core.utils.StringUtils;

import java.util.List;

/**
 * 流程用户Mapper接口
 *
 * @author warm
 * @date 2023-03-29
 */
public class FlowUserDaoImpl extends WarmDaoImpl<FlowUser> implements FlowUserDao<FlowUser> {

    @Override
    public FlowUserMapper getMapper() {
        return FrameInvoker.getBean(FlowUserMapper.class);
    }

    @Override
    public FlowUser newEntity() {
        return new FlowUser();
    }

    @Override
    public int deleteByTaskIds(List<Long> taskIdList) {
        FlowUser entity = TenantDeleteUtil.getEntity(newEntity());
        if (StringUtils.isNotEmpty(entity.getDelFlag())) {
            getMapper().updateByTaskIdsLogic(taskIdList, entity, FlowFactory.getFlowConfig().getLogicDeleteValue(),
                    entity.getDelFlag());
        }
        return getMapper().deleteByTaskIds(taskIdList, entity);
    }

    @Override
    public List<FlowUser> listByAssociatedAndTypes(Long associated, String[] types) {
        return getMapper().listByAssociatedAndTypes(types, TenantDeleteUtil.getEntity(newEntity()).setAssociated(associated));
    }
}
