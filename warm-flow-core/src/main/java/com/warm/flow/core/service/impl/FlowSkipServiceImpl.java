package com.warm.flow.core.service.impl;

import com.warm.flow.core.domain.entity.FlowSkip;
import com.warm.flow.core.mapper.FlowSkipMapper;
import com.warm.flow.core.service.IFlowSkipService;
import com.warm.mybatis.core.invoker.MapperInvoker;
import com.warm.mybatis.core.service.impl.FlowServiceImpl;
import com.warm.mybatis.core.utils.SqlHelper;

import java.util.List;

/**
 * 结点跳转关联Service业务层处理
 *
 * @author hh
 * @date 2023-03-29
 */
public class FlowSkipServiceImpl extends FlowServiceImpl<FlowSkipMapper, FlowSkip> implements IFlowSkipService {

    @Override
    public boolean deleteByNodeId(Long nodeId) {
        Integer result = MapperInvoker.have(baseMapper -> baseMapper.deleteByNodeId(nodeId), mapperClass());
        return SqlHelper.retBool(result);
    }

    /**
     * 根据nodeIds删除
     *
     * @param nodeIds 需要删除的nodeIds
     * @return 结果
     */
    @Override
    public boolean deleteByNodeIds(List<Long> nodeIds) {
        Integer result = MapperInvoker.have(baseMapper -> baseMapper.deleteByNodeIds(nodeIds), mapperClass());
        return SqlHelper.retBool(result);
    }

}
