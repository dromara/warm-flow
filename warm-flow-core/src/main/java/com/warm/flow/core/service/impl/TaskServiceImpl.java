package com.warm.flow.core.service.impl;

import com.warm.flow.core.FlowFactory;
import com.warm.flow.core.constant.ExceptionCons;
import com.warm.flow.core.constant.FlowCons;
import com.warm.flow.core.dao.FlowTaskDao;
import com.warm.flow.core.dto.FlowParams;
import com.warm.flow.core.entity.*;
import com.warm.flow.core.enums.*;
import com.warm.flow.core.listener.Listener;
import com.warm.flow.core.listener.ListenerVariable;
import com.warm.flow.core.orm.service.impl.WarmServiceImpl;
import com.warm.flow.core.service.TaskService;
import com.warm.flow.core.utils.*;
import org.noear.snack.ONode;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 待办任务Service业务层处理
 *
 * @author warm
 * @date 2023-03-29
 */
public class TaskServiceImpl extends WarmServiceImpl<FlowTaskDao<Task>, Task> implements TaskService {

    @Override
    public TaskService setDao(FlowTaskDao<Task> warmDao) {
        this.warmDao = warmDao;
        return this;
    }

    @Override
    public Instance skip(Long taskId, FlowParams flowParams) {
        // 如果是受托人在处理任务，需要处理一条委派记录，并且更新委托人，回到计划审批人,然后直接返回流程实例
        if(FlowFactory.userService().haveDepute(taskId, flowParams.getCreateBy())){
            return handleDepute(taskId, flowParams);
        }
        AssertUtil.isTrue(StringUtils.isNotEmpty(flowParams.getMessage())
                && flowParams.getMessage().length() > 500, ExceptionCons.MSG_OVER_LENGTH);
        // 获取待办任务
        Task task = getById(taskId);
        AssertUtil.isTrue(ObjectUtil.isNull(task), ExceptionCons.NOT_FOUNT_TASK);
        return skip(flowParams, task);
    }

