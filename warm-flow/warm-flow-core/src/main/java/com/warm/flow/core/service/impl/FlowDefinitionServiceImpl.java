package com.warm.flow.core.service.impl;

import com.warm.flow.core.constant.FlowConstant;
import com.warm.flow.core.domain.dto.FlowCombine;
import com.warm.flow.core.domain.entity.FlowDefinition;
import com.warm.flow.core.domain.entity.FlowNode;
import com.warm.flow.core.domain.entity.FlowSkip;
import com.warm.flow.core.enums.PublishStatus;
import com.warm.flow.core.exception.FlowException;
import com.warm.flow.core.mapper.FlowDefinitionMapper;
import com.warm.flow.core.service.IFlowDefinitionService;
import com.warm.flow.core.utils.FlowConfigUtil;
import com.warm.mybatis.core.service.impl.FlowServiceImpl;
import org.dom4j.Document;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.warm.flow.core.FlowFactory.flowFactory;

/**
 * 流程定义Service业务层处理
 *
 * @author hh
 * @date 2023-03-29
 */
public class FlowDefinitionServiceImpl extends FlowServiceImpl<FlowDefinition> implements IFlowDefinitionService {
    @Resource
    private FlowDefinitionMapper definitionMapper;

    @Override
    public FlowDefinitionMapper getBaseMapper() {
        return definitionMapper;
    }

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

    public FlowDefinition getAllDataDefinition(Long id) {
        FlowDefinition definition = flowFactory.defService().getById(id);
        FlowNode node = new FlowNode();
        node.setDefinitionId(id);
        List<FlowNode> nodeList = flowFactory.nodeService().list(node);
        definition.setNodeList(nodeList);
        FlowSkip flowSkip = new FlowSkip();
        flowSkip.setDefinitionId(id);
        List<FlowSkip> flowSkips = flowFactory.skipService().list(flowSkip);
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
        List<FlowDefinition> flowDefinitions = flowFactory.defService().queryByCodeList(flowCodeList);
        for (int j = 0; j < flowDefinitions.size(); j++) {
            FlowDefinition beforeDefinition = flowDefinitions.get(j);
            if (definition.getFlowCode().equals(beforeDefinition.getFlowCode()) && definition.getVersion().equals(beforeDefinition.getVersion())) {
                throw new FlowException(definition.getFlowCode() + "(" + definition.getVersion() + ")" + FlowConstant.ALREADY_EXIST);
            }
        }
        flowFactory.defService().save(definition);
        flowFactory.nodeService().saveBatch(allNodes);
        flowFactory.skipService().saveBatch(allSkips);
    }

    @Override
    public List<FlowDefinition> queryByCodeList(List<String> flowCodeList) {
        return definitionMapper.queryByCodeList(flowCodeList);
    }

    @Override
    public void closeFlowByCodeList(List<String> flowCodeList) {
        definitionMapper.closeFlowByCodeList(flowCodeList);
    }

    @Override
    public boolean checkAndSave(FlowDefinition flowDefinition) {
        List<String> flowCodeList = Arrays.asList(flowDefinition.getFlowCode());
        List<FlowDefinition> flowDefinitions = queryByCodeList(flowCodeList);
        for (FlowDefinition beforeDefinition : flowDefinitions) {
            if (flowDefinition.getFlowCode().equals(beforeDefinition.getFlowCode()) && flowDefinition.getVersion().equals(beforeDefinition.getVersion())) {
                throw new FlowException(flowDefinition.getFlowCode() + "(" + flowDefinition.getVersion() + ")" + FlowConstant.ALREADY_EXIST);
            }
        }
        return save(flowDefinition);
    }

    /**
     * 删除流程定义
     *
     * @param ids
     */
    @Override
    public boolean removeDef(List<Long> ids) {
        definitionMapper.deleteNodeByDefIds(ids);
        definitionMapper.deleteSkipByDefIds(ids);
        return removeByIds(ids);
    }

    @Override
    public boolean publish(Long id) {
        FlowDefinition definition = getById(id);
        List<String> flowCodeList = Arrays.asList(definition.getFlowCode());
        // 把之前的流程定义改为已失效
        closeFlowByCodeList(flowCodeList);

        FlowDefinition flowDefinition = new FlowDefinition();
        flowDefinition.setId(id);
        flowDefinition.setIsPublish(PublishStatus.PUBLISHED.getKey());
        return updateById(flowDefinition);
    }

    @Override
    public boolean unPublish(Long id) {
        FlowDefinition flowDefinition = new FlowDefinition();
        flowDefinition.setId(id);
        flowDefinition.setIsPublish(PublishStatus.UNPUBLISHED.getKey());
        return updateById(flowDefinition);
    }

}
