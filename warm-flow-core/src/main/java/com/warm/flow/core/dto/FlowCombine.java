package com.warm.flow.core.dto;


import com.warm.flow.core.FlowFactory;
import com.warm.flow.core.entity.Definition;
import com.warm.flow.core.entity.Node;
import com.warm.flow.core.entity.Skip;
import com.warm.flow.core.entity.User;

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
     * 所有的流程节点的权限用户
     */
    private List<User> allUsers = new ArrayList<>();

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

    public List<User> getAllUsers() {
        return allUsers;
    }

    public void setAllUsers(List<User> allUsers) {
        this.allUsers = allUsers;
    }

    public List<Skip> getAllSkips() {
        return allSkips;
    }

    public void setAllSkips(List<Skip> allSkips) {
        this.allSkips = allSkips;
    }
}
