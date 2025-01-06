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
import org.dromara.warm.flow.core.constant.FlowCons;
import org.dromara.warm.flow.core.dto.FlowDto;
import org.dromara.warm.flow.core.dto.FlowParams;
import org.dromara.warm.flow.core.dto.PathWayData;
import org.dromara.warm.flow.core.entity.*;
import org.dromara.warm.flow.core.enums.*;
import org.dromara.warm.flow.core.listener.Listener;
import org.dromara.warm.flow.core.listener.ListenerVariable;
import org.dromara.warm.flow.core.orm.dao.FlowTaskDao;
import org.dromara.warm.flow.core.orm.service.impl.WarmServiceImpl;
import org.dromara.warm.flow.core.service.TaskService;
import org.dromara.warm.flow.core.utils.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * 待办任务Service业务层处理
 *
 * @author warm
 * @since 2023-03-29
 */
public class TaskServiceImpl extends WarmServiceImpl<FlowTaskDao<Task>, Task> implements TaskService {

    @Override
    public TaskService setDao(FlowTaskDao<Task> warmDao) {
        this.warmDao = warmDao;
        return this;
    }

    @Override
    public Instance skip(Long taskId, FlowParams flowParams) {
        AssertUtil.isTrue(StringUtils.isNotEmpty(flowParams.getMessage())
                && flowParams.getMessage().length() > 500, ExceptionCons.MSG_OVER_LENGTH);
        // 获取待办任务
        Task task = getById(taskId);
        return skip(flowParams, task);
    }

    @Override
    public Instance skip(FlowParams flowParams, Task task) {
        // TODO min 后续考虑并发问题，待办任务和实例表不同步，可给待办任务id加锁，抽取所接口，方便后续兼容分布式锁
        // 流程开启前正确性校验
        R r = getAndCheck(task);
        flowParams.variable(MapUtil.mergeAll(r.instance.getVariableMap(), flowParams.getVariable()));
        // 非第一个记得跳转类型必传
        if (!NodeType.isStart(task.getNodeType())) {
            AssertUtil.isFalse(StringUtils.isNotEmpty(flowParams.getSkipType()), ExceptionCons.NULL_CONDITION_VALUE);
        }

        // 执行开始监听器
        task.setUserList(FlowEngine.userService().listByAssociatedAndTypes(task.getId()));
        ListenerUtil.executeListener(new ListenerVariable(r.definition, r.instance, r.nowNode, flowParams.getVariable()
                , task).setFlowParams(flowParams), Listener.LISTENER_START);

        // 如果是受托人在处理任务，需要处理一条委派记录，并且更新委托人，回到计划审批人,然后直接返回流程实例
        if (handleDepute(task, flowParams)) {
            return r.instance;
        }

        // 判断当前处理人是否有权限处理
        checkAuth(task, flowParams);

        //或签、会签、票签逻辑处理
        if (cooperate(r.nowNode, task, flowParams)) {
            return r.instance;
        }

        // 获取后续任务节点结合
        PathWayData pathWayData = new PathWayData().setInsId(task.getInstanceId()).setSkipType(flowParams.getSkipType());
        List<Node> nextNodes = FlowEngine.nodeService().getNextNodeList(task.getDefinitionId(), r.nowNode
                , flowParams.getNodeCode(), flowParams.getSkipType(), flowParams.getVariable(), pathWayData);
        filterUnFinishNode(task, r.instance, nextNodes);

        // 构建增待办任务和设置结束任务历史记录
        List<Task> addTasks = StreamUtils.toList(nextNodes, node -> FlowEngine.taskService()
                .addTask(node, r.instance, r.definition, flowParams));

        // 办理人变量替换
        ExpressionUtil.evalVariable(addTasks, MapUtil.mergeAll(r.instance.getVariableMap(), flowParams.getVariable()));

        // 执行分派监听器
        ListenerUtil.executeListener(new ListenerVariable(r.definition, r.instance, r.nowNode, flowParams.getVariable()
                , task, nextNodes, addTasks).setFlowParams(flowParams), Listener.LISTENER_ASSIGNMENT);

        // 设置流程图元数据
        r.instance.setDefJson(FlowEngine.chartService().skipMetadata(pathWayData));

        // 更新流程信息
        updateFlowInfo(task, r.instance, addTasks, flowParams, nextNodes);

        // 一票否决（谨慎使用），如果退回，退回指向节点后还存在其他正在执行的待办任务，转历史任务，状态都为失效,重走流程。
        oneVoteVeto(task, flowParams, nextNodes.get(0).getNodeCode());

        // 处理未完成的任务，当流程完成，还存在待办任务未完成，转历史任务，状态完成。
        handUndoneTask(r.instance, flowParams);

        // 最后判断是否存在节点监听器，存在执行节点监听器
        ListenerUtil.endCreateListener(new ListenerVariable(r.definition, r.instance, r.nowNode
                , flowParams.getVariable(), task, nextNodes, addTasks).setFlowParams(flowParams));

        return r.instance;
    }

    @Override
    public Instance termination(Long taskId, FlowParams flowParams) {
        return termination(getById(taskId), flowParams);
    }

