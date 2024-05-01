package com.warm.flow.orm.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.warm.flow.core.dao.FlowSkipDao;
import com.warm.flow.core.invoker.FrameInvoker;
import com.warm.flow.orm.entity.FlowSkip;
import com.warm.flow.orm.mapper.FlowSkipMapper;

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

    /**
     * 批量删除节点跳转关联
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    @Override
    public int deleteSkipByDefIds(Collection<? extends Serializable> ids) {
        LambdaQueryWrapper<FlowSkip> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(FlowSkip::getDefinitionId, ids);
        return getMapper().delete(queryWrapper);
    }
}
