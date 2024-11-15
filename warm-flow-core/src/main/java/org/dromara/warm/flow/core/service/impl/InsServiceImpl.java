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
package org.dromara.warm.flow.core.service.impl;

import org.dromara.warm.flow.core.FlowFactory;
import org.dromara.warm.flow.core.constant.ExceptionCons;
import org.dromara.warm.flow.core.dao.FlowInstanceDao;
import org.dromara.warm.flow.core.dto.FlowParams;
import org.dromara.warm.flow.core.entity.*;
import org.dromara.warm.flow.core.enums.ActivityStatus;
import org.dromara.warm.flow.core.enums.FlowStatus;
import org.dromara.warm.flow.core.enums.NodeType;
import org.dromara.warm.flow.core.listener.Listener;
import org.dromara.warm.flow.core.listener.ListenerVariable;
import org.dromara.warm.flow.core.orm.service.impl.WarmServiceImpl;
import org.dromara.warm.flow.core.service.InsService;
import org.dromara.warm.flow.core.utils.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 流程实例Service业务层处理
 *
 * @author warm
 * @since 2023-03-29
 */
public class InsServiceImpl extends WarmServiceImpl<FlowInstanceDao<Instance>, Instance> implements InsService {

    @Override
    public InsService setDao(FlowInstanceDao<Instance> warmDao) {
        this.warmDao = warmDao;
        return this;
    }

    @Override
    public Instance start(String businessId, FlowParams flowParams) {
        AssertUtil.isNull(flowParams.getFlowCode(), ExceptionCons.NULL_FLOW_CODE);
        AssertUtil.isEmpty(businessId, ExceptionCons.NULL_BUSINESS_ID);
        // 获取已发布的流程节点
        List<Node> nodes = FlowFactory.nodeService().getByFlowCode(flowParams.getFlowCode());
        AssertUtil.isEmpty(nodes, String.format(ExceptionCons.NOT_PUBLISH_NODE, flowParams.getFlowCode()));
        // 获取开始节点
        Node startNode = nodes.stream().filter(t -> NodeType.isStart(t.getNodeType())).findFirst().orElse(null);
        AssertUtil.isNull(startNode, ExceptionCons.LOST_START_NODE);
        // 获取下一个节点，如果是网关节点，则重新获取后续节点
        List<Node> nextNodes = FlowFactory.nodeService().getNextByCheckGateway(flowParams.getVariable()
                , getFirstBetween(startNode));

        // 判断流程定义是否激活状态
        Definition definition = FlowFactory.defService().getById(nextNodes.get(0).getDefinitionId());
        AssertUtil.isTrue(definition.getActivityStatus().equals(ActivityStatus.SUSPENDED.getKey())
                , ExceptionCons.NOT_DEFINITION_ACTIVITY);

        // 执行开始监听器
        ListenerUtil.executeListener(new ListenerVariable(definition, null, startNode, flowParams.getVariable())
                .setFlowParams(flowParams), Listener.LISTENER_START);

        // 设置流程实例对象
        Instance instance = setStartInstance(nextNodes.get(0), businessId, flowParams);

        // 判断开始结点和下一结点是否有权限监听器,有执行权限监听器node.setPermissionFlag,无走数据库的权限标识符
        ListenerUtil.executeGetNodePermission(new ListenerVariable(definition, instance, startNode, flowParams.getVariable()
                , null, nextNodes).setFlowParams(flowParams));

        // 设置历史任务
        List<HisTask> hisTasks = setHisTask(nextNodes, flowParams, startNode, instance.getId());

        List<Task> addTasks = StreamUtils.toList(nextNodes, node -> FlowFactory.taskService()
                .addTask(node, instance, definition, flowParams));

        // 办理人变量替换
        if (CollUtil.isNotEmpty(addTasks)) {
            VariableUtil.replacement(addTasks, flowParams.getVariable());
        }

        // 执行分派监听器
        ListenerUtil.executeListener(new ListenerVariable(definition, instance, startNode, flowParams.getVariable()
                , null, nextNodes, addTasks).setFlowParams(flowParams), Listener.LISTENER_ASSIGNMENT);

        // 开启流程，保存流程信息
        saveFlowInfo(instance, addTasks, hisTasks);

        // 执行结束监听器和下一节点的节点开始监听器
        ListenerUtil.endCreateListener(new ListenerVariable(definition, instance, startNode, flowParams.getVariable()
                , null, nextNodes, addTasks).setFlowParams(flowParams));

        return instance;
    }

    @Override
    public Instance skipByInsId(Long instanceId, FlowParams flowParams) {
        AssertUtil.isTrue(StringUtils.isNotEmpty(flowParams.getMessage())
                && flowParams.getMessage().length() > 500, ExceptionCons.MSG_OVER_LENGTH);
        // 获取待办任务
        List<Task> taskList = FlowFactory.taskService().list(FlowFactory.newTask().setInstanceId(instanceId));
        AssertUtil.isEmpty(taskList, ExceptionCons.NOT_FOUNT_TASK);
        AssertUtil.isTrue(taskList.size() > 1, ExceptionCons.TASK_NOT_ONE);
        Task task = taskList.get(0);
        return FlowFactory.taskService().skip(flowParams, task);
    }