    @Override
    public Instance skip(FlowParams flowParams, Task task) {
        // 获取当前流程
        Instance instance = FlowFactory.insService().getById(task.getInstanceId());
        AssertUtil.isTrue(ObjectUtil.isNull(instance), ExceptionCons.NOT_FOUNT_INSTANCE);
        AssertUtil.isTrue(FlowStatus.isFinished(instance.getFlowStatus()), ExceptionCons.FLOW_FINISH);

        // TODO min 后续考虑并发问题，待办任务和实例表不同步，可给代办任务id加锁，抽取所接口，方便后续兼容分布式锁
        // 非第一个记得跳转类型必传
        if (!NodeType.isStart(task.getNodeType())) {
            AssertUtil.isFalse(StringUtils.isNotEmpty(flowParams.getSkipType()), ExceptionCons.NULL_CONDITIONVALUE);
        }

        Node NowNode = CollUtil.getOne(FlowFactory.nodeService()
                .getByNodeCodes(Collections.singletonList(task.getNodeCode()), task.getDefinitionId()));
        AssertUtil.isTrue(ObjectUtil.isNull(NowNode), ExceptionCons.LOST_CUR_NODE);

        //执行开始节点 开始监听器
        ListenerUtil.executeListener(new ListenerVariable(instance, NowNode, flowParams.getVariable(), task)
                , Listener.LISTENER_START);

        //判断结点是否有权限监听器,有执行权限监听器NowNode.setPermissionFlag,无走数据库的权限标识符
        ListenerUtil.executeGetNodePermission(new ListenerVariable(instance, NowNode, flowParams.getVariable(), task));

        // 判断当前处理人是否有权限处理
        checkAuth(NowNode, task, flowParams.getPermissionFlag());

        // 获取关联的节点，判断当前处理人是否有权限处理
        Node nextNode = getNextNode(NowNode, task, flowParams);

        // 如果是网关节点，则重新获取后续节点
        List<Node> nextNodes = getNextByCheckGateWay(flowParams, nextNode);

        //判断下一结点是否有权限监听器,有执行权限监听器nextNode.setPermissionFlag,无走数据库的权限标识符
        ListenerUtil.executeGetNodePermission(new ListenerVariable(instance, flowParams.getVariable(), task)
                , StreamUtils.toArray(nextNodes, Node[]::new));

        // 构建增代办任务和设置结束任务历史记录
        List<Task> addTasks = buildAddTasks(flowParams, task, instance, nextNodes, nextNode);

        // 设置流程历史任务信息
        List<HisTask> insHisList = FlowFactory.hisTaskService().setSkipInsHis(task, nextNodes, flowParams);

        // 设置结束节点相关信息
        setEndInfo(instance, addTasks, insHisList);

        // 设置流程实例信息
        setSkipInstance(instance, addTasks, flowParams);

        // 一票否决（谨慎使用），如果退回，退回指向节点后还存在其他正在执行的代办任务，转历史任务，状态都为失效,重走流程。
        oneVoteVeto(task, flowParams.getSkipType(), nextNode.getNodeCode());

        // 历史任务 和 代办任务设置处理人
        List<User> users = FlowFactory.userService().setSkipUser(insHisList, addTasks, flowParams, task.getId());

        // 更新流程信息
        updateFlowInfo(task, instance, insHisList, addTasks, users);

        // 处理未完成的任务，当流程完成，还存在代办任务未完成，转历史任务，状态完成。
        handUndoneTask(instance, null);

        // 最后判断是否存在监听器，存在执行监听器
        ListenerUtil.executeListener(new ListenerVariable(instance, flowParams.getVariable(), task, addTasks)
                , NowNode, nextNodes);
        return instance;
    }
    @Override
    public Instance handleDepute(Long taskId, FlowParams flowParams) {
        // 获取待办任务
        Task task = getById(taskId);
        // 获取委托给别人的人
        List<User> userList = FlowFactory.userService().list(FlowFactory.newUser().setAssociated(taskId)
                .setType(UserType.DEPUTE.getKey()));
        User deputeUser = userList.get(0);
        // 记录被人别人委托的人处理任务记录
        HisTask insHis = FlowFactory.newHisTask()
                .setInstanceId(task.getInstanceId())
                .setNodeCode(task.getNodeCode())
                .setNodeName(task.getNodeName())
                .setNodeType(task.getNodeType())
                .setTenantId(task.getTenantId())
                .setDefinitionId(task.getDefinitionId())
                .setTargetNodeCode(task.getNodeCode())
                .setTargetNodeName(task.getNodeName())
                .setFlowStatus(FlowStatus.PASS.getKey())
                .setMessage(flowParams.getMessage())
                .setCreateTime(new Date())
                .setRecord(" deputed " + flowParams.getCreateBy() + " handle task, depute " + deputeUser.getCreateBy());
        FlowFactory.dataFillHandler().idFill(insHis);
        FlowFactory.hisTaskService().saveBatch(CollUtil.toList(insHis));
        // 更新当前任务的计划审批人
        String processedBy = deputeUser.getProcessedBy();
        deputeUser.setProcessedBy(deputeUser.getCreateBy()).setType(UserType.APPROVER.getKey())
                .setCreateBy(StringUtils.emptyDefault(flowParams.getCreateBy(), processedBy));
        FlowFactory.userService().updateById(deputeUser);
        return FlowFactory.insService().getById(task.getInstanceId());
    }
    @Override
    public Instance termination(Long taskId, FlowParams flowParams) {
        // 获取待办任务
        Task task = getById(taskId);
        AssertUtil.isTrue(ObjectUtil.isNull(task), ExceptionCons.NOT_FOUNT_TASK);
        // 获取当前流程
        Instance instance = FlowFactory.insService().getById(task.getInstanceId());
        AssertUtil.isTrue(ObjectUtil.isNull(instance), ExceptionCons.NOT_FOUNT_INSTANCE);
        return termination(instance, task, flowParams);
    }

