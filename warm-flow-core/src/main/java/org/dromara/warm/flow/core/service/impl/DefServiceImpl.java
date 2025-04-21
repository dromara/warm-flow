/*
 *    Copyright 2024-2025, Warm-Flow (290631660@qq.com).
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.dromara.warm.flow.core.service.impl;

import org.dromara.warm.flow.core.FlowEngine;
import org.dromara.warm.flow.core.constant.ExceptionCons;
import org.dromara.warm.flow.core.dto.DefJson;
import org.dromara.warm.flow.core.dto.FlowCombine;
import org.dromara.warm.flow.core.entity.Definition;
import org.dromara.warm.flow.core.entity.Instance;
import org.dromara.warm.flow.core.entity.Node;
import org.dromara.warm.flow.core.entity.Skip;
import org.dromara.warm.flow.core.enums.ActivityStatus;
import org.dromara.warm.flow.core.enums.NodeType;
import org.dromara.warm.flow.core.enums.PublishStatus;
import org.dromara.warm.flow.core.enums.SkipType;
import org.dromara.warm.flow.core.exception.FlowException;
import org.dromara.warm.flow.core.orm.dao.FlowDefinitionDao;
import org.dromara.warm.flow.core.orm.service.impl.WarmServiceImpl;
import org.dromara.warm.flow.core.service.DefService;
import org.dromara.warm.flow.core.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 流程定义Service业务层处理
 *
 * @author warm
 * @since 2023-03-29
 */
public class DefServiceImpl extends WarmServiceImpl<FlowDefinitionDao<Definition>, Definition> implements DefService {

    private static final Logger log = LoggerFactory.getLogger(DefServiceImpl.class);

    @Override
    public DefService setDao(FlowDefinitionDao<Definition> warmDao) {
        this.warmDao = warmDao;
        return this;
    }

