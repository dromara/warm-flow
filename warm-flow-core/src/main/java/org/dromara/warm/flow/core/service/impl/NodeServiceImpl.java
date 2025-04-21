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
import org.dromara.warm.flow.core.dto.FlowCombine;
import org.dromara.warm.flow.core.dto.PathWayData;
import org.dromara.warm.flow.core.entity.Definition;
import org.dromara.warm.flow.core.entity.Node;
import org.dromara.warm.flow.core.entity.Skip;
import org.dromara.warm.flow.core.enums.NodeType;
import org.dromara.warm.flow.core.enums.PublishStatus;
import org.dromara.warm.flow.core.enums.SkipType;
import org.dromara.warm.flow.core.orm.dao.FlowNodeDao;
import org.dromara.warm.flow.core.orm.service.impl.WarmServiceImpl;
import org.dromara.warm.flow.core.service.NodeService;
import org.dromara.warm.flow.core.utils.*;

import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 流程节点Service业务层处理
 *
 * @author warm
 * @since 2023-03-29
 */
public class NodeServiceImpl extends WarmServiceImpl<FlowNodeDao<Node>, Node> implements NodeService {

    @Override
    public NodeService setDao(FlowNodeDao<Node> warmDao) {
        this.warmDao = warmDao;
        return this;
    }

    @Override
    public List<Node> getPublishByFlowCode(String flowCode) {
        Definition definition = FlowEngine.defService().getOne(FlowEngine.newDef()
                .setFlowCode(flowCode).setIsPublish(PublishStatus.PUBLISHED.getKey()));
        if (ObjectUtil.isNotNull(definition)) {
            return list(FlowEngine.newNode().setDefinitionId(definition.getId()));
        }
        return Collections.emptyList();
    }

    @Override
    public List<Node> getByNodeCodes(List<String> nodeCodes, Long definitionId) {
        return getDao().getByNodeCodes(nodeCodes, definitionId);
    }

    @Override
    public List<Node> previousNodeList(Long nodeId) {
        Node nowNode = getById(nodeId);
        return previousNodeList(nowNode.getDefinitionId(), nowNode.getNodeCode());
    }

    @Override
    public List<Node> previousNodeList(Long definitionId, String nowNodeCode) {
        return prefixOrSuffixNodes(definitionId, nowNodeCode, "previous");
    }

    @Override
    public List<Node> suffixNodeList(Long nodeId) {
        Node nowNode = getById(nodeId);
        return suffixNodeList(nowNode.getDefinitionId(), nowNode.getNodeCode());
    }

    @Override
    public List<Node> suffixNodeList(Long definitionId, String nowNodeCode) {
        return prefixOrSuffixNodes(definitionId, nowNodeCode, "suffix");
    }

    @Override
    public List<Node> suffixNodeList(String nowNodeCode, FlowCombine flowCombine) {
        return prefixOrSuffixNodes(nowNodeCode, "suffix", flowCombine);
    }

    @Override
    public List<Node> getByDefId(Long definitionId) {
        return list(FlowEngine.newNode().setDefinitionId(definitionId));
    }

    @Override
    public Node getByDefIdAndNodeCode(Long definitionId, String nodeCode) {
        return getOne(FlowEngine.newNode().setDefinitionId(definitionId).setNodeCode(nodeCode));
    }

    @Override
    public Node getStartNode(Long definitionId) {
        return getOne(FlowEngine.newNode().setDefinitionId(definitionId).setNodeType(NodeType.START.getKey()));
    }

    @Override
    public List<Node> getBetweenNode(Long definitionId) {
        return list(FlowEngine.newNode().setDefinitionId(definitionId).setNodeType(NodeType.END.getKey()));
    }

    @Override
    public Node getEndNode(Long definitionId) {
        return getOne(FlowEngine.newNode().setDefinitionId(definitionId).setNodeType(NodeType.END.getKey()));
    }

    public List<Node> prefixOrSuffixNodes(Long definitionId, String nowNodeCode, String type) {
        FlowCombine flowCombine = new FlowCombine();
        flowCombine.setAllNodes(FlowEngine.nodeService().getByDefId(definitionId));
        flowCombine.setAllSkips(FlowEngine.skipService().getByDefId(definitionId));
        return prefixOrSuffixNodes(nowNodeCode, type, flowCombine);
    }

