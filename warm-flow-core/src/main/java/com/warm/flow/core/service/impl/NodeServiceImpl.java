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
package com.warm.flow.core.service.impl;

import com.warm.flow.core.FlowFactory;
import com.warm.flow.core.constant.ExceptionCons;
import com.warm.flow.core.constant.FlowCons;
import com.warm.flow.core.dao.FlowNodeDao;
import com.warm.flow.core.dto.FlowParams;
import com.warm.flow.core.entity.Definition;
import com.warm.flow.core.entity.Node;
import com.warm.flow.core.entity.Skip;
import com.warm.flow.core.enums.NodeType;
import com.warm.flow.core.enums.PublishStatus;
import com.warm.flow.core.enums.SkipType;
import com.warm.flow.core.orm.service.impl.WarmServiceImpl;
import com.warm.flow.core.service.NodeService;
import com.warm.flow.core.utils.AssertUtil;
import com.warm.flow.core.utils.CollUtil;
import com.warm.flow.core.utils.ObjectUtil;
import com.warm.flow.core.utils.StringUtils;

import java.io.Serializable;
import java.util.*;
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
    public Node getByNodeCode(String nodeCode, Long definitionId) {
        List<Node> nodeCodes = getDao().getByNodeCodes(Collections.singletonList(nodeCode), definitionId);
        return CollUtil.getOne(nodeCodes);
    }

    @Override
    public List<Node> getNextNodeByNodeCode(Long definitionId, String nowNodeCode, String skipType
            , Map<String, Object> variable, String nextNodeCode) {
        AssertUtil.isNull(definitionId, ExceptionCons.NOT_DEFINITION_ID);
        AssertUtil.isBlank(nowNodeCode, ExceptionCons.LOST_NODE_CODE);
        //不传,默认取通过的条件
        if (StringUtils.isEmpty(skipType)) {
            skipType = SkipType.PASS.getKey();
        }
        //查询当前节点
        Node nowNode = FlowFactory.nodeService().getByNodeCode(nowNodeCode, definitionId);
        AssertUtil.isNull(nowNode, ExceptionCons.LOST_DEST_NODE);
        //是否可以跳转任意节点,如是,返回nextNodeCode的节点
        if (FlowCons.SKIP_ANY_Y.equals(nowNode.getSkipAnyNode())) {
            AssertUtil.isBlank(nextNodeCode, ExceptionCons.LOST_NEXT_NODE_CODE);

            Node nextNode = FlowFactory.nodeService().getByNodeCode(nextNodeCode, definitionId);
            ArrayList<Node> arrayList = new ArrayList<>();
            arrayList.add(nextNode);
            return arrayList;
        }
        //获取跳转关系
        List<Skip> skips = FlowFactory.skipService().list(FlowFactory.newSkip().setDefinitionId(definitionId)
                .setNowNodeCode(nowNodeCode));
        AssertUtil.isNull(skips, ExceptionCons.NULL_CONDITIONVALUE_NODE);

        return getNextSkips(nowNode, skips, skipType, variable);
    }

    @Override
    public int deleteNodeByDefIds(Collection<? extends Serializable> defIds) {
        return getDao().deleteNodeByDefIds(defIds);
    }

    /**
     * 获取下一节点的跳转关系
     *
     * @param node
     * @param skips
     */
    private List<Node> getNextSkips(Node node, List<Skip> skips, String skipType, Map<String, Object> variable) {

        //根据跳转类型获取关系
        if (!NodeType.isStart(node.getNodeType())) {
            skips = skips.stream().filter(t -> {
                if (StringUtils.isNotEmpty(t.getSkipType())) {
                    return skipType.equals(t.getSkipType());
                }
                return true;
            }).collect(Collectors.toList());
        }
        AssertUtil.isTrue(CollUtil.isEmpty(skips), ExceptionCons.NULL_CONDITIONVALUE_NODE);
        Skip skip = skips.get(0);

        Node nextNode = FlowFactory.nodeService().getByNodeCode(skip.getNextNodeCode(), skip.getDefinitionId());
        // 如果是网关节点，则重新获取后续节点
        FlowParams flowParams = FlowParams.build().variable(variable);
        return FlowFactory.taskService().getNextByCheckGateWay(flowParams, nextNode);
    }

}
