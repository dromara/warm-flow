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

import org.dromara.warm.flow.core.FlowEngine;
import org.dromara.warm.flow.core.constant.ExceptionCons;
import org.dromara.warm.flow.core.dto.FlowCombine;
import org.dromara.warm.flow.core.dto.FlowParams;
import org.dromara.warm.flow.core.dto.PathWayData;
import org.dromara.warm.flow.core.entity.*;
import org.dromara.warm.flow.core.enums.*;
import org.dromara.warm.flow.core.listener.Listener;
import org.dromara.warm.flow.core.listener.ListenerVariable;
import org.dromara.warm.flow.core.orm.dao.FlowInstanceDao;
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
        Definition definition = FlowEngine.defService().getPublishByFlowCode(flowParams.getFlowCode());
        FlowCombine flowCombine = FlowEngine.defService().getFlowCombine(definition);
        // 获取开始节点
        Node startNode = StreamUtils.filterOne(flowCombine.getAllNodes(), t -> NodeType.isStart(t.getNodeType()));
        AssertUtil.isNull(startNode, ExceptionCons.LOST_START_NODE);

        // 判断流程定义是否激活状态
        AssertUtil.isTrue(definition.getActivityStatus().equals(ActivityStatus.SUSPENDED.getKey())
                , ExceptionCons.NOT_DEFINITION_ACTIVITY);
        flowParams.skipType(SkipType.PASS.getKey());

        // 执行开始监听器
        ListenerUtil.executeListener(new ListenerVariable(definition, null, startNode, flowParams.getVariable())
                .setFlowParams(flowParams), Listener.LISTENER_START);


        // 获取下一个节点，如果是网关节点，则重新获取后续节点
        PathWayData pathWayData = new PathWayData().setDefId(startNode.getDefinitionId()).setSkipType(flowParams.getSkipType());
        List<Node> nextNodes = FlowEngine.nodeService().getNextNodeList(startNode, null, flowParams.getSkipType(),
                flowParams.getVariable(), pathWayData, flowCombine);

        // 设置流程实例对象
        Instance instance = setStartInstance(nextNodes.get(0), businessId, flowParams);

        // 设置历史任务
        HisTask hisTask = setHisTask(nextNodes, flowParams, startNode, instance.getId());

        List<Task> addTasks = StreamUtils.toList(nextNodes, node -> FlowEngine.taskService()
                .addTask(node, instance, definition, flowParams));

        // 办理人变量替换
        if (CollUtil.isNotEmpty(addTasks)) {
            ExpressionUtil.evalVariable(addTasks, flowParams.getVariable());
        }

        // 执行分派监听器
        ListenerUtil.executeListener(new ListenerVariable(definition, instance, startNode, flowParams.getVariable()
                , null, nextNodes, addTasks).setFlowParams(flowParams), Listener.LISTENER_ASSIGNMENT);

        // 设置流程图元数据
        pathWayData.getTargetNodes().addAll(nextNodes);
        instance.setDefJson(FlowEngine.chartService().startMetadata(pathWayData));

        // 开启流程，保存流程信息
        saveFlowInfo(instance, addTasks, hisTask, flowParams);

        // 执行完成和创建监听器
        ListenerUtil.endCreateListener(new ListenerVariable(definition, instance, startNode, flowParams.getVariable()
                , null, nextNodes, addTasks).setFlowParams(flowParams));

        return instance;
    }

    @Override
    public Instance skipByInsId(Long instanceId, FlowParams flowParams) {
        List<Task> taskList = FlowEngine.taskService().getByInsId(instanceId);
        AssertUtil.isEmpty(taskList, ExceptionCons.NOT_FOUNT_TASK);
        AssertUtil.isTrue(taskList.size() > 1, ExceptionCons.TASK_NOT_ONE);
        return FlowEngine.taskService().skip(flowParams, taskList.get(0));
    }

    @Override
    public List<Instance> listByDefIds(List<Long> defIds) {
        return getDao().getByDefIds(defIds);
    }

    @Override
    public Instance termination(Long instanceId, FlowParams flowParams) {
        // 获取待办任务
        List<Task> taskList = FlowEngine.taskService().getByInsId(instanceId);
        AssertUtil.isEmpty(taskList, ExceptionCons.NOT_FOUNT_TASK);
        Task task = taskList.get(0);
        return FlowEngine.taskService().termination(task, flowParams);
    }

    @Override
    public boolean remove(List<Long> instanceIds) {
        return toRemoveTask(instanceIds);
    }

    @Override
    public List<Instance> getByDefId(Long definitionId) {
        return list(FlowEngine.newIns().setDefinitionId(definitionId));
    }

    /**
     * 设置历史任务
     *
     * @param nextNodes  下一节点集合
     * @param flowParams 流程参数
     * @param startNode  开始节点
     * @param instanceId 流程实例id
     */
    private HisTask setHisTask(List<Node> nextNodes, FlowParams flowParams, Node startNode, Long instanceId) {
        Task startTask = FlowEngine.newTask()
                .setInstanceId(instanceId)
                .setDefinitionId(startNode.getDefinitionId())
                .setNodeCode(startNode.getNodeCode())
                .setNodeName(startNode.getNodeName())
                .setNodeType(startNode.getNodeType());
        FlowEngine.dataFillHandler().idFill(startTask);
        // 开始任务转历史任务
        return FlowEngine.hisTaskService().setSkipInsHis(startTask, nextNodes, flowParams);
    }

    /**
     * 开启流程，保存流程信息
     *
     * @param instance 流程实例
     * @param addTasks 新增任务
     * @param hisTask 历史任务
     */
    private void saveFlowInfo(Instance instance, List<Task> addTasks, HisTask hisTask, FlowParams flowParams) {
        FlowEngine.taskService().setInsFinishInfo(instance, addTasks, flowParams);
        // 待办任务设置处理人
        if (CollUtil.isNotEmpty(addTasks)) {
            List<User> users = FlowEngine.userService().taskAddUsers(addTasks);
            FlowEngine.taskService().saveBatch(addTasks);
            FlowEngine.userService().saveBatch(users);
        }
        FlowEngine.hisTaskService().save(hisTask);
        save(instance);
    }

    /**
     * 设置流程实例对象
     *
     * @param firstBetweenNode 第一个中间节点
     * @param businessId       业务id
     * @return Instance
     */
    private Instance setStartInstance(Node firstBetweenNode, String businessId
            , FlowParams flowParams) {
        Instance instance = FlowEngine.newIns();
        Date now = new Date();
        FlowEngine.dataFillHandler().idFill(instance);
        // 关联业务id,起始后面可以不用到业务id,传业务id目前来看只是为了批量创建流程的时候能创建出有区别化的流程,也是为了后期需要用到businessId。
        instance.setDefinitionId(firstBetweenNode.getDefinitionId())
                .setBusinessId(businessId)
                .setNodeType(firstBetweenNode.getNodeType())
                .setNodeCode(firstBetweenNode.getNodeCode())
                .setNodeName(firstBetweenNode.getNodeName())
                .setFlowStatus(StringUtils.emptyDefault(flowParams.getFlowStatus(), FlowStatus.TOBESUBMIT.getKey()))
                .setActivityStatus(ActivityStatus.ACTIVITY.getKey())
                .setVariable(FlowEngine.jsonConvert.objToStr(flowParams.getVariable()))
                .setCreateTime(now)
                .setUpdateTime(now)
                .setCreateBy(flowParams.getHandler())
                .setExt(flowParams.getExt());
        return instance;
    }

    private boolean toRemoveTask(List<Long> instanceIds) {
        AssertUtil.isEmpty(instanceIds, ExceptionCons.NULL_INSTANCE_ID);

        List<Long> taskIds = new ArrayList<>();
        instanceIds.forEach(instanceId -> taskIds.addAll(
                FlowEngine.taskService()
                        .list(FlowEngine.newTask().setInstanceId(instanceId))
                        .stream()
                        .map(Task::getId)
                        .collect(Collectors.toList())));

        if (CollUtil.isNotEmpty(taskIds)) {
            FlowEngine.userService().deleteByTaskIds(taskIds);
        }

        boolean success = FlowEngine.taskService().deleteByInsIds(instanceIds);
        if (success) {
            FlowEngine.hisTaskService().deleteByInsIds(instanceIds);
            return FlowEngine.insService().removeByIds(instanceIds);
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