    @Override
    public Instance termination(Instance instance, Task task, FlowParams flowParams) {
        // 所有代办转历史
        List<Node> nodeList = FlowFactory.nodeService().list(FlowFactory.newNode()
                .setDefinitionId(instance.getDefinitionId()).setNodeType(NodeType.END.getKey()));
        Node endNode = nodeList.get(0);
        // 代办任务转历史
        List<HisTask> insHisList = FlowFactory.hisTaskService().setSkipInsHis(task, nodeList, flowParams);
        // 流程定义的结束节点转历史
        HisTask hisTask = FlowFactory.newHisTask()
                .setInstanceId(task.getInstanceId())
                .setNodeCode(endNode.getNodeCode())
                .setNodeName(endNode.getNodeName())
                .setNodeType(endNode.getNodeType())
                .setTenantId(task.getTenantId())
                .setDefinitionId(task.getDefinitionId())
                .setFlowStatus(FlowStatus.FINISHED.getKey())
                .setCreateTime(new Date());
        insHisList.add(hisTask);
        FlowFactory.dataFillHandler().idFill(hisTask);
        FlowFactory.hisTaskService().saveBatch(insHisList);
        // 以上所有历史保存处理人
        FlowFactory.userService().saveBatch(FlowFactory.userService().setUser(insHisList, null, flowParams));
        // 流程实例完成
        instance.setNodeType(endNode.getNodeType());
        instance.setNodeCode(endNode.getNodeCode());
        instance.setNodeName(endNode.getNodeName());
        instance.setFlowStatus(FlowStatus.FINISHED.getKey());
        FlowFactory.insService().updateById(instance);

        // 删除流程相关办理人
        FlowFactory.userService().delUser(Collections.singletonList(task.getId()));

        // 处理未完成的任务，当流程完成，还存在代办任务未完成，转历史任务，状态完成。
        handUndoneTask(instance, task.getId());
        return instance;
    }

    @Override
    public boolean deleteByInsIds(List<Long> instanceIds) {
        return SqlHelper.retBool(getDao().deleteByInsIds(instanceIds));
    }

    @Override
    public Node getNextNode(Node NowNode, Task task, FlowParams flowParams) {
        AssertUtil.isNull(task.getDefinitionId(), ExceptionCons.NOT_DEFINITION_ID);
        AssertUtil.isBlank(task.getNodeCode(), ExceptionCons.LOST_NODE_CODE);
        // 如果指定了跳转节点，则判断权限，直接获取节点
        if (StringUtils.isNotEmpty(flowParams.getNodeCode())) {
            return getAnySkipNode(task, flowParams);
        }
        List<Skip> skips = FlowFactory.skipService().list(FlowFactory.newSkip()
                .setDefinitionId(task.getDefinitionId()).setNowNodeCode(task.getNodeCode()));
        List<Skip> nextSkips = getSkipByCheck(task, skips, flowParams);
        AssertUtil.isTrue(CollUtil.isEmpty(nextSkips), ExceptionCons.NULL_SKIP_TYPE);
        List<Node> nodes = FlowFactory.nodeService()
                .getByNodeCodes(Collections.singletonList(nextSkips.get(0).getNextNodeCode()), task.getDefinitionId());
        AssertUtil.isTrue(CollUtil.isEmpty(nodes), ExceptionCons.NOT_NODE_DATA);
        AssertUtil.isTrue(nodes.size() > 1, "[" + nextSkips.get(0).getNextNodeCode() + "]" + ExceptionCons.SAME_NODE_CODE);
        AssertUtil.isTrue(NodeType.isStart(nodes.get(0).getNodeType()), ExceptionCons.FRIST_FORBID_BACK);
        return nodes.get(0);

    }