    public List<Node> prefixOrSuffixNodes(String nowNodeCode, String type, FlowCombine flowCombine) {
        Map<String, Node> nodeMap = StreamUtils.toMap(flowCombine.getAllNodes(), Node::getNodeCode, node -> node);
        Map<String, List<Skip>> skipMap = flowCombine.getAllSkips().stream().filter(skip -> SkipType.isPass(skip.getSkipType()))
                .collect(Collectors.groupingBy("previous".equals(type)? Skip::getNextNodeCode: Skip::getNowNodeCode
                        , LinkedHashMap::new, Collectors.toList()));

        List<Node> prefixOrSuffixNodes = new ArrayList<>();
        List<String> prefixOrSuffixCode = prefixOrSuffixCodes(skipMap, nowNodeCode
                , "previous".equals(type)? Skip::getNowNodeCode: Skip::getNextNodeCode);
        for (String nodeCode : prefixOrSuffixCode) {
            Node node = nodeMap.get(nodeCode);
            if (!NodeType.isGateWay(node.getNodeType())) {
                prefixOrSuffixNodes.add(node);
            }
        }
        Collections.reverse(prefixOrSuffixNodes);
        Set<String> sameCode = new HashSet<>();
        prefixOrSuffixNodes.removeIf(node -> {
            if (sameCode.contains(node.getNodeCode())) {
                return true;
            }
            sameCode.add(node.getNodeCode());
            return false;
        });
        Collections.reverse(prefixOrSuffixNodes);
        return prefixOrSuffixNodes;
    }

    @Override
    public List<Node> getNextNodeList(Long definitionId, String nowNodeCode, String anyNodeCode, String skipType,
                                      Map<String, Object> variable) {
        AssertUtil.isEmpty(nowNodeCode, ExceptionCons.LOST_NODE_CODE);
        // 查询当前节点
        FlowCombine flowCombine = FlowEngine.defService().getFlowCombineNoDef(definitionId);
        Node nowNode = StreamUtils.filterOne(flowCombine.getAllNodes(), t -> t.getNodeCode().equals(nowNodeCode));
        // 如果是网关节点，则根据条件判断
        return getNextByCheckGateway(variable, getNextNode(nowNode, anyNodeCode, skipType, null, flowCombine),
                null, flowCombine);
    }

    @Override
    public Node getNextNode(Long definitionId, String nowNodeCode, String anyNodeCode, String skipType) {
        // 查询当前节点
        FlowCombine flowCombine = FlowEngine.defService().getFlowCombineNoDef(definitionId);
        Node nowNode = StreamUtils.filterOne(flowCombine.getAllNodes(), t -> t.getNodeCode().equals(nowNodeCode));
        return getNextNode(nowNode, anyNodeCode, skipType, null, flowCombine);
    }

    @Override
    public List<Node> getNextNodeList(Node nowNode, String anyNodeCode, String skipType, Map<String, Object> variable
            , PathWayData pathWayData, FlowCombine flowCombine) {
        // 如果是网关节点，则根据条件判断
        return getNextByCheckGateway(variable, getNextNode(nowNode, anyNodeCode, skipType
                , pathWayData, flowCombine), pathWayData, flowCombine);
    }

    @Override
    public Node getNextNode(Node nowNode, String anyNodeCode, String skipType, PathWayData pathWayData, FlowCombine flowCombine) {
        // 查询当前节点
        AssertUtil.isNull(nowNode, ExceptionCons.LOST_NODE_CODE);
        AssertUtil.isNull(nowNode.getDefinitionId(), ExceptionCons.NOT_DEFINITION_ID);
        AssertUtil.isEmpty(skipType, ExceptionCons.NULL_CONDITION_VALUE);

        if (pathWayData != null) {
            pathWayData.getPathWayNodes().add(nowNode);
        }
        Node nextNode = null;
        // 如果指定了跳转节点，直接获取节点
        if (StringUtils.isNotEmpty(anyNodeCode)) {
            nextNode = StreamUtils.filterOne(flowCombine.getAllNodes(), node -> anyNodeCode.equals(node.getNodeCode()));
        }

        // 如果配置了任意跳转节点，直接获取节点
        if (StringUtils.isNotEmpty(nowNode.getAnyNodeSkip()) && SkipType.isReject(skipType)) {
            nextNode = StreamUtils.filterOne(flowCombine.getAllNodes(), node -> nowNode.getAnyNodeSkip().equals(node.getNodeCode()));
        }

        if (ObjectUtil.isNotNull(nextNode)) {
            AssertUtil.isTrue(NodeType.isGateWay(nextNode.getNodeType()), ExceptionCons.TAR_NOT_GATEWAY);
            return nextNode;
        }

        // 获取跳转关系
        List<Skip> skips = StreamUtils.filter(flowCombine.getAllSkips(), skip -> nowNode.getNodeCode().equals(skip.getNowNodeCode()));
        AssertUtil.isNull(skips, ExceptionCons.NULL_SKIP_TYPE);
        Skip nextSkip = getSkipByCheck(skips, skipType);
        AssertUtil.isNull(nextSkip, ExceptionCons.NULL_SKIP_TYPE);

        // 根据跳转查询出跳转到的那个节点
        nextNode = StreamUtils.filterOne(flowCombine.getAllNodes(), node -> nextSkip.getNextNodeCode().equals(node.getNodeCode()));
        AssertUtil.isNull(nextNode, ExceptionCons.NULL_NODE_CODE);
        AssertUtil.isTrue(NodeType.isStart(nextNode.getNodeType()), ExceptionCons.FIRST_FORBID_BACK);
        if (pathWayData != null) {
            pathWayData.getPathWayNodes().add(nextNode);
            pathWayData.getPathWaySkips().add(nextSkip);
        }
        return nextNode;
    }

