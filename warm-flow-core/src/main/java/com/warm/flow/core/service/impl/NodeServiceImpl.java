package com.warm.flow.core.service.impl;

import com.warm.flow.core.dao.FlowNodeDao;
import com.warm.flow.core.entity.Node;
import com.warm.flow.core.orm.service.impl.WarmServiceImpl;
import com.warm.flow.core.service.NodeService;
import com.warm.tools.utils.CollUtil;

import java.util.Collections;
import java.util.List;

/**
 * 流程节点Service业务层处理
 *
 * @author warm
 * @date 2023-03-29
 */
public class NodeServiceImpl extends WarmServiceImpl<FlowNodeDao, Node> implements NodeService {

    @Override
    public NodeService setDao(FlowNodeDao warmDao) {
        this.warmDao = warmDao;
        return this;
    }

    @Override
    public List<Node> getByFlowCode(String flowCode) {
        return getDao().getByFlowCode(flowCode);
    }

    @Override
    public List<Node> getByNodeCodes(List<String> nodeCodes, Long definitionId) {
        return getDao().getByNodeCodes(nodeCodes, definitionId);
    }

    @Override
    public Node getByNodeCode(String nodeCode, Long definitionId) {
        List<Node> nodeCodes = getDao().getByNodeCodes(Collections.singletonList(nodeCode), definitionId);
        return CollUtil.getOne(nodeCodes);
    }

}
