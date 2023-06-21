package com.monkey.flow.core.webService;

import com.monkey.flow.core.DefAppService;
import com.monkey.flow.core.constant.FlowConstant;
import com.monkey.flow.core.domain.dto.FlowCombine;
import com.monkey.flow.core.domain.entity.FlowDefinition;
import com.monkey.flow.core.domain.entity.FlowNode;
import com.monkey.flow.core.domain.entity.FlowSkip;
import com.monkey.flow.core.exception.FlowException;
import com.monkey.flow.core.service.IFlowDefinitionService;
import com.monkey.flow.core.service.IFlowNodeService;
import com.monkey.flow.core.service.IFlowSkipService;
import com.monkey.flow.core.utils.FlowConfigUtil;
import org.dom4j.Document;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author minliuhua
 * @description: 流程定义对外提供
 * @date: 2023/3/30 15:24
 */
public class DefAppServiceImpl implements DefAppService {

    @Resource
    private IFlowNodeService nodeService;

    @Resource
    private IFlowSkipService skipService;

    @Resource
    private IFlowDefinitionService definitionService;

    @Override
    public IFlowDefinitionService getService(){
        return definitionService;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void importXml(InputStream is) throws Exception {
        FlowCombine combine = FlowConfigUtil.readConfig(is);
        // 流程定义
        FlowDefinition definition = combine.getDefinition();
        // 所有的流程结点
        List<FlowNode> allNodes = combine.getAllNodes();
        // 所有的流程连线
        List<FlowSkip> allSkips = combine.getAllSkips();
        // 根据不同策略进行新增或者更新
        updateFlow(definition, allNodes, allSkips);
    }

    @Override
    public Document exportXml(Long id) {
        FlowDefinition definition = getAllDataDefinition(id);
        return FlowConfigUtil.createDocument(definition);
    }

    @Override
    public FlowDefinition getAllDataDefinition(Long id) {
        FlowDefinition definition = definitionService.getById(id);
        FlowNode node = new FlowNode();
        node.setDefinitionId(id);
        List<FlowNode> nodeList = nodeService.list(node);
        definition.setNodeList(nodeList);
        FlowSkip flowSkip = new FlowSkip();
        flowSkip.setDefinitionId(id);
        List<FlowSkip> flowSkips = skipService.list(flowSkip);
        Map<Long, List<FlowSkip>> flowSkipMap = flowSkips.stream()
                .collect(Collectors.groupingBy(FlowSkip::getNodeId));
        nodeList.forEach(flowNode -> flowNode.setSkipList(flowSkipMap.get(flowNode.getId())));

        return definition;
    }

    /**
     * 每次只做新增操作,保证新增的flowCode+version是唯一的
     *
     * @param definition
     * @param allNodes
     * @param allSkips
     */
    private void updateFlow(FlowDefinition definition, List<FlowNode> allNodes, List<FlowSkip> allSkips) {
        List<String> flowCodeList = Arrays.asList(definition.getFlowCode());
        List<FlowDefinition> flowDefinitions = definitionService.queryByCodeList(flowCodeList);
        for (int j = 0; j < flowDefinitions.size(); j++) {
            FlowDefinition beforeDefinition = flowDefinitions.get(j);
            if (definition.getFlowCode().equals(beforeDefinition.getFlowCode()) && definition.getVersion().equals(beforeDefinition.getVersion())) {
                throw new FlowException(definition.getFlowCode() + "(" + definition.getVersion() + ")" + FlowConstant.ALREADY_EXIST);
            }
        }
        definitionService.save(definition);
        nodeService.saveBatch(allNodes);
        skipService.saveBatch(allSkips);
    }
}
