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
package org.dromara.warm.flow.ui.vo;

import org.dromara.warm.flow.core.entity.Definition;
import org.dromara.warm.flow.core.entity.Node;
import org.dromara.warm.flow.core.entity.Skip;
import org.dromara.warm.flow.core.utils.CollUtil;
import org.dromara.warm.flow.orm.entity.FlowDefinition;
import org.dromara.warm.flow.orm.entity.FlowNode;
import org.dromara.warm.flow.orm.entity.FlowSkip;

import java.util.ArrayList;
import java.util.List;

/**
 * 流程定义对象Vo
 *
 * @author warm
 * @since 2023-03-29
 */
public class DefVo {

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

    private List<NodeVo> nodeList = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public DefVo setId(Long id) {
        this.id = id;
        return this;
    }

    public String getFlowCode() {
        return flowCode;
    }

    public DefVo setFlowCode(String flowCode) {
        this.flowCode = flowCode;
        return this;
    }

    public String getFlowName() {
        return flowName;
    }

    public DefVo setFlowName(String flowName) {
        this.flowName = flowName;
        return this;
    }

    public String getCategory() {
        return category;
    }

    public DefVo setCategory(String category) {
        this.category = category;
        return this;
    }

    public String getVersion() {
        return version;
    }

    public DefVo setVersion(String version) {
        this.version = version;
        return this;
    }

    public String getFormCustom() {
        return formCustom;
    }

    public DefVo setFormCustom(String formCustom) {
        this.formCustom = formCustom;
        return this;
    }

    public String getFormPath() {
        return formPath;
    }

    public DefVo setFormPath(String formPath) {
        this.formPath = formPath;
        return this;
    }

    public String getListenerType() {
        return listenerType;
    }

    public DefVo setListenerType(String listenerType) {
        this.listenerType = listenerType;
        return this;
    }

    public String getListenerPath() {
        return listenerPath;
    }

    public DefVo setListenerPath(String listenerPath) {
        this.listenerPath = listenerPath;
        return this;
    }

    public String getExt() {
        return ext;
    }

    public DefVo setExt(String ext) {
        this.ext = ext;
        return this;
    }


    public List<NodeVo> getNodeList() {
        return nodeList;
    }

    public DefVo setNodeList(List<NodeVo> nodeList) {
        this.nodeList = nodeList;
        return this;
    }

    public DefVo copyDef(Definition definition) {
        DefVo defVo = new DefVo()
                .setFlowCode(definition.getFlowCode())
                .setFlowName(definition.getFlowName())
                .setVersion(definition.getVersion())
                .setCategory(definition.getCategory())
                .setFormCustom(definition.getFormCustom())
                .setFormPath(definition.getFormPath())
                .setListenerType(definition.getListenerType())
                .setListenerPath(definition.getListenerPath())
                .setExt(definition.getExt());

        List<NodeVo> nodeList = new ArrayList<>();
        defVo.setNodeList(nodeList);
        for (Node node : definition.getNodeList()) {
            // 向节点中添加子节点
            NodeVo nodeVo = new NodeVo()
                    .setNodeType(node.getNodeType())
                    .setNodeCode(node.getNodeCode())
                    .setNodeName(node.getNodeName())
                    .setPermissionFlag(node.getPermissionFlag())
                    .setNodeRatio(node.getNodeRatio())
                    .setCoordinate(node.getCoordinate())
                    .setSkipAnyNode(node.getSkipAnyNode())
                    .setListenerType(node.getListenerType())
                    .setListenerPath(node.getListenerPath())
                    .setHandlerType(node.getHandlerType())
                    .setHandlerPath(node.getHandlerPath())
                    .setFormCustom(node.getFormCustom())
                    .setFormPath(node.getFormPath());
            nodeList.add(nodeVo);

            List<SkipVo> skipList = new ArrayList<>();
            nodeVo.setSkipList(skipList);
            if (CollUtil.isNotEmpty(node.getSkipList())) {
                for (Skip skip : node.getSkipList()) {
                    skipList.add(new SkipVo()
                            .setCoordinate(skip.getCoordinate())
                            .setSkipType(skip.getSkipType())
                            .setSkipName(skip.getSkipName())
                            .setSkipCondition(skip.getSkipCondition())
                            .setNowNodeCode(skip.getNowNodeCode())
                            .setNextNodeCode(skip.getNextNodeCode()));
                }
            }

        }
        return defVo;
    }

    public Definition copyDef(DefVo defVo) {
        Definition definition = new FlowDefinition()
                .setId(defVo.getId())
                .setFlowCode(defVo.getFlowCode())
                .setFlowName(defVo.getFlowName())
                .setVersion(defVo.getVersion())
                .setCategory(defVo.getCategory())
                .setFormCustom(defVo.getFormCustom())
                .setFormPath(defVo.getFormPath())
                .setListenerType(defVo.getListenerType())
                .setListenerPath(defVo.getListenerPath())
                .setExt(defVo.getExt());

        List<Node> nodeList = new ArrayList<>();
        definition.setNodeList(nodeList);
        for (NodeVo nodeVo : defVo.getNodeList()) {
            // 向节点中添加子节点
            Node node = new FlowNode()
                    .setNodeType(nodeVo.getNodeType())
                    .setNodeCode(nodeVo.getNodeCode())
                    .setNodeName(nodeVo.getNodeName())
                    .setPermissionFlag(nodeVo.getPermissionFlag())
                    .setNodeRatio(nodeVo.getNodeRatio())
                    .setCoordinate(nodeVo.getCoordinate())
                    .setSkipAnyNode(nodeVo.getSkipAnyNode())
                    .setListenerType(nodeVo.getListenerType())
                    .setListenerPath(nodeVo.getListenerPath())
                    .setHandlerType(nodeVo.getHandlerType())
                    .setHandlerPath(nodeVo.getHandlerPath())
                    .setFormCustom(nodeVo.getFormCustom())
                    .setFormPath(nodeVo.getFormPath());
            nodeList.add(node);

            List<Skip> skipList = new ArrayList<>();
            node.setSkipList(skipList);

            if (CollUtil.isNotEmpty(nodeVo.getSkipList())) {
                for (SkipVo skipVo : nodeVo.getSkipList()) {
                    skipList.add(new FlowSkip()
                            .setCoordinate(skipVo.getCoordinate())
                            .setSkipType(skipVo.getSkipType())
                            .setSkipName(skipVo.getSkipName())
                            .setSkipCondition(skipVo.getSkipCondition())
                            .setNowNodeCode(skipVo.getNowNodeCode())
                            .setNextNodeCode(skipVo.getNextNodeCode()));
                }
            }

        }
        return definition;
    }

}
