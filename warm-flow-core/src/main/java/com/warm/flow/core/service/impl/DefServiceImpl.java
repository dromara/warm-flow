package com.warm.flow.core.service.impl;

import com.warm.flow.core.FlowFactory;
import com.warm.flow.core.constant.ExceptionCons;
import com.warm.flow.core.domain.dto.FlowCombine;
import com.warm.flow.core.domain.entity.FlowDefinition;
import com.warm.flow.core.domain.entity.FlowNode;
import com.warm.flow.core.domain.entity.FlowSkip;
import com.warm.flow.core.enums.PublishStatus;
import com.warm.flow.core.exception.FlowException;
import com.warm.flow.core.mapper.FlowDefinitionMapper;
import com.warm.flow.core.mapper.FlowHisTaskMapper;
import com.warm.flow.core.service.DefService;
import com.warm.flow.core.utils.FlowConfigUtil;
import com.warm.mybatis.core.service.impl.WarmServiceImpl;
import org.dom4j.Document;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 流程定义Service业务层处理
 *
 * @author warm
 * @date 2023-03-29
 */
public class DefServiceImpl extends WarmServiceImpl<FlowDefinitionMapper, FlowDefinition> implements DefService {

    @Override
    public Class<FlowDefinitionMapper> getMapperClass() {
        return FlowDefinitionMapper.class;
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
        FlowDefinition definition = getMapper().selectById(id);
        FlowNode node = new FlowNode();
        node.setDefinitionId(id);
        List<FlowNode> nodeList = FlowFactory.nodeService().list(node);
        definition.setNodeList(nodeList);
        FlowSkip flowSkip = new FlowSkip();
        flowSkip.setDefinitionId(id);
        List<FlowSkip> flowSkips = FlowFactory.skipService().list(flowSkip);
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
        List<String> flowCodeList = Collections.singletonList(definition.getFlowCode());
        List<FlowDefinition> flowDefinitions = getMapper().queryByCodeList(flowCodeList);
        for (int j = 0; j < flowDefinitions.size(); j++) {
            FlowDefinition beforeDefinition = flowDefinitions.get(j);
            if (definition.getFlowCode().equals(beforeDefinition.getFlowCode()) && definition.getVersion().equals(beforeDefinition.getVersion())) {
                throw new FlowException(definition.getFlowCode() + "(" + definition.getVersion() + ")" + ExceptionCons.ALREADY_EXIST);
            }
        }
        FlowFactory.defService().save(definition);
        FlowFactory.nodeService().saveBatch(allNodes);
        FlowFactory.skipService().saveBatch(allSkips);
    }

    @Override
    public List<FlowDefinition> queryByCodeList(List<String> flowCodeList) {
        return getMapper().queryByCodeList(flowCodeList);
    }

    @Override
    public void closeFlowByCodeList(List<String> flowCodeList) {
        getMapper().closeFlowByCodeList(flowCodeList);
    }

    @Override
    public boolean checkAndSave(FlowDefinition flowDefinition) {
        List<String> flowCodeList = Collections.singletonList(flowDefinition.getFlowCode());
        List<FlowDefinition> flowDefinitions = queryByCodeList(flowCodeList);
        for (FlowDefinition beforeDefinition : flowDefinitions) {
            if (flowDefinition.getFlowCode().equals(beforeDefinition.getFlowCode()) && flowDefinition.getVersion().equals(beforeDefinition.getVersion())) {
                throw new FlowException(flowDefinition.getFlowCode() + "(" + flowDefinition.getVersion() + ")" + ExceptionCons.ALREADY_EXIST);
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
        getMapper().deleteNodeByDefIds(ids);
        getMapper().deleteSkipByDefIds(ids);
        return removeByIds(ids);
    }

    @Override
    public boolean publish(Long id) {
        FlowDefinition definition = getById(id);
        List<String> flowCodeList = Collections.singletonList(definition.getFlowCode());
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
