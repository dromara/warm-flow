package com.monkey.flow.core.service.impl;

import com.monkey.flow.core.domain.entity.FlowNode;
import com.monkey.flow.core.mapper.FlowNodeMapper;
import com.monkey.flow.core.service.IFlowNodeService;
import com.monkey.mybatis.core.service.impl.FlowBaseServiceImpl;

import javax.annotation.Resource;
import java.util.List;

/**
 * 流程结点Service业务层处理
 *
 * @author hh
 * @date 2023-03-29
 */
public class FlowNodeServiceImpl extends FlowBaseServiceImpl<FlowNode> implements IFlowNodeService {
    @Resource
    private FlowNodeMapper nodeMapper;

    @Override
    public FlowNodeMapper getBaseMapper() {
        return nodeMapper;
    }

    @Override
    public List<FlowNode> queryNewVersionFlowNodeByFlowCode(String flowCode) {
        return nodeMapper.queryNewVersionFlowNodeByFlowCode(flowCode);
    }

}