    @Override
    public Instance termination(Task task, FlowParams flowParams) {
        R r = getAndCheck(task);
        flowParams.variable(MapUtil.mergeAll(r.instance.getVariableMap(), flowParams.getVariable()));
        ListenerUtil.executeListener(new ListenerVariable(r.definition, r.instance, r.nowNode, flowParams.getVariable()
                , task).setFlowParams(flowParams), Listener.LISTENER_START);

        // 判断当前处理人是否有权限处理
        task.setUserList(FlowEngine.userService().listByAssociatedAndTypes(task.getId()));
        checkAuth(task, flowParams);

        // 所有待办转历史
        Node endNode = FlowEngine.nodeService().getOne(FlowEngine.newNode()
                .setDefinitionId(r.instance.getDefinitionId()).setNodeType(NodeType.END.getKey()));

        // 设置流程图元数据
        PathWayData pathWayData = new PathWayData()
                .setInsId(task.getInstanceId())
                .setSkipType(flowParams.getSkipType())
                .setPathWayNodes(Collections.singletonList(r.nowNode))
                .setTargetNodes(Collections.singletonList(endNode));
        r.instance.setDefJson(FlowEngine.chartService().skipMetadata(pathWayData));

        // 流程实例完成
        r.instance.setNodeType(endNode.getNodeType())
                .setNodeCode(endNode.getNodeCode())
                .setNodeName(endNode.getNodeName())
                .setFlowStatus(ObjectUtil.isNotNull(flowParams.getFlowStatus()) ? flowParams.getFlowStatus()
                        : FlowStatus.TERMINATE.getKey());

        // 待办任务转历史
        flowParams.skipType(SkipType.PASS.getKey()).flowStatus(r.instance.getFlowStatus());
        List<HisTask> insHisList = FlowEngine.hisTaskService().setSkipInsHis(task, Collections.singletonList(endNode)
                , flowParams);
        FlowEngine.hisTaskService().saveBatch(insHisList);
        FlowEngine.insService().updateById(r.instance);

        // 删除流程相关办理人
        FlowEngine.userService().deleteByTaskIds(Collections.singletonList(task.getId()));

        // 处理未完成的任务，当流程完成，还存在待办任务未完成，转历史任务，状态完成。
        handUndoneTask(r.instance, flowParams);
        // 最后判断是否存在节点监听器，存在执行节点监听器
        ListenerUtil.executeListener(new ListenerVariable(r.definition, r.instance, r.nowNode, flowParams.getVariable()
                , task), Listener.LISTENER_FINISH);
        return r.instance;
    }

    @Override
    public boolean deleteByInsIds(List<Long> instanceIds) {
        List<Instance> instanceList = FlowEngine.insService().getByIds(instanceIds);
        Definition definition;
        for (Instance instance : instanceList) {
            definition = FlowEngine.defService().getById(instance.getDefinitionId());
            AssertUtil.isFalse(judgeActivityStatus(definition, instance), ExceptionCons.NOT_ACTIVITY);
        }
        return SqlHelper.retBool(getDao().deleteByInsIds(instanceIds));
    }

    @Override
    public boolean transfer(Long taskId, FlowParams flowParams) {
        AssertUtil.isNotNull(taskId, ExceptionCons.NULL_TASK_ID);
        AssertUtil.isNotNull(flowParams.getHandler(), ExceptionCons.HANDLER_NOT_EMPTY);
        AssertUtil.isNotNull(flowParams.getAddHandlers(), ExceptionCons.NUll_TRANSFER_HANDLER);
        List<User> users = FlowEngine.userService().getByProcessedBys(taskId, flowParams.getAddHandlers(), UserType.TRANSFER.getKey());
        AssertUtil.isNotEmpty(users, ExceptionCons.IS_ALREADY_TRANSFER);
        flowParams.cooperateType(CooperateType.TRANSFER.getKey())
                .reductionHandlers(Collections.singletonList(flowParams.getHandler()));

        return updateHandler(taskId, flowParams);
    }

    @Override
    public boolean depute(Long taskId, FlowParams flowParams) {
        AssertUtil.isNotNull(taskId, ExceptionCons.NULL_TASK_ID);
        AssertUtil.isNotNull(flowParams.getHandler(), ExceptionCons.HANDLER_NOT_EMPTY);
        AssertUtil.isNotNull(flowParams.getAddHandlers(), ExceptionCons.NUll_DEPUTE_HANDLER);
        List<User> users = FlowEngine.userService().getByProcessedBys(taskId, flowParams.getAddHandlers(), UserType.DEPUTE.getKey());
        AssertUtil.isNotEmpty(users, ExceptionCons.IS_ALREADY_DEPUTE);
        flowParams.cooperateType(CooperateType.DEPUTE.getKey())
                .reductionHandlers(Collections.singletonList(flowParams.getHandler()));

        return updateHandler(taskId, flowParams);
    }

    @Override
    public boolean addSignature(Long taskId, FlowParams flowParams) {
        AssertUtil.isNotNull(taskId, ExceptionCons.NULL_TASK_ID);
        AssertUtil.isNotNull(flowParams.getHandler(), ExceptionCons.HANDLER_NOT_EMPTY);
        AssertUtil.isNotNull(flowParams.getAddHandlers(), ExceptionCons.NUll_ADD_SIGNATURE_HANDLER);
        List<User> users = FlowEngine.userService().getByProcessedBys(taskId, flowParams.getAddHandlers(), UserType.APPROVAL.getKey());
        AssertUtil.isNotEmpty(users, ExceptionCons.IS_ALREADY_SIGN);
        flowParams.cooperateType(CooperateType.ADD_SIGNATURE.getKey());

        return updateHandler(taskId, flowParams);
    }

    @Override
    public boolean reductionSignature(Long taskId, FlowParams flowParams) {
        AssertUtil.isNotNull(taskId, ExceptionCons.NULL_TASK_ID);
        AssertUtil.isNotNull(flowParams.getHandler(), ExceptionCons.HANDLER_NOT_EMPTY);
        AssertUtil.isNotNull(flowParams.getReductionHandlers(), ExceptionCons.NUll_REDUCTION_SIGNATURE_HANDLER);
        List<User> users = FlowEngine.userService().list(FlowEngine.newUser().setAssociated(taskId)
                .setType(UserType.APPROVAL.getKey()));
        AssertUtil.isTrue(CollUtil.isEmpty(users) || users.size() == 1, ExceptionCons.REDUCTION_SIGN_ONE_ERROR);
        flowParams.cooperateType(CooperateType.REDUCTION_SIGNATURE.getKey());

        return updateHandler(taskId, flowParams);
    }

