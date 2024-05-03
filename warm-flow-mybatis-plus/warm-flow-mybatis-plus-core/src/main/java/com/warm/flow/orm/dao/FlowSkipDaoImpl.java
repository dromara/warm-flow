package com.warm.flow.orm.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.warm.flow.core.dao.FlowSkipDao;
import com.warm.flow.core.invoker.FrameInvoker;
import com.warm.flow.orm.entity.FlowNode;
import com.warm.flow.orm.entity.FlowSkip;
import com.warm.flow.orm.mapper.FlowSkipMapper;
import com.warm.flow.orm.utils.TenantDeleteUtil;

import java.io.Serializable;
import java.util.Collection;

/**
 * 节点跳转关联Mapper接口
 *
 * @author warm
 * @date 2023-03-29
 */
public class FlowSkipDaoImpl extends WarmDaoImpl<FlowSkip> implements FlowSkipDao<FlowSkip> {

    @Override
    public FlowSkipMapper getMapper() {
        return FrameInvoker.getBean(FlowSkipMapper.class);
    }

    @Override
    public FlowSkip newEntity() {
        return new FlowSkip();
    }

    /**
     * 批量删除节点跳转关联
     *
     * @param defIds 需要删除的数据主键集合
     * @return 结果
     */
    @Override
    public int deleteSkipByDefIds(Collection<? extends Serializable> defIds) {
        return delete(newEntity(), (luw) -> luw.in(FlowSkip::getDefinitionId, defIds)
                , (lqw) -> lqw.in(FlowSkip::getDefinitionId, defIds));
    }
}
