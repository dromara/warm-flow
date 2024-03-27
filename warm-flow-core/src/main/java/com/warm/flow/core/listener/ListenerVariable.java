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

    /**
     * 任务变量
     */
    private Map<String, Object> variableTask;

    public ListenerVariable(Instance instance,Node node, Map<String, Object> variable, Map<String, Object> variableTask) {
        this.instance = instance;
        this.variable = variable;
        this.variableTask = variableTask;
    }

    public Instance getInstance() {
        return instance;
    }

    public ListenerVariable setInstance(Instance instance) {
        this.instance = instance;
        return this;
    }

    public Map<String, Object> getVariable() {
        return variable;
    }

    public ListenerVariable setVariable(Map<String, Object> variable) {
        this.variable = variable;
        return this;
    }

    public Map<String, Object> getVariableTask() {
        return variableTask;
    }

    public ListenerVariable setVariableTask(Map<String, Object> variableTask) {
        this.variableTask = variableTask;
        return this;
    }

    @Override
    public String toString() {
        return "ListenerVariable{" +
                "instance=" + instance +
                ", variable=" + variable +
                ", variableTask=" + variableTask +
                '}';
    }
}