    @Override
    public boolean updateHandler(Long taskId, FlowParams flowParams) {
        // 获取待办任务
        R r = getAndCheck(taskId);
        flowParams.variable(MapUtil.mergeAll(r.instance.getVariableMap(), flowParams.getVariable()));
        // 执行开始监听器
        ListenerUtil.executeListener(new ListenerVariable(r.definition, r.instance, r.nowNode, null, r.task)
                , Listener.LISTENER_START);

        // 获取给谁的权限
        if (!flowParams.isIgnore()) {
            // 判断当前处理人是否有权限，获取当前办理人的权限
            List<String> permissions = flowParams.getPermissionFlag();
            // 获取任务权限人
            List<String> taskPermissions = FlowEngine.userService().getPermission(taskId
                    , UserType.APPROVAL.getKey(), UserType.TRANSFER.getKey(), UserType.DEPUTE.getKey());
            AssertUtil.isTrue(CollUtil.isNotEmpty(taskPermissions) && (CollUtil.isEmpty(permissions)
                    || CollUtil.notContainsAny(permissions, taskPermissions)), ExceptionCons.NOT_AUTHORITY);
        }
        // 留存历史记录
        flowParams.skipType(SkipType.NONE.getKey());
        List<HisTask> hisTasks = null;
        // 删除对应的操作人
        if (CollUtil.isNotEmpty(flowParams.getReductionHandlers())) {
            for (String reductionHandler : flowParams.getReductionHandlers()) {
                FlowEngine.userService().remove(FlowEngine.newUser().setAssociated(taskId)
                        .setProcessedBy(reductionHandler));
            }
            hisTasks = FlowEngine.hisTaskService().setCooperateHis(r.task, r.nowNode
                    , flowParams, flowParams.getReductionHandlers());
        }

        // 新增权限人
        if (CollUtil.isNotEmpty(flowParams.getAddHandlers())) {
            String type;
            if (CooperateType.TRANSFER.getKey().equals(flowParams.getCooperateType())) {
                type = UserType.TRANSFER.getKey();
            } else if (CooperateType.DEPUTE.getKey().equals(flowParams.getCooperateType())) {
                type = UserType.DEPUTE.getKey();
            } else {
                type = UserType.APPROVAL.getKey();
            }
            FlowEngine.userService().saveBatch(StreamUtils.toList(flowParams.getAddHandlers(), permission ->
                    FlowEngine.userService().structureUser(taskId, permission
                            , type, flowParams.getHandler())));
            hisTasks = FlowEngine.hisTaskService().setCooperateHis(r.task, r.nowNode
                    , flowParams, flowParams.getAddHandlers());
        }
        if (CollUtil.isNotEmpty(hisTasks)) {
            FlowEngine.hisTaskService().saveBatch(hisTasks);
        }
        // 最后判断是否存在节点监听器，存在执行节点监听器
        ListenerUtil.executeListener(new ListenerVariable(r.definition, r.instance, r.nowNode, flowParams.getVariable()
                , r.task), Listener.LISTENER_FINISH);
        return true;
    }

    @Override
    public Instance retrieve(Long instanceId, FlowParams flowParams) {
        Instance instance = FlowEngine.insService().getById(instanceId);
        flowParams.variable(MapUtil.mergeAll(instance.getVariableMap(), flowParams.getVariable()));
        AssertUtil.isNull(instance, ExceptionCons.NOT_FOUNT_INSTANCE);
        // 验证权限是不是当前任务的发起人
        AssertUtil.isFalse(instance.getCreateBy().equals(flowParams.getHandler())
                , ExceptionCons.NOT_DEF_PROMOTER_NOT_RETRIEVE);
        // 如果没传nodeCode，则默认跳转开始节点
        if (StringUtils.isEmpty(flowParams.getNodeCode())) {
            flowParams.nodeCode(FlowEngine.nodeService().getOne(FlowEngine.newNode()
                    .setDefinitionId(instance.getDefinitionId())
                    .setNodeType(NodeType.START.getKey())).getNodeCode());
        }
        // TODO warm 监听器
        Definition definition = FlowEngine.defService().getById(instance.getDefinitionId());
        AssertUtil.isFalse(judgeActivityStatus(definition, instance), ExceptionCons.NOT_ACTIVITY);
        AssertUtil.isTrue(NodeType.isEnd(instance.getNodeType()), ExceptionCons.FLOW_FINISH);

        AssertUtil.isTrue(StringUtils.isNotEmpty(flowParams.getMessage())
                && flowParams.getMessage().length() > 500, ExceptionCons.MSG_OVER_LENGTH);
        // 查询目标节点信息
        Node nextNode = FlowEngine.nodeService().getOne(FlowEngine.newNode()
                .setNodeCode(flowParams.getNodeCode()).setDefinitionId(instance.getDefinitionId()));
        AssertUtil.isNull(nextNode, ExceptionCons.LOST_DEST_NODE);
        // 回退到的节点是否为结束节点，结束节点是不能回退
        AssertUtil.isTrue(NodeType.isEnd(nextNode.getNodeType()), ExceptionCons.NODE_IS_END);

        // 查询任务,如果前一个节点是并行网关，可能任务表有多个任务,增加查询和判断
        List<Task> curTaskList = list(FlowEngine.newTask().setInstanceId(instance.getId()));
        AssertUtil.isEmpty(curTaskList, ExceptionCons.NOT_FOUND_FLOW_TASK);

        // 给回退到的那个节点赋权限-给当前处理人权限
        Task nextTask = addTask(nextNode, instance, definition, flowParams);
        // 流程参数
        flowParams.skipType(SkipType.REJECT.getKey());
        // 删除待办任务，保存历史，删除所有代办任务的权限人
        if (StringUtils.isNotEmpty(flowParams.getFlowStatus())) {
            flowParams.flowStatus(FlowStatus.RETRIEVE.getKey());
        }
        removeByIds(StreamUtils.toList(curTaskList, Task::getId));
        // 删除所有待办任务的权限人,处理人保存
        FlowEngine.userService().deleteByTaskIds(StreamUtils.toList(curTaskList, Task::getId));
        FlowEngine.hisTaskService().saveBatch(StreamUtils.toList(curTaskList,
                task -> FlowEngine.hisTaskService().setSkipHisTask(task, nextNode, flowParams)));

        // 设置任务完成后的实例相关信息
        setInsFinishInfo(instance, CollUtil.toList(nextTask), flowParams);
        FlowEngine.insService().updateById(instance);
        // 保存待办任务
        List<User> users = FlowEngine.userService().taskAddUsers(CollUtil.toList(nextTask));
        FlowEngine.userService().saveBatch(users);
        save(nextTask);
        return instance;
    }

