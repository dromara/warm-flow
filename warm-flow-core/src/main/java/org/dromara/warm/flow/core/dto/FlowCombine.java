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


import org.dromara.warm.flow.core.FlowFactory;
import org.dromara.warm.flow.core.entity.Definition;
import org.dromara.warm.flow.core.entity.Node;
import org.dromara.warm.flow.core.entity.Skip;

import java.util.ArrayList;
import java.util.List;


/**
 * @author warm
 * @description: 流程初始化数据集合
 * @date: 2023/3/30 14:27
 */
public class FlowCombine {
    /**
     * 所有的流程定义
     */
    private Definition definition = FlowFactory.newDef();

    /**
     * 所有的流程节点
     */
    private List<Node> allNodes = new ArrayList<>();

    /**
     * 所有的流程节点跳转关联
     */
    private List<Skip> allSkips = new ArrayList<>();

    public Definition getDefinition() {
        return definition;
    }

    public void setDefinition(Definition definition) {
        this.definition = definition;
    }

    public List<Node> getAllNodes() {
        return allNodes;
    }

    public void setAllNodes(List<Node> allNodes) {
        this.allNodes = allNodes;
    }

    public List<Skip> getAllSkips() {
        return allSkips;
    }

    public void setAllSkips(List<Skip> allSkips) {
        this.allSkips = allSkips;
    }
}
