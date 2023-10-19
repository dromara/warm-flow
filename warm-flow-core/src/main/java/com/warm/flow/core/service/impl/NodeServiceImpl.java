package com.warm.flow.core.service.impl;

import com.warm.flow.core.domain.entity.FlowNode;
import com.warm.flow.core.mapper.FlowNodeMapper;
import com.warm.flow.core.service.NodeService;
import com.warm.mybatis.core.service.impl.WarmServiceImpl;
import com.warm.tools.utils.CollUtil;

import java.util.Collections;
import java.util.List;

/**
 * 流程节点Service业务层处理
 *
 * @author warm
 * @date 2023-03-29
 */
public class NodeServiceImpl extends WarmServiceImpl<FlowNodeMapper, FlowNode> implements NodeService {

    @Override
    public Class<FlowNodeMapper> getMapperClass() {
        return FlowNodeMapper.class;
    }

    @Override
    public List<FlowNode> getByFlowCode(String flowCode) {
        return getMapper().getByFlowCode(flowCode);
    }

    @Override
    public List<FlowNode> getByNodeCodes(List<String> nodeCodes, Long definitionId) {
        return getMapper().getByNodeCodes(nodeCodes, definitionId);
    }

    @Override
    public FlowNode getByNodeCode(String nodeCode, Long definitionId) {
        List<FlowNode> nodeCodes = getMapper().getByNodeCodes(Collections.singletonList(nodeCode), definitionId);
        return CollUtil.getOne(nodeCodes);
    }

}
