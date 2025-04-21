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
package org.dromara.warm.flow.core.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.dromara.warm.flow.core.FlowEngine;
import org.dromara.warm.flow.core.entity.Definition;
import org.dromara.warm.flow.core.entity.Node;
import org.dromara.warm.flow.core.entity.Skip;
import org.dromara.warm.flow.core.utils.CollUtil;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 流程定义json对象
 *
 * @author warm
 * @since 2023-03-29
 */
@Setter
@Getter
@Accessors(chain = true)
@ToString
public class DefJson {

    /**
     * 主键
     */
    private Long id;

    /**
     * 流程编码
     */
    private String flowCode;

    /**
     * 流程名称
     */
    private String flowName;

    /**
     * 流程类别
     */
    private String category;

    /**
     * 流程版本
     */
    private String version;

    /**
     * 审批表单是否自定义（Y是 2否）
     */
    private String formCustom;

    /**
     * 审批表单是否自定义（Y是 2否）
     */
    private String formPath;

    /**
     * 监听器类型
     */
    private String listenerType;

    /**
     * 监听器路径
     */
    private String listenerPath;

    /**
     * 扩展字段，预留给业务系统使用
     */
    private String ext;

    /**
     * 扩展map，保存业务自定义扩展属性
     */
    private Map<String, Object> extMap;

    /**
     * 所有节点结合
     */
    private List<NodeJson> nodeList = new ArrayList<>();

    public static DefJson copyDef(Definition definition) {
        DefJson defJson = new DefJson()
                .setFlowCode(definition.getFlowCode())
                .setFlowName(definition.getFlowName())
                .setVersion(definition.getVersion())
                .setCategory(definition.getCategory())
                .setFormCustom(definition.getFormCustom())
                .setFormPath(definition.getFormPath())
                .setListenerType(definition.getListenerType())
                .setListenerPath(definition.getListenerPath())
                .setExt(definition.getExt());

        List<NodeJson> nodeList = new ArrayList<>();
        defJson.setNodeList(nodeList);
        for (Node node : definition.getNodeList()) {
            // 向节点中添加子节点
            NodeJson nodeJson = new NodeJson()
                    .setNodeType(node.getNodeType())
                    .setNodeCode(node.getNodeCode())
                    .setNodeName(node.getNodeName())
                    .setPermissionFlag(node.getPermissionFlag())
                    .setNodeRatio(node.getNodeRatio())
                    .setCoordinate(node.getCoordinate())
                    .setAnyNodeSkip(node.getAnyNodeSkip())
                    .setListenerType(node.getListenerType())
                    .setListenerPath(node.getListenerPath())
                    .setHandlerType(node.getHandlerType())
                    .setHandlerPath(node.getHandlerPath())
                    .setFormCustom(node.getFormCustom())
                    .setFormPath(node.getFormPath())
                    .setExt(node.getExt());
            nodeList.add(nodeJson);

            List<SkipJson> skipList = new ArrayList<>();
            nodeJson.setSkipList(skipList);
            if (CollUtil.isNotEmpty(node.getSkipList())) {
                for (Skip skip : node.getSkipList()) {
                    skipList.add(new SkipJson()
                            .setCoordinate(skip.getCoordinate())
                            .setSkipType(skip.getSkipType())
                            .setSkipName(skip.getSkipName())
                            .setSkipCondition(skip.getSkipCondition())
                            .setNowNodeCode(skip.getNowNodeCode())
                            .setNextNodeCode(skip.getNextNodeCode()));
                }
            }

        }
        return defJson;
    }

    public static Definition copyDef(DefJson defJson) {
        Definition definition = FlowEngine.newDef()
                .setId(defJson.getId())
                .setFlowCode(defJson.getFlowCode())
                .setFlowName(defJson.getFlowName())
                .setVersion(defJson.getVersion())
                .setCategory(defJson.getCategory())
                .setFormCustom(defJson.getFormCustom())
                .setFormPath(defJson.getFormPath())
                .setListenerType(defJson.getListenerType())
                .setListenerPath(defJson.getListenerPath())
                .setExt(defJson.getExt());

        List<Node> nodeList = new ArrayList<>();
        definition.setNodeList(nodeList);
        for (NodeJson nodeJson : defJson.getNodeList()) {
            // 向节点中添加子节点
            Node node = FlowEngine.newNode()
                    .setNodeType(nodeJson.getNodeType())
                    .setNodeCode(nodeJson.getNodeCode())
                    .setNodeName(nodeJson.getNodeName())
                    .setPermissionFlag(nodeJson.getPermissionFlag())
                    .setNodeRatio(nodeJson.getNodeRatio() != null ? nodeJson.getNodeRatio() : BigDecimal.ZERO)
                    .setCoordinate(nodeJson.getCoordinate())
                    .setAnyNodeSkip(nodeJson.getAnyNodeSkip())
                    .setListenerType(nodeJson.getListenerType())
                    .setListenerPath(nodeJson.getListenerPath())
                    .setHandlerType(nodeJson.getHandlerType())
                    .setHandlerPath(nodeJson.getHandlerPath())
                    .setFormCustom(nodeJson.getFormCustom())
                    .setFormPath(nodeJson.getFormPath())
                    .setExt(nodeJson.getExt());
            nodeList.add(node);

            List<Skip> skipList = new ArrayList<>();
            node.setSkipList(skipList);

            if (CollUtil.isNotEmpty(nodeJson.getSkipList())) {
                for (SkipJson skipJson : nodeJson.getSkipList()) {
                    skipList.add(FlowEngine.newSkip()
                            .setCoordinate(skipJson.getCoordinate())
                            .setSkipType(skipJson.getSkipType())
                            .setSkipName(skipJson.getSkipName())
                            .setSkipCondition(skipJson.getSkipCondition())
                            .setNowNodeCode(skipJson.getNowNodeCode())
                            .setNextNodeCode(skipJson.getNextNodeCode()));
                }
            }

        }
        return definition;
    }

    public static DefChart copyChart(DefJson defJson) {
        DefChart defChart = new DefChart();
        defChart.setDefJson(defJson);
        defChart.setNodeJsonList(defJson.getNodeList());
        defChart.setSkipJsonList(Optional.of(defJson)
                .map(DefJson::getNodeList)
                .orElse(Collections.emptyList())
                .stream()
                .map(NodeJson::getSkipList)
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .collect(Collectors.toList()));

        return defChart;
    }

    public static FlowCombine copyCombine(DefJson defJson) {
        Definition definition = copyDef(defJson);
        FlowCombine flowCombine = new FlowCombine();
        flowCombine.setDefinition(definition);
        flowCombine.setAllNodes(definition.getNodeList());
        List<Skip> skipList = Optional.of(definition)
                .map(Definition::getNodeList)
                .orElse(Collections.emptyList())
                .stream()
                .map(Node::getSkipList)
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .collect(Collectors.toList());

        flowCombine.setAllSkips(skipList);
        return flowCombine;
    }

}