    @Override
    public Task addTask(Node node, Instance instance, Definition definition, FlowParams flowParams) {
        Task addTask = FlowEngine.newTask();
        FlowEngine.dataFillHandler().idFill(addTask);
        addTask.setDefinitionId(instance.getDefinitionId())
                .setInstanceId(instance.getId())
                .setNodeCode(node.getNodeCode())
                .setNodeName(node.getNodeName())
                .setNodeType(node.getNodeType())
                .setCreateTime(new Date())
                .setPermissionList(StringUtils.str2List(node.getPermissionFlag(), FlowCons.splitAt));

        if (StringUtils.isNotEmpty(node.getFormCustom()) && StringUtils.isNotEmpty(node.getFormPath())) {
            // 节点有自定义表单则使用
            addTask.setFormCustom(node.getFormCustom()).setFormPath(node.getFormPath());
        } else {
            addTask.setFormCustom(definition.getFormCustom()).setFormPath(definition.getFormPath());
        }

        return addTask;
    }

    @Override
    public String setFlowStatus(Integer nodeType, String skipType) {
        // 根据审批动作确定流程状态
        if (NodeType.isStart(nodeType)) {
            return FlowStatus.TOBESUBMIT.getKey();
        } else if (NodeType.isEnd(nodeType)) {
            return FlowStatus.FINISHED.getKey();
        } else if (SkipType.isReject(skipType)) {
            return FlowStatus.REJECT.getKey();
        } else {
            return FlowStatus.APPROVAL.getKey();
        }
    }

    @Override
    public Task getNextTask(List<Task> tasks) {
        if (tasks.size() == 1) {
            return tasks.get(0);
        }
        for (Task task : tasks) {
            if (NodeType.isEnd(task.getNodeType())) {
                return task;
            }
        }
        return tasks.stream().max(Comparator.comparingLong(Task::getId)).orElse(null);
    }

    @Override
    public void mergeVariable(Instance instance, Map<String, Object> variable) {
        if (MapUtil.isNotEmpty(variable)) {
            String variableStr = instance.getVariable();
            Map<String, Object> deserialize = FlowEngine.jsonConvert.strToMap(variableStr);
            deserialize.putAll(variable);
            instance.setVariable(FlowEngine.jsonConvert.objToStr(deserialize));
        }
    }

    @Override
    public boolean checkAuth(Long instanceId, Long taskId, String... permissionList) {
//        AssertUtil.isTrue(instanceId == null && taskId == null, ExceptionCons.TASKID_INSTANCEID_NULL);
//        if (instanceId != null) {
//            Instance instance = FlowEngine.insService().getById(instanceId);
//            AssertUtil.isNull(instance, ExceptionCons.NOT_FOUNT_INSTANCE);
//        }
//        if (permissionList == null) {
//            String[] array = FlowEngine.permissionHandler().permissions();
//            AssertUtil.isTrue(array.length < 1, ExceptionCons.NOT_AUTHORITY);
//            permissionList = array;
//        }
//        Task flowTask = FlowEngine.newTask();
//        flowTask.setInstanceId(instanceId);
//        flowTask.setId(taskId);
//        List<Task> taskList = FlowEngine.taskService().list(flowTask);
//        AssertUtil.isEmpty(taskList, ExceptionCons.NOT_FOUND_FLOW_TASK);
//        return Arrays.stream(permissionList)
//                .anyMatch(permission -> taskList.stream()
//                        .anyMatch(task -> {
//                            User flowUser = FlowEngine.newUser();
//                            flowUser.setAssociated(task.getId());
//                            flowUser.setProcessedBy(permission);
//                            return FlowEngine.userService().selectCount(flowUser) > 0;
//                        })
//                );
        return true;
    }

    private R getAndCheck(Long taskId) {
        AssertUtil.isNotNull(taskId, ExceptionCons.NULL_TASK_ID);
        return getAndCheck(getById(taskId));
    }

