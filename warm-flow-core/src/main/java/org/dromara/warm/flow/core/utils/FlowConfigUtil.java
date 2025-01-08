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
package org.dromara.warm.flow.core.utils;

import org.dromara.warm.flow.core.FlowEngine;
import org.dromara.warm.flow.core.constant.ExceptionCons;
import org.dromara.warm.flow.core.dto.FlowCombine;
import org.dromara.warm.flow.core.entity.Definition;
import org.dromara.warm.flow.core.entity.Node;
import org.dromara.warm.flow.core.entity.Skip;
import org.dromara.warm.flow.core.enums.ActivityStatus;
import org.dromara.warm.flow.core.enums.NodeType;
import org.dromara.warm.flow.core.enums.SkipType;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 流程配置帮助类
 *
 * @author zhoukai
 */
public class FlowConfigUtil {
    /**
     * 无参构造
     */
    private FlowConfigUtil() {

    }

    /**
     * 读取配置
     *
     * @param is
     * @throws Exception
     */
    public static FlowCombine readConfig(InputStream is) throws Exception {
        Definition definition = readDocument(is);
        return structureFlow(definition);
    }

    @SuppressWarnings("unchecked")
    public static Definition readDocument(InputStream is) throws Exception {
        AssertUtil.isNull(is, "文件不存在！");
        // 获取流程节点
        Element definitionElement = new SAXReader().read(is).getRootElement();
        AssertUtil.isNull(definitionElement, "流程为空！");

        // 读取流程定义
        Definition definition = FlowEngine.newDef();
        definition.setFlowCode(definitionElement.attributeValue("flowCode"));
        definition.setFlowName(definitionElement.attributeValue("flowName"));
        definition.setVersion(definitionElement.attributeValue("version"));
        definition.setCategory(definitionElement.attributeValue("category"));
        definition.setFormCustom(definitionElement.attributeValue("formCustom"));
        definition.setFormPath(definitionElement.attributeValue("formPath"));
        definition.setExt(definitionElement.attributeValue("ext"));
        definition.setActivityStatus(ActivityStatus.ACTIVITY.getKey());
        definition.setListenerType(definitionElement.attributeValue("listenerType"));
        definition.setListenerPath(definitionElement.attributeValue("listenerPath"));

        List<Element> nodesElement = definitionElement.elements();
        // 遍历一个流程中的各个节点
        List<Node> nodeList = definition.getNodeList();
        for (Element nodeElement : nodesElement) {
            Node node = initNodeAndCondition(nodeElement);
            nodeList.add(node);
        }
        try {
            if (is != null) {
                is.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return definition;
    }

    /**
     * 读取工作节点和跳转条件
     *
     * @param nodeElement
     * @return
     */
    private static Node initNodeAndCondition(Element nodeElement) {
        Node node = FlowEngine.newNode();
        node.setNodeType(NodeType.getKeyByValue(nodeElement.attributeValue("nodeType")));
        node.setNodeCode(nodeElement.attributeValue("nodeCode"));
        node.setNodeName(nodeElement.attributeValue("nodeName"));
        node.setPermissionFlag(nodeElement.attributeValue("permissionFlag"));
        if (StringUtils.isNotEmpty(nodeElement.attributeValue("nodeRatio"))) {
            node.setNodeRatio(new BigDecimal(nodeElement.attributeValue("nodeRatio")));
        } else {
            node.setNodeRatio(new BigDecimal("0"));
        }
        node.setCoordinate(nodeElement.attributeValue("coordinate"));
        node.setSkipAnyNode(nodeElement.attributeValue("skipAnyNode"));
        node.setAnyNodeSkip(nodeElement.attributeValue("anyNodeSkip"));
        node.setListenerType(nodeElement.attributeValue("listenerType"));
        node.setListenerPath(nodeElement.attributeValue("listenerPath"));
        node.setHandlerType(nodeElement.attributeValue("handlerType"));
        node.setHandlerPath(nodeElement.attributeValue("handlerPath"));
        node.setFormCustom(nodeElement.attributeValue("formCustom"));
        node.setFormPath(nodeElement.attributeValue("formPath"));
        FlowEngine.dataFillHandler().idFill(node);

        List<Element> skipsElement = nodeElement.elements();
        List<Skip> skips = node.getSkipList();
        // 遍历节点下的跳转条件
        for (Element skipElement : skipsElement) {
            Skip skip = FlowEngine.newSkip();
            if ("skip".equals(skipElement.getName())) {
                skip.setNowNodeCode(node.getNodeCode());
                skip.setNowNodeType(node.getNodeType());
                skip.setNextNodeCode(skipElement.getText());
                // 条件约束
                skip.setSkipName(skipElement.attributeValue("skipName"));
                skip.setSkipType(skipElement.attributeValue("skipType"));
                skip.setCoordinate(skipElement.attributeValue("coordinate"));
                skip.setSkipCondition(skipElement.attributeValue("skipCondition"));
                skips.add(skip);
            }
        }
        return node;
    }

    public static Document createDocument(Definition definition) {
        // 创建document对象
        Document document = DocumentHelper.createDocument();
        // 创建根节点bookRoot
        Element definitionElement = document.addElement("definition");
        // 向子节点中添加属性
        definitionElement.addAttribute("flowCode", definition.getFlowCode());
        definitionElement.addAttribute("flowName", definition.getFlowName());
        definitionElement.addAttribute("version", definition.getVersion());
        definitionElement.addAttribute("category", definition.getCategory());
        definitionElement.addAttribute("formCustom", definition.getFormCustom());
        definitionElement.addAttribute("formPath", definition.getFormPath());
        definitionElement.addAttribute("listenerType", definition.getListenerType());
        definitionElement.addAttribute("listenerPath", definition.getListenerPath());
        definitionElement.addAttribute("ext", definition.getExt());

        List<Node> nodeList = definition.getNodeList();
        for (Node node : nodeList) {
            // 向节点中添加子节点
            Element nodeElement = definitionElement.addElement("node");
            nodeElement.addAttribute("nodeType", NodeType.getValueByKey(node.getNodeType()));
            nodeElement.addAttribute("nodeCode", node.getNodeCode());
            nodeElement.addAttribute("nodeName", node.getNodeName());
            nodeElement.addAttribute("permissionFlag", node.getPermissionFlag());
            if (Objects.nonNull(node.getNodeRatio())) {
                nodeElement.addAttribute("nodeRatio", node.getNodeRatio().toString());
            }
            nodeElement.addAttribute("coordinate", node.getCoordinate());
            nodeElement.addAttribute("skipAnyNode", node.getSkipAnyNode());
            nodeElement.addAttribute("anyNodeSkip", node.getAnyNodeSkip());
            nodeElement.addAttribute("listenerType", node.getListenerType());
            nodeElement.addAttribute("listenerPath", node.getListenerPath());
            nodeElement.addAttribute("handlerType", node.getHandlerType());
            nodeElement.addAttribute("handlerPath", node.getHandlerPath());
            nodeElement.addAttribute("formCustom", node.getFormCustom());
            nodeElement.addAttribute("formPath", node.getFormPath());

            List<Skip> skipList = node.getSkipList();
            if (CollUtil.isNotEmpty(skipList)) {
                for (Skip skip : skipList) {
                    Element skipElement = nodeElement.addElement("skip");
                    skipElement.addAttribute("coordinate", skip.getCoordinate());
                    if (StringUtils.isNotEmpty(skip.getSkipType())) {
                        AssertUtil.isFalse(StringUtils.isNotEmpty(skip.getNextNodeCode()), "下一个流程节点编码为空");
                        skipElement.addAttribute("skipType", skip.getSkipType());
                    }
                    if (StringUtils.isNotEmpty(skip.getSkipName())) {
                        skipElement.addAttribute("skipName", skip.getSkipName());
                    }
                    if (StringUtils.isNotEmpty(skip.getSkipCondition())) {
                        skipElement.addAttribute("skipCondition", skip.getSkipCondition());
                    }
                    skipElement.addText(skip.getNextNodeCode());
                }
            }
        }
        return document;
    }

    public static FlowCombine structureFlow(Definition definition) {
        // 获取流程
        FlowCombine combine = new FlowCombine();
        // 流程定义
        combine.setDefinition(definition);
        // 所有的流程节点
        List<Node> allNodes = combine.getAllNodes();
        // 所有的流程连线
        List<Skip> allSkips = combine.getAllSkips();

        String flowName = definition.getFlowName();
        AssertUtil.isEmpty(definition.getFlowCode(), "【" + flowName + "】流程flowCode为空!");
        // 发布
        definition.setIsPublish(0);
        definition.setUpdateTime(new Date());
        FlowEngine.dataFillHandler().idFill(definition);

        List<Node> nodeList = definition.getNodeList();
        // 每一个流程的开始节点个数
        int startNum = 0;
        Set<String> nodeCodeSet = new HashSet<>();
        // 便利一个流程中的各个节点
        for (Node node : nodeList) {
            initNodeAndCondition(node, definition.getId(), definition.getVersion());
            startNum = checkStartAndSame(node, startNum, flowName, nodeCodeSet);
            allNodes.add(node);
            allSkips.addAll(node.getSkipList());
        }
        Map<String, Integer> skipMap = StreamUtils.toMap(allNodes, Node::getNodeCode, Node::getNodeType);
        allSkips.forEach(allSkip -> allSkip.setNextNodeType(skipMap.get(allSkip.getNextNodeCode())));
        AssertUtil.isTrue(startNum == 0, "[" + flowName + "]" + ExceptionCons.LOST_START_NODE);
        // 校验跳转节点的合法性
        checkSkipNode(allSkips);
        // 校验所有目标节点是否都存在
        validaIsExistDestNode(allSkips, nodeCodeSet);
        return combine;
    }

    public static int checkStartAndSame(Node node, int startNum, String flowName, Set<String> nodeCodeSet) {
        if (NodeType.isStart(node.getNodeType())) {
            startNum++;
            AssertUtil.isTrue(startNum > 1, "[" + flowName + "]" + ExceptionCons.MUL_START_NODE);
        }
        // 保证不存在重复的nodeCode
        AssertUtil.contains(nodeCodeSet, node.getNodeCode(),
                "【" + flowName + "】" + ExceptionCons.SAME_NODE_CODE);
        nodeCodeSet.add(node.getNodeCode());
        return startNum;
    }

    /**
     * 校验跳转节点的合法性
     *
     * @param allSkips
     */
    public static void checkSkipNode(List<Skip> allSkips) {
        Map<String, List<Skip>> allSkipMap = StreamUtils.groupByKey(allSkips, Skip::getNowNodeCode);
        // 不可同时通过或者退回到多个中间节点，必须先流转到网关节点
        allSkipMap.forEach((key, values) -> {
            AtomicInteger passNum = new AtomicInteger();
            AtomicInteger rejectNum = new AtomicInteger();
            for (Skip value : values) {
                if (NodeType.isBetween(value.getNowNodeType()) && NodeType.isBetween(value.getNextNodeType())) {
                    if (SkipType.isPass(value.getSkipType())) {
                        passNum.getAndIncrement();
                    } else {
                        rejectNum.getAndIncrement();
                    }
                }
            }
            AssertUtil.isTrue(passNum.get() > 1 || rejectNum.get() > 1, ExceptionCons.MUL_SKIP_BETWEEN);
        });
    }

    /**
     * 校验所有的目标节点是否存在
     *
     * @param allSkips
     * @param nodeCodeSet
     */
    public static void validaIsExistDestNode(List<Skip> allSkips, Set<String> nodeCodeSet) {
        for (int i = 0; i < allSkips.size(); i++) {
            String nextNodeCode = allSkips.get(i).getNextNodeCode();
            AssertUtil.isTrue(!nodeCodeSet.contains(nextNodeCode), "【" + nextNodeCode + "】" + ExceptionCons.NULL_NODE_CODE);
        }
    }


    /**
     * 读取工作节点和跳转条件
     *
     * @param node
     * @param definitionId
     * @param version
     * @return
     */
    public static void initNodeAndCondition(Node node, Long definitionId, String version) {
        String nodeName = node.getNodeName();
        String nodeCode = node.getNodeCode();
        List<Skip> skipList = node.getSkipList();
        if (!NodeType.isEnd(node.getNodeType())) {
            AssertUtil.isEmpty(skipList, ExceptionCons.MUST_SKIP);
        }
        AssertUtil.isEmpty(nodeCode, "[" + nodeName + "]" + ExceptionCons.LOST_NODE_CODE);

        node.setVersion(version);
        node.setDefinitionId(definitionId);
        node.setUpdateTime(new Date());

        // 中间节点的集合， 跳转类型和目标节点不能重复
        Set<String> betweenSet = new HashSet<>();
        // 网关的集合 跳转条件和下目标节点不能重复
        Set<String> gateWaySet = new HashSet<>();
        int skipNum = 0;
        // 遍历节点下的跳转条件
        if (CollUtil.isNotEmpty(skipList)) {
            for (Skip skip : skipList) {
                if (NodeType.isStart(node.getNodeType())) {
                    skipNum++;
                    AssertUtil.isTrue(skipNum > 1, "[" + node.getNodeName() + "]" + ExceptionCons.MUL_START_SKIP);
                }
                AssertUtil.isEmpty(skip.getNextNodeCode(), "【" + nodeName + "】" + ExceptionCons.LOST_DEST_NODE);
                FlowEngine.dataFillHandler().idFill(skip);
                // 流程id
                skip.setDefinitionId(definitionId);
                skip.setNowNodeType(node.getNodeType());
                if (NodeType.isGateWaySerial(node.getNodeType())) {
                    String target = skip.getSkipCondition() + ":" + skip.getNextNodeCode();
                    AssertUtil.contains(gateWaySet, target, "[" + nodeName + "]" + ExceptionCons.SAME_CONDITION_NODE);
                    gateWaySet.add(target);
                } else if (NodeType.isGateWayParallel(node.getNodeType())) {
                    String target = skip.getNextNodeCode();
                    AssertUtil.contains(gateWaySet, target, "[" + nodeName + "]" + ExceptionCons.SAME_DEST_NODE);
                    gateWaySet.add(target);
                } else {
                    String value = skip.getSkipType() + ":" + skip.getNextNodeCode();
                    AssertUtil.contains(betweenSet, value, "[" + nodeName + "]" + ExceptionCons.SAME_CONDITION_VALUE);
                    betweenSet.add(value);
                }
            }
        }
    }

}
