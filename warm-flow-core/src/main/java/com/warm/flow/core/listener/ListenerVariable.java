package com.warm.flow.core.listener;

import com.warm.flow.core.entity.Instance;
import com.warm.flow.core.entity.Node;
import com.warm.flow.core.entity.Task;

import java.util.List;
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
     * 当前任务（启动流程时开始和权限监听器不会传入）
     */
    private Task task;

    /**
     * 新创建任务集合（只有完成监听器和创建监听器会传入）
     */
    private List<Task> nextTasks;

    /**
     * 流程变量
     */
    private Map<String, Object> variable;

    /**
     * 权限监听器使用
     * 权限标识 例如：[role:admin,user:2]
     */
    private List<NodePermission> nodePermissionList;

    public ListenerVariable() {
    }

    public ListenerVariable(Instance instance, Map<String, Object> variable) {
        this.instance = instance;
        this.variable = variable;
    }

    public ListenerVariable(Instance instance, Node node, Map<String, Object> variable) {
        this.instance = instance;
        this.node = node;
        this.variable = variable;
    }

    public ListenerVariable(Instance instance, Map<String, Object> variable, Task task) {
        this.instance = instance;
        this.variable = variable;
        this.task = task;
    }

    public ListenerVariable(Instance instance, Node node, Map<String, Object> variable, Task task) {
        this.instance = instance;
        this.node = node;
        this.variable = variable;
        this.task = task;
    }

    public ListenerVariable(Instance instance, Map<String, Object> variable, Task task, List<Task> nextTasks) {
        this.instance = instance;
        this.variable = variable;
        this.task = task;
        this.nextTasks = nextTasks;
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

    public List<NodePermission> getNodePermissionList() {
        return nodePermissionList;
    }

    public void setNodePermissionList(List<NodePermission> nodePermissionList) {
        this.nodePermissionList = nodePermissionList;
    }

    public NodePermission getPermissionByNode(String nodeCode) {

        return nodePermissionList.stream().filter(t -> t.getNodeCode().equals(nodeCode))
                .findFirst()
                .orElse(null);
    }

    @Override
    public String toString() {
        return "ListenerVariable{" +
                "instance=" + instance +
                ", node=" + node +
                ", variable=" + variable +
                ", nodePermissionList=" + nodePermissionList +
                '}';
    }
}