    @Override
    public Definition importIs(InputStream is) {
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append(System.lineSeparator());
            }
        } catch (IOException e) {
            throw new FlowException(ExceptionCons.READ_IS_ERROR);
        }
        return importJson(stringBuilder.toString());
    }

    @Override
    public Definition importJson(String defJson) {
        return importDef(FlowEngine.jsonConvert.strToBean(defJson, DefJson.class));
    }

    @Override
    public Definition importDef(DefJson defJson) {
        Definition definition = DefJson.copyDef(defJson);
        FlowCombine flowCombine = FlowConfigUtil.structureFlow(definition);
        return insertFlow(flowCombine.getDefinition(), flowCombine.getAllNodes(), flowCombine.getAllSkips());
    }

    @Override
    public Definition insertFlow(Definition definition, List<Node> nodeList, List<Skip> skipList) {
        definition.setVersion(getNewVersion(definition));
        for (Node node : nodeList) {
            node.setVersion(definition.getVersion());
        }
        FlowEngine.defService().save(definition);
        FlowEngine.nodeService().saveBatch(nodeList);
        FlowEngine.skipService().saveBatch(skipList);
        return definition;
    }

    @Override
    public boolean saveAndInitNode(Definition definition) {
        definition.setVersion(getNewVersion(definition));
        FlowEngine.dataFillHandler().idFill(definition);
        List<Node> nodeList = new ArrayList<>();
        List<Skip> skipList = new ArrayList<>();

        Node startNode = FlowEngine.newNode()
                .setDefinitionId(definition.getId())
                .setNodeCode(NodeType.START.getValue())
                .setNodeName("开始")
                .setNodeType(NodeType.START.getKey())
                .setCoordinate("260,200|260,200")
                .setNodeRatio(BigDecimal.ZERO)
                .setVersion(definition.getVersion());
        nodeList.add(startNode);

        Node betweenOneNode = FlowEngine.newNode()
                .setDefinitionId(definition.getId())
                .setNodeCode("submit")
                .setNodeName("中间节点-或签1")
                .setNodeType(NodeType.BETWEEN.getKey())
                .setCoordinate("420,200|420,200")
                .setNodeRatio(BigDecimal.ZERO)
                .setVersion(definition.getVersion());
        nodeList.add(betweenOneNode);

        Node betweenTwoNode = FlowEngine.newNode()
                .setDefinitionId(definition.getId())
                .setNodeCode("approval")
                .setNodeName("中间节点-或签2")
                .setNodeType(NodeType.BETWEEN.getKey())
                .setCoordinate("600,200|600,200")
                .setNodeRatio(BigDecimal.ZERO)
                .setVersion(definition.getVersion());
        nodeList.add(betweenTwoNode);

        Node endNode = FlowEngine.newNode()
                .setDefinitionId(definition.getId())
                .setNodeCode(NodeType.END.getValue())
                .setNodeName("结束")
                .setNodeType(NodeType.END.getKey())
                .setCoordinate("760,200|760,200")
                .setNodeRatio(BigDecimal.ZERO)
                .setVersion(definition.getVersion());
        nodeList.add(endNode);

        skipList.add(FlowEngine.newSkip()
                .setDefinitionId(definition.getId())
                .setNowNodeCode(startNode.getNodeCode())
                .setNowNodeType(startNode.getNodeType())
                .setNextNodeCode(betweenOneNode.getNodeCode())
                .setNextNodeType(betweenOneNode.getNodeType())
                .setSkipType(SkipType.PASS.getKey())
                .setCoordinate("280,200;370,200"));

        skipList.add(FlowEngine.newSkip()
                .setDefinitionId(definition.getId())
                .setNowNodeCode(betweenOneNode.getNodeCode())
                .setNowNodeType(betweenOneNode.getNodeType())
                .setNextNodeCode(betweenTwoNode.getNodeCode())
                .setNextNodeType(betweenTwoNode.getNodeType())
                .setSkipType(SkipType.PASS.getKey())
                .setCoordinate("470,200;550,200"));

        skipList.add(FlowEngine.newSkip()
                .setDefinitionId(definition.getId())
                .setNowNodeCode(betweenTwoNode.getNodeCode())
                .setNowNodeType(betweenTwoNode.getNodeType())
                .setNextNodeCode(endNode.getNodeCode())
                .setNextNodeType(endNode.getNodeType())
                .setSkipType(SkipType.PASS.getKey())
                .setCoordinate("650,200;740,200"));
        FlowEngine.nodeService().saveBatch(nodeList);
        FlowEngine.skipService().saveBatch(skipList);
        return save(definition);
    }

    @Override
    public boolean checkAndSave(Definition definition) {
        return save(definition.setVersion(getNewVersion(definition)));
    }

    @Override
    public void saveDef(DefJson defJson) {
        if (ObjectUtil.isNull(defJson)) {
            return;
        }
        FlowCombine flowCombine = DefJson.copyCombine(defJson);

        // 校验流程定义合法性
        checkFlowLegal(flowCombine);
        // 保存流程节点和跳转
        saveNodeAndSkip(flowCombine.getDefinition().getId(), flowCombine);
    }

    @Override
    public String exportJson(Long id) {
        return FlowEngine.jsonConvert.objToStr(DefJson.copyDef(getAllDataDefinition(id)));
    }

    @Override
    public Definition getAllDataDefinition(Long id) {
        Definition definition = getDao().selectById(id);
        List<Node> nodeList = FlowEngine.nodeService().getByDefId(id);
        definition.setNodeList(nodeList);
        List<Skip> skips = FlowEngine.skipService().getByDefId(id);
        Map<String, List<Skip>> flowSkipMap = skips.stream()
                .collect(Collectors.groupingBy(Skip::getNowNodeCode));
        nodeList.forEach(flowNode -> flowNode.setSkipList(flowSkipMap.get(flowNode.getNodeCode())));
        return definition;
    }

    @Override
    public FlowCombine getFlowCombine(Long id) {
        return getFlowCombine(getDao().selectById(id));
    }

    @Override
    public FlowCombine getFlowCombineNoDef(Long id) {
        FlowCombine flowCombine = new FlowCombine();
        flowCombine.setAllNodes(FlowEngine.nodeService().getByDefId(id));
        flowCombine.setAllSkips(FlowEngine.skipService().getByDefId(id));
        return flowCombine;
    }

    @Override
    public FlowCombine getFlowCombine(Definition definition) {
        FlowCombine flowCombine = getFlowCombineNoDef(definition.getId());
        flowCombine.setDefinition(definition);
        return flowCombine;
    }

    @Override
    public DefJson queryDesign(Long id) {
        return DefJson.copyDef(getAllDataDefinition(id));
    }

    @Override
    public List<Definition> queryByCodeList(List<String> flowCodeList) {
        return getDao().queryByCodeList(flowCodeList);
    }

    @Override
    public void updatePublishStatus(List<Long> ids, Integer publishStatus) {
        getDao().updatePublishStatus(ids, publishStatus);
    }

    /**
     * 删除流程定义
     *
     * @param ids 流程定义id
     */
    @Override
    public boolean removeDef(List<Long> ids) {
        ids.forEach(id -> {
            List<Instance> instances = FlowEngine.insService().getByDefId(id);
            AssertUtil.isNotEmpty(instances, ExceptionCons.EXIST_START_TASK);
        });
        FlowEngine.nodeService().deleteNodeByDefIds(ids);
        FlowEngine.skipService().deleteSkipByDefIds(ids);
        return removeByIds(ids);
    }

    @Override
    public boolean publish(Long id) {
        Definition definition = getById(id);
        List<Definition> definitions = getByFlowCode(definition.getFlowCode());
        // 已发布流程定义，改为已失效或者未发布状态
        List<Long> otherDefIds = definitions.stream()
                .filter(item -> !Objects.equals(definition.getId(), item.getId())
                        && PublishStatus.PUBLISHED.getKey().equals(item.getIsPublish()))
                .map(Definition::getId)
                .collect(Collectors.toList());
        if (CollUtil.isNotEmpty(otherDefIds)) {
            List<Instance> instanceList = FlowEngine.insService().listByDefIds(otherDefIds);
            if (CollUtil.isNotEmpty(instanceList)) {
                // 已发布已使用过的流程定义
                Set<Long> useDefIds = StreamUtils.toSet(instanceList, Instance::getDefinitionId);
                if (CollUtil.isNotEmpty(useDefIds)) {
                    // 已发布已使用过的流程定义，改为已失效
                    updatePublishStatus(new ArrayList<>(useDefIds), PublishStatus.EXPIRED.getKey());

                    // 已发布未使用的流定义
                    otherDefIds.removeIf(useDefIds::contains);
                }

            }
            if (CollUtil.isNotEmpty(otherDefIds)) {
                // 已发布未使用过的流程定义，改为未发布
                updatePublishStatus(otherDefIds, PublishStatus.UNPUBLISHED.getKey());
            }
        }

        Definition flowDefinition = FlowEngine.newDef();
        flowDefinition.setId(id);
        flowDefinition.setIsPublish(PublishStatus.PUBLISHED.getKey());
        return updateById(flowDefinition);
    }

    @Override
    public boolean unPublish(Long id) {
        List<Instance> instances = FlowEngine.insService().getByDefId(id);
        AssertUtil.isNotEmpty(instances, ExceptionCons.EXIST_START_TASK);
        Definition definition = FlowEngine.newDef().setId(id);
        definition.setIsPublish(PublishStatus.UNPUBLISHED.getKey());
        return updateById(definition);
    }

    @Override
    public boolean copyDef(Long id) {
        Definition definition = getById(id).copy();
        definition.setVersion(getNewVersion(definition));
        AssertUtil.isNull(definition, ExceptionCons.NOT_FOUNT_DEF);

        List<Node> nodeList = FlowEngine.nodeService().getByDefId(id).stream().map(Node::copy).collect(Collectors.toList());
        List<Skip> skipList = FlowEngine.skipService().getByDefId(id).stream().map(Skip::copy).collect(Collectors.toList());
        FlowEngine.dataFillHandler().idFill(definition.setId(null));
        definition.setIsPublish(PublishStatus.UNPUBLISHED.getKey())
                .setCreateTime(null)
                .setUpdateTime(null);

        nodeList.forEach(node -> node.setId(null)
                .setDefinitionId(definition.getId())
                .setVersion(definition.getVersion())
                .setCreateTime(null)
                .setUpdateTime(null));
        FlowEngine.nodeService().saveBatch(nodeList);

        skipList.forEach(skip -> skip.setId(null)
                .setDefinitionId(definition.getId())
                .setCreateTime(null)
                .setUpdateTime(null));
        FlowEngine.skipService().saveBatch(skipList);
        return save(definition);
    }

    @Override
    public boolean active(Long id) {
        Definition definition = getById(id);
        AssertUtil.isTrue(definition.getActivityStatus().equals(ActivityStatus.ACTIVITY.getKey()), ExceptionCons.DEFINITION_ALREADY_ACTIVITY);
        definition.setActivityStatus(ActivityStatus.ACTIVITY.getKey());
        return updateById(definition);
    }

    @Override
    public boolean unActive(Long id) {
        Definition definition = getById(id);
        AssertUtil.isTrue(definition.getActivityStatus().equals(ActivityStatus.SUSPENDED.getKey()), ExceptionCons.DEFINITION_ALREADY_SUSPENDED);
        definition.setActivityStatus(ActivityStatus.SUSPENDED.getKey());
        return updateById(definition);
    }

    @Override
    public List<Definition> getByFlowCode(String flowCode) {
        return list(FlowEngine.newDef().setFlowCode(flowCode));
    }

    @Override
    public Definition getPublishByFlowCode(String flowCode) {
        return FlowEngine.defService().getOne(FlowEngine.newDef()
                .setFlowCode(flowCode).setIsPublish(PublishStatus.PUBLISHED.getKey()));
    }

    private String getNewVersion(Definition definition) {
        List<String> flowCodeList = Collections.singletonList(definition.getFlowCode());
        List<Definition> definitions = queryByCodeList(flowCodeList);
        int highestVersion = 0;
        String latestNonPositiveVersion = null;
        long latestTimestamp = Long.MIN_VALUE;

        for (Definition otherDef : definitions) {
            if (definition.getFlowCode().equals(otherDef.getFlowCode())) {
                try {
                    int version = Integer.parseInt(otherDef.getVersion());
                    if (version > highestVersion) {
                        highestVersion = version;
                    }
                } catch (NumberFormatException e) {
                    long timestamp = otherDef.getCreateTime().getTime();
                    if (timestamp > latestTimestamp) {
                        latestTimestamp = timestamp;
                        latestNonPositiveVersion = otherDef.getVersion();
                    }
                }
            }
        }

        String version = "1";
        if (highestVersion > 0) {
            version = String.valueOf(highestVersion + 1);
        } else if (latestNonPositiveVersion != null) {
            version = latestNonPositiveVersion + "_1";
        }

        return version;
    }

    private void checkFlowLegal(FlowCombine flowCombine) {
        Definition definition = flowCombine.getDefinition();
        String flowName = definition.getFlowName();
        AssertUtil.isEmpty(definition.getFlowCode(), "【" + flowName + "】流程flowCode为空!");
        // 节点校验
        List<Node> allNodes = flowCombine.getAllNodes();
        List<Skip> allSkips = flowCombine.getAllSkips();
        Map<String, List<Skip>> skipMap = StreamUtils.groupByKey(allSkips, Skip::getNowNodeCode);
        allNodes.forEach(node -> {
            node.setSkipList(skipMap.get(node.getNodeCode()));
            skipMap.remove(node.getNodeCode());
        });
        AssertUtil.isNotEmpty(skipMap, "[" + flowName + "]" + ExceptionCons.FLOW_HAVE_USELESS_SKIP);
        // 每一个流程的开始节点个数
        Set<String> nodeCodeSet = new HashSet<>();
        // 便利一个流程中的各个节点
        int startNum = 0;
        for (Node node : allNodes) {
            FlowConfigUtil.initNodeAndCondition(node, definition.getId(), definition.getVersion());
            startNum = FlowConfigUtil.checkStartAndSame(node, startNum, flowName, nodeCodeSet);
        }
        AssertUtil.isTrue(startNum == 0, "[" + flowName + "]" + ExceptionCons.LOST_START_NODE);
        // 校验跳转节点的合法性
        FlowConfigUtil.checkSkipNode(allSkips);
        // 校验所有目标节点是否都存在
        FlowConfigUtil.validaIsExistDestNode(allSkips, nodeCodeSet);
    }

    private void saveNodeAndSkip(Long defId, FlowCombine flowCombine) {
        // 所有的流程节点
        List<Node> allNodes = flowCombine.getAllNodes();
        // 所有的流程连线
        List<Skip> allSkips = flowCombine.getAllSkips();
        // 删除所有节点和连线
        FlowEngine.nodeService().remove(FlowEngine.newNode().setDefinitionId(defId));
        FlowEngine.skipService().remove(FlowEngine.newSkip().setDefinitionId(defId));

        allNodes.forEach(node -> node.setId(null)
                .setDefinitionId(defId)
                .setCreateTime(null)
                .setUpdateTime(null));

        allSkips.forEach(skip -> skip.setId(null)
                .setDefinitionId(defId)
                .setCreateTime(null)
                .setUpdateTime(null));

        // 保存节点，流程连线，权利人
        FlowEngine.nodeService().saveBatch(allNodes);
        FlowEngine.skipService().saveBatch(allSkips);
    }

}