    @Override
    public Instance termination(Long instanceId, FlowParams flowParams) {
        // 获取待办任务
        List<Task> taskList = FlowFactory.taskService().list(FlowFactory.newTask().setInstanceId(instanceId));
        AssertUtil.isEmpty(taskList, ExceptionCons.NOT_FOUNT_TASK);
        Task task = taskList.get(0);
        return FlowFactory.taskService().termination(task, flowParams);
    }

    @Override
    public boolean remove(List<Long> instanceIds) {
        return toRemoveTask(instanceIds);
    }


    /**
     * 设置历史任务
     *
     * @param nextNodes 下一节点集合
     * @param flowParams 流程参数
     * @param startNode 开始节点
     * @param instanceId 流程实例id
     */
    private List<HisTask> setHisTask(List<Node> nextNodes, FlowParams flowParams, Node startNode, Long instanceId) {
        Task startTask = FlowFactory.newTask()
                .setInstanceId(instanceId)
                .setDefinitionId(startNode.getDefinitionId())
                .setNodeCode(startNode.getNodeCode())
                .setNodeName(startNode.getNodeName())
                .setNodeType(startNode.getNodeType());
        FlowFactory.dataFillHandler().idFill(startTask);
        // 开始任务转历史任务
        return FlowFactory.hisTaskService().setSkipInsHis(startTask, nextNodes, flowParams);
    }

    /**
     * 开启流程，保存流程信息
     *
     * @param instance 流程实例
     * @param addTasks 新增任务
     * @param hisTasks 历史任务
     */
    private void saveFlowInfo(Instance instance, List<Task> addTasks, List<HisTask> hisTasks) {
        // 待办任务设置处理人
        List<User> users = FlowFactory.userService().taskAddUsers(addTasks);
        FlowFactory.hisTaskService().saveBatch(hisTasks);
        FlowFactory.taskService().saveBatch(addTasks);
        FlowFactory.userService().saveBatch(users);
        save(instance);
    }

    /**
     * 设置流程实例对象
     *
     * @param firstBetweenNode 第一个中间节点
     * @param businessId 业务id
     * @return Instance
     */
    private Instance setStartInstance(Node firstBetweenNode, String businessId
            , FlowParams flowParams) {
        Instance instance = FlowFactory.newIns();
        Date now = new Date();
        FlowFactory.dataFillHandler().idFill(instance);
        // 关联业务id,起始后面可以不用到业务id,传业务id目前来看只是为了批量创建流程的时候能创建出有区别化的流程,也是为了后期需要用到businessId。
        instance.setDefinitionId(firstBetweenNode.getDefinitionId())
                .setBusinessId(businessId)
                .setNodeType(firstBetweenNode.getNodeType())
                .setNodeCode(firstBetweenNode.getNodeCode())
                .setNodeName(firstBetweenNode.getNodeName())
                .setFlowStatus(ObjectUtil.isNotNull(flowParams.getFlowStatus())? flowParams.getFlowStatus()
                        : FlowStatus.TOBESUBMIT.getKey())
                .setActivityStatus(ActivityStatus.ACTIVITY.getKey())
                .setVariable(FlowFactory.jsonConvert.mapToStr(flowParams.getVariable()))
                .setCreateTime(now)
                .setUpdateTime(now)
                .setCreateBy(flowParams.getHandler())
                .setExt(flowParams.getExt());
        return instance;
    }

    /**
     * 有且只能有一个开始节点
     *
     * @param startNode 开始节点
     * @return Node
     */
    private Node getFirstBetween(Node startNode) {
        List<Skip> skips = FlowFactory.skipService().list(FlowFactory.newSkip()
                .setDefinitionId(startNode.getDefinitionId()).setNowNodeCode(startNode.getNodeCode()));
        Skip skip = skips.get(0);
        return FlowFactory.nodeService().getOne(FlowFactory.newNode().setDefinitionId(startNode.getDefinitionId())
                .setNodeCode(skip.getNextNodeCode()));
    }

    private boolean toRemoveTask(List<Long> instanceIds) {
        AssertUtil.isEmpty(instanceIds, ExceptionCons.NULL_INSTANCE_ID);

        List<Long> taskIds = new ArrayList<>();
        instanceIds.forEach(instanceId -> taskIds.addAll(
                FlowFactory.taskService()
                        .list(FlowFactory.newTask().setInstanceId(instanceId))
                        .stream()
                        .map(Task::getId)
                        .collect(Collectors.toList())));

        FlowFactory.userService().deleteByTaskIds(taskIds);

        boolean success = FlowFactory.taskService().deleteByInsIds(instanceIds);
        if (success) {
            FlowFactory.hisTaskService().deleteByInsIds(instanceIds);
            return FlowFactory.insService().removeByIds(instanceIds);
        }
        return false;
    }

    @Override
    public boolean active(Long id) {
        Instance instance = getById(id);
        AssertUtil.isTrue(instance.getActivityStatus().equals(ActivityStatus.ACTIVITY.getKey()), ExceptionCons.INSTANCE_ALREADY_ACTIVITY);
        instance.setActivityStatus(ActivityStatus.ACTIVITY.getKey());
        return updateById(instance);
    }

    @Override
    public boolean unActive(Long id) {
        Instance instance = getById(id);
        AssertUtil.isTrue(instance.getActivityStatus().equals(ActivityStatus.SUSPENDED.getKey()), ExceptionCons.INSTANCE_ALREADY_SUSPENDED);
        instance.setActivityStatus(ActivityStatus.SUSPENDED.getKey());
        return updateById(instance);
    }
}
