package com.monkey.flow.core.service.impl;

import com.monkey.flow.core.domain.entity.FlowSkip;
import com.monkey.flow.core.mapper.FlowSkipMapper;
import com.monkey.flow.core.service.IFlowSkipService;
import com.monkey.mybatis.core.service.impl.FlowServiceImpl;
import com.monkey.mybatis.core.utils.SqlHelper;

import javax.annotation.Resource;
import java.util.List;

/**
 * 结点跳转关联Service业务层处理
 *
 * @author hh
 * @date 2023-03-29
 */
public class FlowSkipServiceImpl extends FlowServiceImpl<FlowSkip> implements IFlowSkipService {
    @Resource
    private FlowSkipMapper flowSkipMapper;

    @Override
    public FlowSkipMapper getBaseMapper() {
        return flowSkipMapper;
    }

    @Override
    public boolean deleteByNodeId(Long nodeId) {
        return SqlHelper.retBool(flowSkipMapper.deleteByNodeId(nodeId));
    }

    /**
     * 根据nodeIds删除
     *
     * @param nodeIds 需要删除的nodeIds
     * @return 结果
     */
    @Override
    public boolean deleteByNodeIds(List<Long> nodeIds)
    {
        return SqlHelper.retBool(flowSkipMapper.deleteByNodeIds(nodeIds));
    }

}
