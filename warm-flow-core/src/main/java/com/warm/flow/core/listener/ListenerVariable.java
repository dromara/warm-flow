package com.warm.flow.core.listener;

import com.warm.flow.core.entity.Instance;
import com.warm.flow.core.entity.Node;

import java.util.Map;

/**
 * 监听器变量
 *
 * @author warm
 */
public class ListenerVariable {

    /**
     * 流程实例
     */
    private Instance instance;

    /**
     * 当前节点
     */
    private Node node;
    /**
     * 流程变量
     */
    private Map<String, Object> variable;

    public ListenerVariable(Instance instance, Node node, Map<String, Object> variable) {
        this.instance = instance;
        this.node = node;
        this.variable = variable;
    }

    public Instance getInstance() {
        return instance;
    }

    public ListenerVariable setInstance(Instance instance) {
        this.instance = instance;
        return this;
    }

    public Node getNode() {
        return node;
    }

    public ListenerVariable setNode(Node node) {
        this.node = node;
        return this;
    }

    public Map<String, Object> getVariable() {
        return variable;
    }

    public ListenerVariable setVariable(Map<String, Object> variable) {
        this.variable = variable;
        return this;
    }

    @Override
    public String toString() {
        return "ListenerVariable{" +
                "instance=" + instance +
                ", node=" + node +
                ", variable=" + variable +
                '}';
    }
}