    private R getAndCheck(Task task) {
        AssertUtil.isNull(task, ExceptionCons.NOT_FOUNT_TASK);
        Instance instance = FlowEngine.insService().getById(task.getInstanceId());
        AssertUtil.isNull(instance, ExceptionCons.NOT_FOUNT_INSTANCE);
        Definition definition = FlowEngine.defService().getById(instance.getDefinitionId());
        AssertUtil.isFalse(judgeActivityStatus(definition, instance), ExceptionCons.NOT_ACTIVITY);
        AssertUtil.isTrue(NodeType.isEnd(instance.getNodeType()), ExceptionCons.FLOW_FINISH);
        Node nowNode = FlowEngine.nodeService().getOne(FlowEngine.newNode().setNodeCode(task.getNodeCode())
                .setDefinitionId(task.getDefinitionId()));
        AssertUtil.isNull(nowNode, ExceptionCons.LOST_CUR_NODE);
        return new R(instance, definition, nowNode, task);
    }

    private static class R {
        public final Instance instance;
        public final Definition definition;
        public final Node nowNode;
        public final Task task;

        public R(Instance instance, Definition definition, Node nowNode, Task task) {
            this.instance = instance;
            this.definition = definition;
            this.nowNode = nowNode;
            this.task = task;
        }
    }

    private boolean handleDepute(Task task, FlowParams flowParams) {
        // 获取受托人
        User entrustedUser = FlowEngine.userService().getOne(FlowEngine.newUser().setAssociated(task.getId())
                .setProcessedBy(flowParams.getHandler()).setType(UserType.DEPUTE.getKey()));
        if (ObjectUtil.isNull(entrustedUser)) {
            return false;
        }

        // 记录受托人处理任务记录
        HisTask hisTask = FlowEngine.hisTaskService().setDeputeHisTask(task, flowParams, entrustedUser);
        FlowEngine.hisTaskService().save(hisTask);
        FlowEngine.userService().removeById(entrustedUser.getId());

        // 查询委托人，如果在flow_user不存在，则给委托人新增待办记录
        User deputeUser = FlowEngine.userService().getOne(FlowEngine.newUser().setAssociated(task.getId())
                .setProcessedBy(entrustedUser.getCreateBy()).setType(UserType.APPROVAL.getKey()));
        if (ObjectUtil.isNull(deputeUser)) {
            User newUser = FlowEngine.userService().structureUser(entrustedUser.getAssociated()
                    , entrustedUser.getCreateBy()
                    , UserType.APPROVAL.getKey(), entrustedUser.getProcessedBy());
            FlowEngine.userService().save(newUser);
        }

        return true;
    }

    /**
     * 会签，票签，协作处理，返回true；或签或者会签、票签结束返回false
     *
     * @param nowNode    当前节点
     * @param task       任务
     * @param flowParams 流程参数
     * @return boolean
     */
    private boolean cooperate(Node nowNode, Task task, FlowParams flowParams) {
        BigDecimal nodeRatio = nowNode.getNodeRatio();
        // 或签，直接返回
        if (CooperateType.isOrSign(nodeRatio)) {
            return false;
        }

        // 办理人和转办人列表
        List<User> todoList = FlowEngine.userService().listByAssociatedAndTypes(task.getId()
                , UserType.APPROVAL.getKey(), UserType.TRANSFER.getKey(), UserType.DEPUTE.getKey());

        // 判断办理人是否有办理权限
        AssertUtil.isEmpty(flowParams.getHandler(), ExceptionCons.SIGN_NULL_HANDLER);
        User todoUser = CollUtil.getOne(StreamUtils.filter(todoList, u -> Objects.equals(u.getProcessedBy(), flowParams.getHandler())));
        AssertUtil.isNull(todoUser, ExceptionCons.NOT_AUTHORITY);

        // 当只剩一位待办用户时，由当前用户决定走向
        if (todoList.size() == 1) {
            return false;
        }

        // 除当前办理人外剩余办理人列表
        List<User> restList = StreamUtils.filter(todoList, u -> !Objects.equals(u.getProcessedBy(), flowParams.getHandler()));

        // 会签并且当前人驳回直接返回
        if (CooperateType.isCountersign(nodeRatio) && SkipType.isReject(flowParams.getSkipType())) {
            FlowEngine.userService().removeByIds(StreamUtils.toList(restList, User::getId));
            return false;
        }

        // 查询会签票签已办列表

        List<HisTask> doneList = FlowEngine.hisTaskService().listByTaskIdAndCooperateTypes(task.getId()
                , CooperateType.isCountersign(nodeRatio) ? CooperateType.COUNTERSIGN.getKey() : CooperateType.VOTE.getKey());
        doneList = CollUtil.emptyDefault(doneList, Collections.emptyList());

        // TODO 这里处理 cooperation handler 获取下面的 passRatio rejectRatio all 值，能获取使用 handler的值，不能获取使用以下全自动计算代码

        // 所有人
        BigDecimal all = BigDecimal.ZERO.add(BigDecimal.valueOf(todoList.size())).add(BigDecimal.valueOf(doneList.size()));

        List<HisTask> donePassList = StreamUtils.filter(doneList
                , hisTask -> Objects.equals(hisTask.getSkipType(), SkipType.PASS.getKey()));

        List<HisTask> doneRejectList = StreamUtils.filter(doneList
                , hisTask -> Objects.equals(hisTask.getSkipType(), SkipType.REJECT.getKey()));

        boolean isPass = SkipType.isPass(flowParams.getSkipType());

        // 计算通过率
        BigDecimal passRatio = (isPass ? BigDecimal.ONE : BigDecimal.ZERO)
                .add(BigDecimal.valueOf(donePassList.size()))
                .divide(all, 4, RoundingMode.HALF_UP).multiply(CooperateType.ONE_HUNDRED);

        // 计算驳回率
        BigDecimal rejectRatio = (isPass ? BigDecimal.ZERO : BigDecimal.ONE)
                .add(BigDecimal.valueOf(doneRejectList.size()))
                .divide(all, 4, RoundingMode.HALF_UP).multiply(CooperateType.ONE_HUNDRED);

        if (!isPass && rejectRatio.compareTo(CooperateType.ONE_HUNDRED.subtract(nodeRatio)) > 0) {
            // 驳回，并且当前是驳回
            FlowEngine.userService().removeByIds(StreamUtils.toList(restList, User::getId));
            return false;
        }

        if (passRatio.compareTo(nodeRatio) >= 0) {
            // 大于等于 nodeRatio 设置值结束任务
            FlowEngine.userService().removeByIds(StreamUtils.toList(restList, User::getId));
            return false;
        }

        // 添加历史任务
        HisTask hisTask = FlowEngine.hisTaskService().setSignHisTask(task, flowParams, nodeRatio, isPass);
        FlowEngine.hisTaskService().save(hisTask);

        // 删掉待办用户
        FlowEngine.userService().removeById(todoUser.getId());
        return true;
    }