    @Override
    public List<Node> getNextByCheckGateWay(FlowParams flowParams, Node nextNode) {
        List<Node> nextNodes = new ArrayList<>();
        if (NodeType.isGateWay(nextNode.getNodeType())) {
            List<Skip> skipsGateway = FlowFactory.skipService().list(FlowFactory.newSkip()
                    .setDefinitionId(nextNode.getDefinitionId()).setNowNodeCode(nextNode.getNodeCode()));
            if (CollUtil.isEmpty(skipsGateway)) {
                return null;
            }

            if (!NodeType.isStart(nextNode.getNodeType())) {
                skipsGateway = skipsGateway.stream().filter(t -> {
                    if (NodeType.isGateWaySerial(nextNode.getNodeType())) {
                        AssertUtil.isTrue(MapUtil.isEmpty(flowParams.getVariable()), ExceptionCons.MUST_CONDITIONVALUE_NODE);
                        if (ObjectUtil.isNotNull(t.getSkipCondition())) {
                            return ExpressionUtil.eval(t.getSkipCondition(), flowParams.getVariable());
                        }
                        return true;
                    }
                    // 并行网关可以返回多个跳转
                    return true;

                }).collect(Collectors.toList());
            }
            AssertUtil.isTrue(CollUtil.isEmpty(skipsGateway), ExceptionCons.NULL_CONDITIONVALUE_NODE);

            List<String> nextNodeCodes = StreamUtils.toList(skipsGateway, Skip::getNextNodeCode);
            nextNodes = FlowFactory.nodeService()
                    .getByNodeCodes(nextNodeCodes, nextNode.getDefinitionId());
            AssertUtil.isTrue(CollUtil.isEmpty(nextNodes), ExceptionCons.NOT_NODE_DATA);
        } else {
            nextNodes.add(nextNode);
        }
        return nextNodes;
    }

    @Override
    public Task addTask(Node node, Instance instance, FlowParams flowParams) {
        Task addTask = FlowFactory.newTask();
        Date date = new Date();
        FlowFactory.dataFillHandler().idFill(addTask);
        addTask.setDefinitionId(instance.getDefinitionId());
        addTask.setInstanceId(instance.getId());
        addTask.setNodeCode(node.getNodeCode());
        addTask.setNodeName(node.getNodeName());
        addTask.setNodeType(node.getNodeType());
        addTask.setFlowStatus(setFlowStatus(node.getNodeType(), flowParams.getSkipType()));
        addTask.setCreateTime(date);
        List<String> permissionList;
        if (CollUtil.isNotEmpty(node.getDynamicPermissionFlagList())) {
            permissionList = node.getDynamicPermissionFlagList();
        } else {
            // 查询下一节点权限人
            permissionList = FlowFactory.userService().getPermission(node.getId(), UserType.PROPOSE.getKey());
            // 如果设置了发起人审批，则需要动态替换权限标识
            for (int i = 0; i < permissionList.size(); i++) {
                String permission = permissionList.get(i);
                if (StringUtils.isNotEmpty(permission) && permission.contains(FlowCons.WARMFLOWINITIATOR)) {
                    permissionList.set(i, permission.replace(FlowCons.WARMFLOWINITIATOR, instance.getCreateBy()));
                }
            }
        }
        addTask.setPermissionList(permissionList);
        addTask.setTenantId(flowParams.getTenantId());
        return addTask;
    }

