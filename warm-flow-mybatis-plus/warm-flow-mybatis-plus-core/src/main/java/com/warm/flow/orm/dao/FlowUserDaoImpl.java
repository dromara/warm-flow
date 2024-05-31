package com.warm.flow.orm.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.warm.flow.core.dao.FlowUserDao;
import com.warm.flow.core.invoker.FrameInvoker;
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
    public List<FlowUser> listByAssociatedAndTypes(Long associated, String[] types) {
        LambdaQueryWrapper<FlowUser> queryWrapper = TenantDeleteUtil.getLambdaWrapperDefault(newEntity());
        queryWrapper.eq(FlowUser::getAssociated, associated).in(FlowUser::getType, types);
        return getMapper().selectList(queryWrapper);
    }
}