    /**
     * 构建增待办任务
     *
     * @param task       待办任务
     * @param instance   实例
     */
    private void filterUnFinishNode(Task task, Instance instance, List<Node> nextNodes) {
        nextNodes.removeIf(node -> {
            // 下个节点非并行网关节点，可以直接生成下一个待办任务
            if (!NodeType.isGateWayParallel(node.getNodeType())) {
                return false;
            } else {
                // 下个节点是并行网关节点，判断前置节点是否都完成
                return !gateWayParallelIsFinish(task, instance, node.getNodeCode());
            }
        });
    }

    /**
     * 构建增待办任务
     *
     * @param flowParams 流程参数
     * @param task       待办任务
     * @param instance   实例
     * @return List<Task>
     */
    private List<Task> buildAddTasks(FlowParams flowParams, Task task, Instance instance
            , List<Node> nextNodes, Definition definition) {
        List<Task> addTasks = new ArrayList<>();
        for (Node node : nextNodes) {
            boolean buildFlag = false;
            // 下个节点非并行网关节点，可以直接生成下一个待办任务
            if (!NodeType.isGateWayParallel(node.getNodeType())) {
                buildFlag = true;
            } else {
                // 下个节点是并行网关节点，判断前置节点是否都完成
                if (gateWayParallelIsFinish(task, instance, node.getNodeCode())) {
                    buildFlag = true;
                }
            }
            if (buildFlag) {
                addTasks.add(addTask(node, instance, definition, flowParams));
            }
        }
        return addTasks;
    }

    /**
     * 判断并行网关节点前置任务是否都完成
     * 多条线路汇聚到并行网关，必须所有任务都完成，才能继续。 根据并行网关节点，查询前面的节点是否都完成，
     * 判断规则，获取网关所有前置节点，并且查询是否有历史任务记录，前前置节点完成时间是否早于前置节点
     *
     * @param task         待办任务
     * @param instance     实例
     * @param nextNodeCode 下个节点编码
     * @return boolean
     */
    private boolean gateWayParallelIsFinish(Task task, Instance instance, String nextNodeCode) {
        List<Skip> allSkips = FlowEngine.skipService().list(FlowEngine.newSkip()
                .setDefinitionId(instance.getDefinitionId()));
        Map<String, List<Skip>> skipNextMap = StreamUtils.groupByKeyFilter(skip -> !task.getNodeCode()
                        .equals(skip.getNowNodeCode()) || !nextNodeCode.equals(skip.getNextNodeCode())
                , allSkips, Skip::getNextNodeCode);
        List<Skip> oneLastSkips = skipNextMap.get(nextNodeCode);
        // 说明没有其他前置节点，是并行网关的起始位置，可以完成往下执行
        if (CollUtil.isEmpty(oneLastSkips)) {
            return true;
        }
        List<HisTask> hisTaskList = FlowEngine.hisTaskService().getNoReject(instance.getId());
        int wayParallelIsFinish = 0;
        if (CollUtil.isNotEmpty(oneLastSkips)) {
            for (Skip oneLastSkip : oneLastSkips) {
                HisTask oneLastHisTask = FlowEngine.hisTaskService()
                        .getNoReject(oneLastSkip.getNowNodeCode(), null, hisTaskList);
                // 查询前置节点是否有完成记录
                if (ObjectUtil.isNull(oneLastHisTask)) {
                    return false;
                }
                List<Skip> twoLastSkips = skipNextMap.get(oneLastSkip.getNowNodeCode());
                for (Skip twoLastSkip : twoLastSkips) {
                    if (NodeType.isStart(twoLastSkip.getNowNodeType())) {
                        wayParallelIsFinish++;
                    } else if (NodeType.isGateWay(twoLastSkip.getNowNodeType())) {
                        // 如果前前置节点是网关，那网关前任意一个任务完成就算完成
                        List<Skip> threeLastSkips = skipNextMap.get(twoLastSkip.getNowNodeCode());
                        for (Skip threeLastSkip : threeLastSkips) {
                            HisTask threeLastHisTask = FlowEngine.hisTaskService()
                                    .getNoReject(threeLastSkip.getNowNodeCode(), null, hisTaskList);
                            if (ObjectUtil.isNotNull(threeLastHisTask) && (threeLastHisTask.getUpdateTime()
                                    .before(oneLastHisTask.getUpdateTime()) || threeLastHisTask.getUpdateTime()
                                    .equals(oneLastHisTask.getUpdateTime()))) {
                                wayParallelIsFinish++;
                            }
                        }
                    } else {
                        HisTask twoLastHisTask = FlowEngine.hisTaskService()
                                .getNoReject(twoLastSkip.getNowNodeCode(), null, hisTaskList);
                        // 前前置节点完成时间是否早于前置节点，如果是串行网关，那前前置节点必须只有一个完成，如果是并行网关都要完成
                        if (ObjectUtil.isNotNull(twoLastHisTask) && (twoLastHisTask.getUpdateTime()
                                .before(oneLastHisTask.getUpdateTime()) || twoLastHisTask.getUpdateTime()
                                .equals(oneLastHisTask.getUpdateTime()))) {
                            wayParallelIsFinish++;
                        }
                    }
                }
            }
        }
        return wayParallelIsFinish == oneLastSkips.size();
    }

