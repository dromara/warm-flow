package com.warm.flow.orm.dao;

import com.mybatisflex.core.query.QueryWrapper;
import com.warm.flow.core.dao.FlowUserDao;
import com.warm.flow.core.invoker.FrameInvoker;
import com.warm.flow.core.utils.CollUtil;
import com.warm.flow.core.utils.ObjectUtil;
import com.warm.flow.orm.entity.FlowUser;
import com.warm.flow.orm.mapper.FlowUserMapper;
import com.warm.flow.orm.utils.TenantDeleteUtil;

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
        return delete(newEntity(), (luw) -> luw.in(FlowUser::getAssociated, taskIdList)
                , (lqw) -> lqw.in(FlowUser::getAssociated, taskIdList));
    }

    @Override
    public List<FlowUser> listByAssociatedAndTypes(List<Long> associatedList, String[] types) {
        QueryWrapper queryWrapper = TenantDeleteUtil.getDefaultWrapper(newEntity());
        if (CollUtil.isNotEmpty(associatedList)) {
            if (associatedList.size() == 1) {
                queryWrapper.eq(FlowUser::getAssociated, associatedList.get(0));
            } else {
                queryWrapper.in(FlowUser::getAssociated, associatedList);
            }
        }
        queryWrapper.in(FlowUser::getType, CollUtil.toList(types));
        return getMapper().selectListByQuery(queryWrapper);
    }

    @Override
    public List<FlowUser> listByProcessedBys(Long associated, List<String> processedBys, String[] types) {
        QueryWrapper queryWrapper = TenantDeleteUtil.getDefaultWrapper(newEntity());
        queryWrapper.eq(FlowUser::getAssociated, associated, ObjectUtil.isNotNull(associated));
        if (CollUtil.isNotEmpty(processedBys)) {
            if (processedBys.size() == 1) {
                queryWrapper.eq(FlowUser::getProcessedBy, processedBys.get(0));
            } else {
                queryWrapper.in(FlowUser::getProcessedBy, processedBys);
            }
        }
        queryWrapper.in(FlowUser::getType, CollUtil.toList(types));
        return getMapper().selectListByQuery(queryWrapper);
    }

}
