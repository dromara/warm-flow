package com.warm.flow.core.service.impl;

import com.warm.flow.core.domain.entity.FlowNode;
import com.warm.flow.core.mapper.FlowNodeMapper;
import com.warm.flow.core.service.NodeService;
import com.warm.mybatis.core.service.impl.WarmServiceImpl;

import java.util.List;

/**
 * 流程结点Service业务层处理
 *
 * @author hh
 * @date 2023-03-29
 */
public class NodeServiceImpl extends WarmServiceImpl<FlowNodeMapper, FlowNode> implements NodeService {

    @Override
    public List<FlowNode> getLastByFlowCode(String flowCode) {
        return getMapper().getLastByFlowCode(flowCode);
    }

}