    /**
     * 判断当前处理人是否有权限处理
     *
     * @param task                   当前任务（任务id）
     * @param flowParams:包含流程相关参数的对象
     */
    private void checkAuth(Task task, FlowParams flowParams) {
        if (flowParams.isIgnore()) {
            return;
        }
        // 查询审批人和转办人
        List<String> permissions = StreamUtils.toList(task.getUserList(), User::getProcessedBy);
        // 当前办理人拥有的权限和设计时候填的权限集合是否有交集，有说明有权限办理
        AssertUtil.isTrue(CollUtil.isNotEmpty(permissions) && (CollUtil.isEmpty(flowParams.getPermissionFlag())
                || CollUtil.notContainsAny(flowParams.getPermissionFlag(), permissions)), ExceptionCons.NULL_ROLE_NODE);
    }

    // 设置任务完成后的实例相关信息
    private void setInsFinishInfo(Instance instance, List<Task> addTasks, FlowParams flowParams) {
        instance.setUpdateTime(new Date());
        // 合并流程变量到实例对象
        mergeVariable(instance, flowParams.getVariable());
        if (CollUtil.isNotEmpty(addTasks)) {
            addTasks.removeIf(addTask -> {
                if (NodeType.isEnd(addTask.getNodeType())) {
                    instance.setNodeType(addTask.getNodeType())
                            .setNodeCode(addTask.getNodeCode())
                            .setNodeName(addTask.getNodeName())
                            .setFlowStatus(ObjectUtil.isNotNull(flowParams.getFlowStatus())
                                    ? flowParams.getFlowStatus() : FlowStatus.FINISHED.getKey());
                    return true;
                }
                return false;
            });
        }
        if (CollUtil.isNotEmpty(addTasks) && !NodeType.isEnd(instance.getNodeType())) {
            Task nextTask = getNextTask(addTasks);
            instance.setNodeType(nextTask.getNodeType())
                    .setNodeCode(nextTask.getNodeCode())
                    .setNodeName(nextTask.getNodeName())
                    .setFlowStatus(ObjectUtil.isNotNull(flowParams.getFlowStatus()) ? flowParams.getFlowStatus()
                            : setFlowStatus(nextTask.getNodeType(), flowParams.getSkipType()));
        }
    }

    /**
     * 一票否决（谨慎使用），如果退回，退回指向节点后还存在其他正在执行的待办任务，转历史任务，状态都为退回,重走流程。
     *
     * @param task         当前任务
     * @param flowParams   包含流程相关参数的对象
     * @param nextNodeCode 下一个节点编码
     */
    private void oneVoteVeto(Task task, FlowParams flowParams, String nextNodeCode) {
        // 一票否决（谨慎使用），如果退回，退回指向节点后还存在其他正在执行的待办任务，转历史任务，状态失效,重走流程。
        if (SkipType.isReject(flowParams.getSkipType())) {
            List<Task> tasks = list(FlowEngine.newTask().setInstanceId(task.getInstanceId()));
            List<Skip> allSkips = FlowEngine.skipService().list(FlowEngine.newSkip()
                    .setDefinitionId(task.getDefinitionId()));
            // 排除执行当前节点的流程跳转
            Map<String, List<Skip>> skipMap = StreamUtils.groupByKeyFilter(skip ->
                    !task.getNodeCode().equals(skip.getNextNodeCode()), allSkips, Skip::getNextNodeCode);
            // 属于退回指向节点的后置未完成的任务
            List<Task> noDoneTasks = new ArrayList<>();
            for (Task flowTask : tasks) {
                if (!task.getNodeCode().equals(flowTask.getNodeCode())) {
                    List<Skip> lastSkips = skipMap.get(flowTask.getNodeCode());
                    if (judgeReject(nextNodeCode, lastSkips, skipMap)) {
                        noDoneTasks.add(flowTask);
                    }
                }
            }
            if (CollUtil.isNotEmpty(noDoneTasks)) {
                convertHisTask(noDoneTasks, flowParams, FlowStatus.INVALID.getKey());
            }
        }
    }


    /**
     * 判断是否属于退回指向节点的后置未完成的任务
     *
     * @param nextNodeCode 下一个节点编码
     * @param lastSkips    上一个跳转集合
     * @param skipMap      跳转map
     * @return boolean
     */
    private boolean judgeReject(String nextNodeCode, List<Skip> lastSkips
            , Map<String, List<Skip>> skipMap) {
        if (CollUtil.isNotEmpty(lastSkips)) {
            for (Skip lastSkip : lastSkips) {
                if (nextNodeCode.equals(lastSkip.getNowNodeCode())) {
                    return true;
                }
                List<Skip> lastLastSkips = skipMap.get(lastSkip.getNowNodeCode());
                return judgeReject(nextNodeCode, lastLastSkips, skipMap);
            }
        }
        return false;
    }

