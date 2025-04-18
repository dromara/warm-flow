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
package org.dromara.warm.flow.core.listener;

import org.dromara.warm.flow.core.dto.FlowParams;
import org.dromara.warm.flow.core.entity.Definition;
import org.dromara.warm.flow.core.entity.Instance;
import org.dromara.warm.flow.core.entity.Node;
import org.dromara.warm.flow.core.entity.Task;

import java.util.List;
import java.util.Map;

/**
 * 监听器变量
 *
 * @author warm
 * @see <a href="https://warm-flow.dromara.org/master/advanced/listener.html">文档地址</a>
 */
public class ListenerVariable {

    /**
     * 流程定义
     */
    private Definition definition;

    /**
     * 流程实例
     */
    private Instance instance;

    /**
     * 监听器对应的节点
     */
    private Node node;

    /**
     * 当前任务
     */
    private Task task;

    /**
     * 下一次执行的节点集合
     */
    private List<Node> nextNodes;

    /**
     * 新创建任务集合
     */
    private List<Task> nextTasks;

    /**
     * 流程变量
     */
    private Map<String, Object> variable;

    /**
     * 工作流内置参数
     */
    private FlowParams flowParams;


    public ListenerVariable() {
    }

    public ListenerVariable(Definition definition, Instance instance, Map<String, Object> variable) {
        this.definition = definition;
        this.instance = instance;
        this.variable = variable;
    }

    public ListenerVariable(Definition definition, Instance instance, Node node, Map<String, Object> variable) {
        this.definition = definition;
        this.instance = instance;
        this.node = node;
        this.variable = variable;
    }

    public ListenerVariable(Definition definition, Instance instance, Map<String, Object> variable, Task task) {
        this.definition = definition;
        this.instance = instance;
        this.variable = variable;
        this.task = task;
    }

    public ListenerVariable(Definition definition, Instance instance, Node node, Map<String, Object> variable, Task task) {
        this.definition = definition;
        this.instance = instance;
        this.node = node;
        this.variable = variable;
        this.task = task;
    }

    public ListenerVariable(Definition definition, Instance instance, Node node, Map<String, Object> variable, Task task, List<Node> nextNodes) {
        this.definition = definition;
        this.instance = instance;
        this.node = node;
        this.variable = variable;
        this.task = task;
        this.nextNodes = nextNodes;
    }

    public ListenerVariable(Definition definition, Instance instance, Node node , Map<String, Object> variable, Task task
            , List<Node> nextNodes, List<Task> nextTasks) {
        this.definition = definition;
        this.instance = instance;
        this.node = node;
        this.variable = variable;
        this.task = task;
        this.nextNodes = nextNodes;
        this.nextTasks = nextTasks;
    }

    public Definition getDefinition() {
        return definition;
    }

    public ListenerVariable setDefinition(Definition definition) {
        this.definition = definition;
        return this;
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

    public Task getTask() {
        return task;
    }

    public ListenerVariable setTask(Task task) {
        this.task = task;
        return this;
    }

    public List<Node> getNextNodes() {
        return nextNodes;
    }

    public ListenerVariable setNextNodes(List<Node> nextNodes) {
        this.nextNodes = nextNodes;
        return this;
    }

    public List<Task> getNextTasks() {
        return nextTasks;
    }

    public ListenerVariable setNextTasks(List<Task> nextTasks) {
        this.nextTasks = nextTasks;
        return this;
    }

    public Map<String, Object> getVariable() {
        return variable;
    }

    public ListenerVariable setVariable(Map<String, Object> variable) {
        this.variable = variable;
        return this;
    }

    public FlowParams getFlowParams() {
        return flowParams;
    }

    public ListenerVariable setFlowParams(FlowParams flowParams) {
        this.flowParams = flowParams;
        return this;
    }


    @Override
    public String toString() {
        return "ListenerVariable{" +
                "definition=" + definition +
                ", instance=" + instance +
                ", node=" + node +
                ", task=" + task +
                ", nextNodes=" + nextNodes +
                ", nextTasks=" + nextTasks +
                ", variable=" + variable +
                ", flowParams=" + flowParams +
                '}';
    }
}