    @Override
    public Integer setFlowStatus(Integer nodeType, String skipType) {
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
    public boolean transfer(Long taskId, FlowParams flowParams) {
        return transfer(taskId, flowParams, false, false);
    }

    @Override
    public boolean transfer(Long taskId, FlowParams flowParams, boolean ignore, boolean clear) {
        return processedHandle(taskId, flowParams, ignore, clear? CirculationType.CHANGE:CirculationType.TRANSFER);
    }

    @Override
    public boolean signature(Long taskId, FlowParams flowParams) {
        return processedHandle(taskId, flowParams, false, CirculationType.SIGNATURE);
    }

    @Override
    public boolean signature(Long taskId, FlowParams flowParams, boolean ignore) {
        return processedHandle(taskId, flowParams, ignore, CirculationType.SIGNATURE);
    }

    @Override
    public boolean depute(Long taskId, FlowParams flowParams) {
        return processedHandle(taskId, flowParams, false, CirculationType.DEPUTE);
    }

    @Override
    public boolean depute(Long taskId, FlowParams flowParams, boolean ignore) {
        return processedHandle(taskId, flowParams, ignore, CirculationType.DEPUTE);
    }

    @Override
    public boolean processedHandle(Long taskId, FlowParams flowParams, boolean ignore, CirculationType circulationType) {
        // 获取给谁的权限
        List<String> additionalHandler = flowParams.getAdditionalHandler();
        AssertUtil.isTrue(CollUtil.isEmpty(additionalHandler), ExceptionCons.LOST_ADDITIONAL_PERMISSION);
        if (!ignore) {
            // 判断当前处理人是否有权限，获取当前办理人的权限
            List<String> permissions = flowParams.getPermissionFlag();
            // 获取任务权限人
            List<String> taskPermissions = FlowFactory.userService().getPermission(taskId, UserType.APPROVAL.getKey());
            AssertUtil.isTrue(CollUtil.notContainsAny(permissions, taskPermissions), ExceptionCons.NOT_AUTHORITY);
        }
        // 增减流程参数 跳转类型和审批意见（留存记录的消息）
        flowParams.skipType(SkipType.PASS.getKey()).record("user:" + flowParams.getCreateBy() + " "
                + circulationType.getKey() + " " + CollUtil.strListToStr(additionalHandler, ","));
        // 留存历史记录
        Task task = FlowFactory.taskService().getById(taskId);
        Node node = FlowFactory.nodeService().getOne(FlowFactory.newNode().setNodeCode(task.getNodeCode())
                .setDefinitionId(task.getDefinitionId()));
        HisTask hisTask = CollUtil.getOne(FlowFactory.hisTaskService().setSkipInsHis(task, CollUtil.toList(node), flowParams));
        FlowFactory.hisTaskService().save(hisTask);

        // 处理此任务的权限流转
        UserType userType = null;
        switch (circulationType){
            // 加减签，清空当前的计划审批人，重新设置新的计划审批人
            case SIGNATURE:
                userType = UserType.APPROVAL;
                break;
            // 转办，不清理原有计划审批人
            case TRANSFER:
            // 转办，清理原有计划审批人
            case CHANGE:
                userType = UserType.ASSIGNEE;
                break;
            // 委派，清理计划审批人，新增受托人
            case DEPUTE:
                userType = UserType.DEPUTE;
                break;
        }
        return FlowFactory.userService().updatePermission(taskId, flowParams.getAdditionalHandler(), userType.getKey(),
                circulationType.getClear(), flowParams.getCreateBy());
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

    /**
     * 构建增代办任务
     *
     * @param flowParams
     * @param task
     * @param instance
     * @param nextNode
     * @return
     */
    private List<Task> buildAddTasks(FlowParams flowParams, Task task, Instance instance
            , List<Node> nextNodes, Node nextNode) {
        boolean buildFlag = false;
        // 下个节点非并行网关节点，可以直接生成下一个代办任务
        if (!NodeType.isGateWayParallel(nextNode.getNodeType())) {
            buildFlag = true;
        } else {
            // 下个节点是并行网关节点，判断前置节点是否都完成
            if (gateWayParallelIsFinish(task, instance, nextNode.getNodeCode())) {
                buildFlag = true;
            }
        }

        if (buildFlag) {
            List<Task> addTasks = new ArrayList<>();
            for (Node node : nextNodes) {
                Task flowTask = addTask(node, instance, flowParams);
                flowTask.setTenantId(task.getTenantId());
                addTasks.add(flowTask);
            }
            return addTasks;
        }
        return null;
    }

    /**
     * 判断并行网关节点前置任务是否都完成
     * 多条线路汇聚到并行网关，必须所有任务都完成，才能继续。 根据并行网关节点，查询前面的节点是否都完成，
     * 判断规则，获取网关所有前置节点，并且查询是否有历史任务记录，前前置节点完成时间是否早于前置节点
     *
     * @param task
     * @param instance
     * @param nextNodeCode
     * @return
     */
    private boolean gateWayParallelIsFinish(Task task, Instance instance, String nextNodeCode) {
        List<Skip> allSkips = FlowFactory.skipService().list(FlowFactory.newSkip()
                .setDefinitionId(instance.getDefinitionId()));
        Map<String, List<Skip>> skipNextMap = StreamUtils.groupByKeyFilter(skip -> !task.getNodeCode()
                        .equals(skip.getNowNodeCode()) || !nextNodeCode.equals(skip.getNextNodeCode())
                , allSkips, Skip::getNextNodeCode);
        List<Skip> oneLastSkips = skipNextMap.get(nextNodeCode);
        // 说明没有其他前置节点，那可以完成往下执行
        if (CollUtil.isEmpty(oneLastSkips)) {
            return true;
        }
        if (CollUtil.isNotEmpty(oneLastSkips)) {
            for (Skip oneLastSkip : oneLastSkips) {
                HisTask oneLastHisTask = CollUtil.getOne(FlowFactory.hisTaskService()
                        .getNoReject(oneLastSkip.getNowNodeCode(), instance.getId()));
                // 查询前置节点是否有完成记录
                if (ObjectUtil.isNull(oneLastHisTask)) {
                    return false;
                }
                List<Skip> twoLastSkips = skipNextMap.get(oneLastSkip.getNowNodeCode());
                for (Skip twoLastSkip : twoLastSkips) {
                    if (NodeType.isStart(twoLastSkip.getNowNodeType())) {
                        return true;
                    } else if (NodeType.isGateWay(twoLastSkip.getNowNodeType())) {
                        // 如果前前置节点是网关，那网关前任意一个任务完成就算完成
                        List<Skip> threeLastSkips = skipNextMap.get(twoLastSkip.getNowNodeCode());
                        for (Skip threeLastSkip : threeLastSkips) {
                            HisTask threeLastHisTask = CollUtil.getOne(FlowFactory.hisTaskService()
                                    .getNoReject(threeLastSkip.getNowNodeCode(), instance.getId()));
                            if (ObjectUtil.isNotNull(threeLastHisTask) && threeLastHisTask.getCreateTime()
                                    .before(oneLastHisTask.getCreateTime())) {
                                return true;
                            }
                        }
                    } else {
                        HisTask twoLastHisTask = CollUtil.getOne(FlowFactory.hisTaskService()
                                .getNoReject(twoLastSkip.getNowNodeCode(), instance.getId()));
                        // 前前置节点完成时间是否早于前置节点，如果是串行网关，那前前置节点必须只有一个完成，如果是并行网关都要完成
                        if (ObjectUtil.isNotNull(twoLastHisTask) && twoLastHisTask.getCreateTime()
                                .before(oneLastHisTask.getCreateTime())) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * 获取任意跳转指定的节点
     *
     * @param task
     * @param flowParams
     * @return
     */
    private Node getAnySkipNode(Task task, FlowParams flowParams) {
        List<Node> curNodes = FlowFactory.nodeService()
                .getByNodeCodes(Collections.singletonList(task.getNodeCode()), task.getDefinitionId());
        Node curNode = CollUtil.getOne(curNodes);
        // 判断当前节点是否可以任意跳转
        AssertUtil.isTrue(ObjectUtil.isNull(curNode), ExceptionCons.NOT_NODE_DATA);

        List<Node> nextNodes = FlowFactory.nodeService()
                .getByNodeCodes(Collections.singletonList(flowParams.getNodeCode()), task.getDefinitionId());
        return CollUtil.getOne(nextNodes);
    }

    /**
     * 通过校验调整类型获取跳转集合
     *
     * @param task
     * @param skips
     * @param flowParams
     * @return
     */
    private List<Skip> getSkipByCheck(Task task, List<Skip> skips, FlowParams flowParams) {
        if (CollUtil.isEmpty(skips)) {
            return null;
        }
        if (!NodeType.isStart(task.getNodeType())) {
            skips = skips.stream().filter(t -> {
                if (StringUtils.isNotEmpty(t.getSkipType())) {
                    return (flowParams.getSkipType()).equals(t.getSkipType());
                }
                return true;
            }).collect(Collectors.toList());
        }
        return skips;
    }

    /**
     * 判断当前处理人是否有权限处理
     *
     * @param NowNode 当前节点权限（动态权限）
     * @param task 当前任务（任务id）
     * @param permissionFlags 当前处理人的权限
     */
    private void checkAuth(Node NowNode, Task task, List<String> permissionFlags) {
        // 如果有动态权限标识，则优先使用动态权限标识
        List<String> permissions;
        if (CollUtil.isNotEmpty(NowNode.getDynamicPermissionFlagList())) {
            permissions = NowNode.getDynamicPermissionFlagList();
        } else {
            permissions = FlowFactory.userService().getPermission(task.getId(), UserType.APPROVAL.getKey());
        }
        // 当前节点
        AssertUtil.isTrue(CollUtil.isEmpty(permissions), ExceptionCons.LOST_NODE_PERMISSION);
        AssertUtil.isTrue(CollUtil.notContainsAny(permissionFlags, permissions), ExceptionCons.NULL_ROLE_NODE);
    }

    // 设置结束节点相关信息
    private void setEndInfo(Instance instance, List<Task> addTasks, List<HisTask> insHisList) {
        if (CollUtil.isNotEmpty(addTasks)) {
            addTasks.removeIf(addTask -> {
                if (NodeType.isEnd(addTask.getNodeType())) {
                    HisTask insHis = FlowFactory.newHisTask()
                            .setTaskId(addTask.getId())
                            .setInstanceId(addTask.getInstanceId())
                            .setNodeCode(addTask.getNodeCode())
                            .setNodeName(addTask.getNodeName())
                            .setNodeType(addTask.getNodeType())
                            .setTenantId(addTask.getTenantId())
                            .setDefinitionId(addTask.getDefinitionId())
                            .setFlowStatus(FlowStatus.FINISHED.getKey())
                            .setCreateTime(new Date());
                    FlowFactory.dataFillHandler().idFill(insHis);
                    insHisList.add(insHis);
                    instance.setNodeType(addTask.getNodeType());
                    instance.setNodeCode(addTask.getNodeCode());
                    instance.setNodeName(addTask.getNodeName());
                    instance.setFlowStatus(FlowStatus.FINISHED.getKey());
                    return true;
                }
                return false;
            });
        }
    }

    private void setSkipInstance(Instance instance, List<Task> addTasks, FlowParams flowParams) {
        instance.setUpdateTime(new Date());
        Map<String, Object> variable = flowParams.getVariable();
        if (MapUtil.isNotEmpty(variable)) {
            String variableStr = instance.getVariable();
            if (StringUtils.isNotEmpty(variableStr)) {
                Map<String, Object> deserialize = ONode.deserialize(variableStr);
                deserialize.putAll(variable);
            }
            instance.setVariable(ONode.serialize(variable));
        }
        // 流程未完成，存在后续任务，才重新设置流程信息
        if (!FlowStatus.isFinished(instance.getFlowStatus()) && CollUtil.isNotEmpty(addTasks)) {
            Task nextTask = getNextTask(addTasks);
            instance.setNodeType(nextTask.getNodeType());
            instance.setNodeCode(nextTask.getNodeCode());
            instance.setNodeName(nextTask.getNodeName());
            instance.setFlowStatus(setFlowStatus(nextTask.getNodeType()
                    , flowParams.getSkipType()));
        }
    }

    /**
     * 一票否决（谨慎使用），如果退回，退回指向节点后还存在其他正在执行的代办任务，转历史任务，状态都为退回,重走流程。
     *
     * @param task
     * @param skipType
     * @param nextNodeCode
     * @return
     */
    private void oneVoteVeto(Task task, String skipType, String nextNodeCode) {
        // 一票否决（谨慎使用），如果退回，退回指向节点后还存在其他正在执行的代办任务，转历史任务，状态失效,重走流程。
        if (SkipType.isReject(skipType)) {
            List<Task> tasks = list(FlowFactory.newTask().setInstanceId(task.getInstanceId()));
            List<Skip> allSkips = FlowFactory.skipService().list(FlowFactory.newSkip()
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
                convertHisTask(noDoneTasks, FlowStatus.INVALID.getKey(), null);
            }
        }
    }


    /**
     * 判断是否属于退回指向节点的后置未完成的任务
     *
     * @param nextNodeCode
     * @param lastSkips
     * @param skipMap
     * @return
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
     * 处理未完成的任务，当流程完成，还存在代办任务未完成，转历史任务，状态完成。
     *
     * @param instance
     * @param taskId   排除此任务
     */
    private void handUndoneTask(Instance instance, Long taskId) {
        if (FlowStatus.isFinished(instance.getFlowStatus())) {
            List<Task> taskList = list(FlowFactory.newTask().setInstanceId(instance.getId()));
            if (CollUtil.isNotEmpty(taskList)) {
                convertHisTask(taskList, FlowStatus.FINISHED.getKey(), taskId);
            }
        }
    }

    /**
     * 代办任务转历史任务。
     *
     * @param taskList
     */
    private void convertHisTask(List<Task> taskList, Integer flowStatus, Long taskId) {
        List<HisTask> insHisList = new ArrayList<>();
        for (Task task : taskList) {
            if (ObjectUtil.isNotNull(taskId) && !task.getId().equals(taskId)) {
                HisTask insHis = FlowFactory.newHisTask();
                insHis.setId(task.getId());
                insHis.setInstanceId(task.getInstanceId());
                insHis.setDefinitionId(task.getDefinitionId());
                insHis.setNodeCode(task.getNodeCode());
                insHis.setNodeName(task.getNodeName());
                insHis.setNodeType(task.getNodeType());
                insHis.setFlowStatus(flowStatus);
                insHis.setCreateTime(new Date());
                insHis.setTenantId(task.getTenantId());
                insHisList.add(insHis);
            }
        }
        removeByIds(StreamUtils.toList(taskList, Task::getId));
        FlowFactory.hisTaskService().saveBatch(insHisList);
        // 删除所有代办任务的权限人
        FlowFactory.userService().delUser(StreamUtils.toList(taskList, Task::getId));
    }

    /**
     * 更新流程信息
     *
     * @param task
     * @param instance
     * @param insHisList
     * @param addTasks
     */
    private void updateFlowInfo(Task task, Instance instance, List<HisTask> insHisList
            , List<Task> addTasks, List<User> users) {
        removeById(task.getId());
        FlowFactory.hisTaskService().saveBatch(insHisList);
        if (CollUtil.isNotEmpty(addTasks)) {
            saveBatch(addTasks);
        }
        FlowFactory.insService().updateById(instance);
        // 保存下一个代办任务的权限人和历史任务的处理人
        FlowFactory.userService().saveBatch(users);
    }
}