    /**
     * 处理未完成的任务，当流程完成，还存在待办任务未完成，转历史任务，状态完成。
     *
     * @param instance 流程实例
     */
    private void handUndoneTask(Instance instance, FlowParams flowParams) {
        if (NodeType.isEnd(instance.getNodeType())) {
            List<Task> taskList = list(FlowEngine.newTask().setInstanceId(instance.getId()));
            if (CollUtil.isNotEmpty(taskList)) {
                convertHisTask(taskList, flowParams, FlowStatus.AUTO_PASS.getKey());
            }
        }
    }

    /**
     * 待办任务转历史任务。
     */
    private void convertHisTask(List<Task> taskList, FlowParams flowParams, String flowStatus) {
        List<HisTask> insHisList = new ArrayList<>();
        for (Task task : taskList) {
            List<User> userList = FlowEngine.userService().listByAssociatedAndTypes(task.getId());
            List<HisTask> hisTasks = FlowEngine.hisTaskService().autoHisTask(flowParams, flowStatus, task, userList, CooperateType.APPROVAL.getKey());
            // 设置每个HisTask的ext字段
            hisTasks.forEach(hisTask -> hisTask.setExt(flowParams.getHisTaskExt()));
            insHisList.addAll(hisTasks);
        }
        removeByIds(StreamUtils.toList(taskList, Task::getId));
        FlowEngine.hisTaskService().saveBatch(insHisList);
        // 删除所有待办任务的权限人
        FlowEngine.userService().deleteByTaskIds(StreamUtils.toList(taskList, Task::getId));
    }

    /**
     * 更新流程信息
     *
     * @param task       当前任务
     * @param instance   流程实例
     * @param addTasks   新增待办任务
     * @param flowParams 包含流程相关参数的对象
     * @param nextNodes  下一个节点集合
     */
    private void updateFlowInfo(Task task, Instance instance, List<Task> addTasks, FlowParams flowParams
            , List<Node> nextNodes) {
        // 设置流程历史任务信息
        List<HisTask> insHisList = FlowEngine.hisTaskService().setSkipInsHis(task, nextNodes, flowParams);
        FlowEngine.hisTaskService().saveBatch(insHisList);
        // 待办任务设置处理人
        List<User> users = FlowEngine.userService().setSkipUser(addTasks, task.getId());
        removeById(task.getId());

        // 设置任务完成后的实例相关信息
        setInsFinishInfo(instance, addTasks, flowParams);
        if (CollUtil.isNotEmpty(addTasks)) {
            saveBatch(addTasks);
        }
        FlowEngine.insService().updateById(instance);
        // 保存下一个待办任务的权限人和历史任务的处理人
        FlowEngine.userService().saveBatch(users);
    }

    private boolean judgeActivityStatus(Definition definition, Instance instance) {
        return Objects.equals(definition.getActivityStatus(), ActivityStatus.ACTIVITY.getKey())
                && Objects.equals(instance.getActivityStatus(), ActivityStatus.ACTIVITY.getKey());
    }


    @Override
    public FlowDto load(Long taskId, FlowParams flowParams) {
        R r = getAndCheck(taskId);

        ListenerVariable listenerVariable = new ListenerVariable(r.definition, r.instance, r.nowNode
                , flowParams.getVariable(), r.task);

        FlowDto flowDto = new FlowDto();
        if (FlowCons.FORM_CUSTOM_Y.equals(r.nowNode.getFormCustom())) {
            ListenerUtil.execute(listenerVariable, Listener.LISTENER_FORM_LOAD, r.nowNode.getListenerPath()
                    , r.nowNode.getListenerType());
            Form form = FlowEngine.formService().getById(Long.valueOf(r.task.getFormPath()));
            flowDto.setForm(form);
        } else if(StringUtils.isEmpty(r.nowNode.getFormCustom()) && FlowCons.FORM_CUSTOM_Y.equals(r.definition.getFormCustom())) {
            ListenerUtil.execute(listenerVariable, Listener.LISTENER_FORM_LOAD, r.definition.getListenerPath()
                    , r.definition.getListenerType());
            Form form = FlowEngine.formService().getById(Long.valueOf(r.definition.getFormPath()));
            flowDto.setForm(form);
        }
        flowDto.setData(r.instance.getVariableMap().get(FlowCons.FORM_DATA));

        return flowDto;
    }

    @Override
    public FlowDto hisLoad(Long hisTaskId, FlowParams flowParams) {
        HisTask hisTask = FlowEngine.hisTaskService().getById(hisTaskId);
        AssertUtil.isNull(hisTask, ExceptionCons.NOT_FOUND_FLOW_TASK);

        Definition definition = FlowEngine.defService().getById(hisTask.getDefinitionId());
        AssertUtil.isNull(definition, ExceptionCons.NOT_FOUNT_DEF);

        Node nowNode = CollUtil.getOne(FlowEngine.nodeService()
                .getByNodeCodes(Collections.singletonList(hisTask.getNodeCode()), hisTask.getDefinitionId()));
        AssertUtil.isNull(nowNode, ExceptionCons.LOST_CUR_NODE);

        FlowDto flowDto = new FlowDto();
        if (FlowCons.FORM_CUSTOM_Y.equals(nowNode.getFormCustom())) {
            Form form = FlowEngine.formService().getById(Long.valueOf(hisTask.getFormPath()));
            flowDto.setForm(form);
        } else if(StringUtils.isEmpty(nowNode.getFormCustom()) && FlowCons.FORM_CUSTOM_Y.equals(definition.getFormCustom())) {
            Form form = FlowEngine.formService().getById(Long.valueOf(definition.getFormPath()));
            flowDto.setForm(form);
        }
        flowDto.setData(hisTask.getVariableMap().get(FlowCons.FORM_DATA));

        return flowDto;
    }
}
