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
package org.dromara.warm.flow.ui.dto;

import org.dromara.warm.flow.orm.entity.FlowDefinition;
import org.dromara.warm.flow.orm.entity.FlowNode;
import org.dromara.warm.flow.orm.entity.FlowSkip;

import java.util.List;

/**
 * 流程设计器-流程定义数据传输对象
 *
 * @author warm
 */
public class FlowCombineDto {

    /**
     * 流程定义
     */
    private FlowDefinition definition;

    /**
     * 所有的流程节点
     */
    private List<FlowNode> allNodes;

    /**
     * 所有的流程节点跳转关联
     */
    private List<FlowSkip> allSkips;

    public FlowDefinition getDefinition() {
        return definition;
    }

    public void setDefinition(FlowDefinition definition) {
        this.definition = definition;
    }

    public List<FlowNode> getAllNodes() {
        return allNodes;
    }

    public void setAllNodes(List<FlowNode> allNodes) {
        this.allNodes = allNodes;
    }

    public List<FlowSkip> getAllSkips() {
        return allSkips;
    }

    public void setAllSkips(List<FlowSkip> allSkips) {
        this.allSkips = allSkips;
    }

}
