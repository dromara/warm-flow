package com.warm.flow.core.domain.dto;


import com.warm.flow.core.domain.entity.FlowDefinition;
import com.warm.flow.core.domain.entity.FlowNode;
import com.warm.flow.core.domain.entity.FlowSkip;

import java.util.ArrayList;
import java.util.List;


/**
 * @author minliuhua
 * @description: 流程初始化数据集合
 * @date: 2023/3/30 14:27
 */
public class FlowCombine {
    /**
     * 所有的流程定义
     */
    private FlowDefinition definition = new FlowDefinition();

    /**
     * 所有的流程节点
     */
    private List<FlowNode> allNodes = new ArrayList<>();

    /**
     * 所有的流程节点跳转关联
     */
    private List<FlowSkip> allSkips = new ArrayList<>();

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
