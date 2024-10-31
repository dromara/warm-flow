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

import org.dromara.warm.flow.core.FlowFactory;
import org.dromara.warm.flow.core.constant.ExceptionCons;
import org.dromara.warm.flow.core.dao.FlowNodeDao;
import org.dromara.warm.flow.core.entity.Definition;
import org.dromara.warm.flow.core.entity.Node;
import org.dromara.warm.flow.core.entity.Skip;
import org.dromara.warm.flow.core.enums.NodeType;
import org.dromara.warm.flow.core.enums.PublishStatus;
import org.dromara.warm.flow.core.orm.service.impl.WarmServiceImpl;
import org.dromara.warm.flow.core.service.NodeService;
import org.dromara.warm.flow.core.utils.*;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 流程节点Service业务层处理
 *
 * @author warm
 * @date 2023-03-29
 */
public class NodeServiceImpl extends WarmServiceImpl<FlowNodeDao<Node>, Node> implements NodeService {

    @Override
    public NodeService setDao(FlowNodeDao<Node> warmDao) {
        this.warmDao = warmDao;
        return this;
    }

    @Override
    public List<Node> getByFlowCode(String flowCode) {
        Definition definition = FlowFactory.defService().getOne(FlowFactory.newDef()
                .setFlowCode(flowCode).setIsPublish(PublishStatus.PUBLISHED.getKey()));
        if (ObjectUtil.isNotNull(definition)) {
            return list(FlowFactory.newNode().setDefinitionId(definition.getId()));
        }
        return Collections.emptyList();
    }

    @Override
    public List<Node> getByNodeCodes(List<String> nodeCodes, Long definitionId) {
        return getDao().getByNodeCodes(nodeCodes, definitionId);
    }

    @Override
    public List<Node> getNextNodeList(Long definitionId, String nowNodeCode, String anyNodeCode, String skipType,
                                      Map<String, Object> variable) {
        // 如果是网关节点，则根据条件判断
        return getNextByCheckGateway(variable, getNextNode(definitionId, nowNodeCode, anyNodeCode, skipType));
    }

    @Override
    public Node getNextNode(Long definitionId, String nowNodeCode, String anyNodeCode, String skipType) {
        AssertUtil.isNull(definitionId, ExceptionCons.NOT_DEFINITION_ID);
        AssertUtil.isEmpty(nowNodeCode, ExceptionCons.LOST_NODE_CODE);
        AssertUtil.isEmpty(skipType, ExceptionCons.NULL_CONDITIONVALUE);

        // 如果指定了跳转节点，直接获取节点
        if (StringUtils.isNotEmpty(anyNodeCode)) {
            return getOne(FlowFactory.newNode().setNodeCode(anyNodeCode).setDefinitionId(definitionId));
        }
        // 查询当前节点
        Node nowNode = getOne(FlowFactory.newNode().setNodeCode(nowNodeCode).setDefinitionId(definitionId));
        AssertUtil.isNull(nowNode, ExceptionCons.LOST_CUR_NODE);
        // 获取跳转关系
        List<Skip> skips = FlowFactory.skipService().list(FlowFactory.newSkip().setDefinitionId(definitionId)
                .setNowNodeCode(nowNodeCode));
        AssertUtil.isNull(skips, ExceptionCons.NULL_SKIP_TYPE);

        Skip nextSkip = getSkipByCheck(nowNode, skips, skipType);
        AssertUtil.isNull(nextSkip, ExceptionCons.NULL_SKIP_TYPE);

        // 根据跳转查询出跳转到的那个节点
        Node nextNode = getOne(FlowFactory.newNode().setNodeCode(nextSkip.getNextNodeCode()).setDefinitionId(definitionId));
        AssertUtil.isNull(nextNode, ExceptionCons.NULL_NODE_CODE);
        AssertUtil.isTrue(NodeType.isStart(nextNode.getNodeType()), ExceptionCons.FRIST_FORBID_BACK);
        return nextNode;
    }

    /**
     * 通过校验跳转类型获取跳转集合
     *
     * @param nowNode       当前节点信息
     * @param skips         跳转集合
     * @param skipType      跳转类型
     * @return List<Skip>
     * @author xiarg
     * @date 2024/8/21 11:32
     */
    private Skip getSkipByCheck(Node nowNode, List<Skip> skips, String skipType) {
        if (CollUtil.isEmpty(skips)) {
            return null;
        }
        if (!NodeType.isStart(nowNode.getNodeType())) {
            skips = skips.stream().filter(t -> {
                if (StringUtils.isNotEmpty(t.getSkipType())) {
                    return skipType.equals(t.getSkipType());
                }
                return true;
            }).collect(Collectors.toList());
        }
        AssertUtil.isEmpty(skips, ExceptionCons.NULL_SKIP_TYPE);
        return skips.get(0);
    }

    @Override
    public List<Node> getNextByCheckGateway(Map<String, Object> variable, Node nextNode) {
        // 网关节点处理
        if (NodeType.isGateWay(nextNode.getNodeType())) {
            List<Skip> skipsGateway = FlowFactory.skipService().list(FlowFactory.newSkip()
                    .setDefinitionId(nextNode.getDefinitionId()).setNowNodeCode(nextNode.getNodeCode()));
            if (CollUtil.isEmpty(skipsGateway)) {
                return null;
            }
            // 过滤跳转
            if (!NodeType.isStart(nextNode.getNodeType())) {
                skipsGateway = skipsGateway.stream().filter(t -> {
                    if (NodeType.isGateWaySerial(nextNode.getNodeType())) {
                        AssertUtil.isEmpty(variable, ExceptionCons.MUST_CONDITIONVALUE_NODE);
                        if (ObjectUtil.isNotNull(t.getSkipCondition())) {
                            return ExpressionUtil.eval(t.getSkipCondition(), variable);
                        }
                    }
                    // 并行网关返回多个跳转
                    return true;
                }).collect(Collectors.toList());
            }
            AssertUtil.isEmpty(skipsGateway, ExceptionCons.NULL_CONDITIONVALUE_NODE);
            List<String> nextNodeCodes = StreamUtils.toList(skipsGateway, Skip::getNextNodeCode);
            List<Node> nextNodes = FlowFactory.nodeService()
                    .getByNodeCodes(nextNodeCodes, nextNode.getDefinitionId());
            AssertUtil.isEmpty(nextNodes, ExceptionCons.NOT_NODE_DATA);
            return nextNodes;
        }
        // 非网关节点直接返回
        return CollUtil.toList(nextNode);
    }

    @Override
    public int deleteNodeByDefIds(Collection<? extends Serializable> defIds) {
        return getDao().deleteNodeByDefIds(defIds);
    }

}