    @Override
    public List<Node> getNextByCheckGateway(Map<String, Object> variable, Node nextNode, PathWayData pathWayData
            , FlowCombine flowCombine) {
        // 网关节点处理
        if (NodeType.isGateWay(nextNode.getNodeType())) {
            List<Skip> skipsGateway = StreamUtils.filter(flowCombine.getAllSkips()
                    , skip -> nextNode.getNodeCode().equals(skip.getNowNodeCode()));
            if (CollUtil.isEmpty(skipsGateway)) {
                return null;
            }
            if (!NodeType.isStart(nextNode.getNodeType()) && NodeType.isGateWaySerial(nextNode.getNodeType())) {
                //  如果满足跳转条件，则取任意一条，否则取跳转条件为空的任意一条
                Skip skipOne = null;
                for (Skip skip : skipsGateway) {
                    if (StringUtils.isNotEmpty(skip.getSkipCondition())) {
                        if (ExpressionUtil.evalCondition(skip.getSkipCondition(), variable)) {
                            skipOne = skip;
                            break;
                        }
                    } else {
                        skipOne = skip;
                    }
                }
                skipsGateway = skipOne == null ? null : CollUtil.toList(skipOne);
            }
            AssertUtil.isEmpty(skipsGateway, ExceptionCons.NULL_CONDITION_VALUE_NODE);
            List<String> nextNodeCodes = StreamUtils.toList(skipsGateway, Skip::getNextNodeCode);
            List<Node> nextNodes = StreamUtils.filter(flowCombine.getAllNodes()
                    , node -> nextNodeCodes.contains(node.getNodeCode()));
            AssertUtil.isEmpty(nextNodes, ExceptionCons.NOT_NODE_DATA);
            if (pathWayData != null) {
                pathWayData.getPathWayNodes().addAll(nextNodes);
                pathWayData.getPathWaySkips().addAll(skipsGateway);
            }
            List<Node> newNextNodes = new ArrayList<>();
            for (Node node : nextNodes) {
                List<Node> nodeList = getNextByCheckGateway(variable, node, pathWayData, flowCombine);
                newNextNodes.addAll(nodeList);
            }
            return newNextNodes;
        }
        // 非网关节点直接返回
        if (pathWayData != null) {
            pathWayData.getPathWayNodes().remove(nextNode);
        }
        AssertUtil.isTrue(NodeType.isStart(nextNode.getNodeType()), ExceptionCons.START_NODE_NOT_ALLOW_JUMP);
        return CollUtil.toList(nextNode);
    }


    @Override
    public int deleteNodeByDefIds(Collection<? extends Serializable> defIds) {
        return getDao().deleteNodeByDefIds(defIds);
    }

    private List<String> prefixOrSuffixCodes(Map<String, List<Skip>> skipMap, String nodeCode
            , Function<Skip, String> supplier) {
        List<String> prefixOrSuffixCode = new ArrayList<>();
        List<Skip> skipList = skipMap.get(nodeCode);
        if (CollUtil.isNotEmpty(skipList)) {
            for (Skip skip : skipList) {
                if (SkipType.isPass(skip.getSkipType())) {
                    prefixOrSuffixCode.add(supplier.apply(skip));
                    prefixOrSuffixCode.addAll(prefixOrSuffixCodes(skipMap, supplier.apply(skip), supplier));
                }
            }
        }
        return prefixOrSuffixCode;
    }

    /**
     * 通过校验跳转类型获取跳转集合
     *
     * @param skips    跳转集合
     * @param skipType 跳转类型
     * @return List<Skip>
     * @author xiarg
     * @since 2024/8/21 11:32
     */
    private Skip getSkipByCheck(List<Skip> skips, String skipType) {
        if (CollUtil.isEmpty(skips)) {
            return null;
        }
        skips = skips.stream().filter(t -> {
            if (StringUtils.isNotEmpty(t.getSkipType())) {
                return skipType.equals(t.getSkipType());
            }
            return true;
        }).collect(Collectors.toList());
        AssertUtil.isEmpty(skips, ExceptionCons.NULL_SKIP_TYPE);
        return skips.get(0);
    }

}
