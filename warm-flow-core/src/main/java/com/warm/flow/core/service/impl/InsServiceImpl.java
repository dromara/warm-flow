package com.warm.flow.core.service.impl;

import com.warm.flow.core.FlowFactory;
import com.warm.flow.core.constant.ExceptionCons;
import com.warm.flow.core.dto.FlowParams;
import com.warm.flow.core.entity.*;
import com.warm.flow.core.enums.FlowStatus;
import com.warm.flow.core.enums.NodeType;
import com.warm.flow.core.enums.SkipType;
import com.warm.flow.core.exception.FlowException;
import com.warm.flow.core.dao.FlowInstanceDao;
import com.warm.flow.core.orm.service.impl.WarmServiceImpl;
import com.warm.flow.core.service.InsService;
import com.warm.flow.core.utils.AssertUtil;
import com.warm.flow.core.utils.ExpressionUtil;
import com.warm.tools.utils.*;

import java.util.*;
import java.util.stream.Collectors;


/**
 * 流程实例Service业务层处理
 *
 * @author warm
 * @date 2023-03-29
 */
public class InsServiceImpl extends WarmServiceImpl<FlowInstanceDao, Instance> implements InsService {

    @Override
    public InsService setDao(FlowInstanceDao warmDao) {
        this.warmDao = warmDao;
        return this;
    }

    @Override
    public List<Instance> getByIdWithLock(List<Long> ids) {
        AssertUtil.isTrue(CollUtil.isEmpty(ids), ExceptionCons.NOT_FOUNT_INSTANCE_ID);
        for (Long id : ids) {
            AssertUtil.isNull(id, "流程定义id不能为空!");
        }
        return getDao().getByIdWithLock(ids);
    }

    @Override
    public Instance start(String businessId, FlowParams flowUser) {

        AssertUtil.isNull(flowUser.getFlowCode(), ExceptionCons.NULL_FLOW_CODE);
        AssertUtil.isTrue(StringUtils.isEmpty(businessId), ExceptionCons.NULL_BUSINESS_ID);
        // 获取有意义有用的节点
        List<Node> nodes = FlowFactory.nodeService().getByFlowCode(flowUser.getFlowCode());
        AssertUtil.isTrue(CollUtil.isEmpty(nodes), ExceptionCons.NOT_PUBLISH_NODE);
        // 获取开始节点
        Node startNode = getFirstBetween(nodes);

        AssertUtil.isBlank(businessId, ExceptionCons.NULL_BUSINESS_ID);
        // 设置流程实例对象
        Instance instance = setStartInstance(startNode, businessId, flowUser);
        // 设置流程历史任务记录对象
        Task task = addTask(startNode, instance, flowUser);
        save(instance);
        FlowFactory.taskService().save(task);
        return instance;
    }

    @Override
    public Instance skipByInsId(Long instanceId, FlowParams flowUser) {
        AssertUtil.isTrue(StringUtils.isNotEmpty(flowUser.getMessage())
                && flowUser.getMessage().length() > 500, ExceptionCons.MSG_OVER_LENGTH);
        // 获取当前流程
        Instance instance = getById(instanceId);
        AssertUtil.isTrue(FlowStatus.isFinished(instance.getFlowStatus()), ExceptionCons.FLOW_FINISH);
        AssertUtil.isTrue(ObjectUtil.isNull(instance), ExceptionCons.NOT_FOUNT_INSTANCE);
        // 获取待办任务
        List<Task> taskList = FlowFactory.taskService().getByInsId(instanceId);
        AssertUtil.isTrue(CollUtil.isEmpty(taskList), ExceptionCons.NOT_FOUNT_TASK);
        AssertUtil.isTrue(taskList.size() > 1, ExceptionCons.TASK_NOT_ONE);
        Task task = taskList.get(0);
        return skip(flowUser, task, instance);
    }

    @Override
    public Instance skip(Long taskId, FlowParams flowUser) {
        AssertUtil.isTrue(StringUtils.isNotEmpty(flowUser.getMessage())
                && flowUser.getMessage().length() > 500, ExceptionCons.MSG_OVER_LENGTH);
        // 获取待办任务
        Task task = FlowFactory.taskService().getById(taskId);
        AssertUtil.isTrue(ObjectUtil.isNull(task), ExceptionCons.NOT_FOUNT_TASK);
        // 获取当前流程
        Instance instance = getById(task.getInstanceId());
        AssertUtil.isTrue(ObjectUtil.isNull(instance), ExceptionCons.NOT_FOUNT_INSTANCE);
        return skip(flowUser, task, instance);
    }


    @Override
    public boolean remove(List<Long> instanceIds) {
        return toRemoveTask(instanceIds);
    }

    private Instance skip(FlowParams flowUser, Task task, Instance instance) {
        // TODO min 后续考虑并发问题，待办任务和实例表不同步，可给代办任务id加锁，抽取所接口，方便后续兼容分布式锁
        // 非第一个记得跳转类型必传
        if (!NodeType.isStart(task.getNodeType())) {
            AssertUtil.isFalse(StringUtils.isNotEmpty(flowUser.getSkipType()), ExceptionCons.NULL_CONDITIONVALUE);
        }

        List<HisTask> insHisList = new ArrayList<>();
        // 获取关联的节点
        Node nextNode = getNextNode(task, flowUser);

        // 如果是网关节点，则重新获取后续节点
        List<Node> nextNodes = checkGateWay(flowUser, task, nextNode);

        // 构建代办任务
        List<Task> addTasks = buildAddTasks(flowUser, task, instance, nextNodes, nextNode);

        // 设置流程历史任务信息
        insHisList.add(setSkipInsHis(task, nextNodes, flowUser));

        // 设置流程实例信息
        setSkipInstance(instance, nextNodes, addTasks, flowUser);

        // 一票否决（谨慎使用），如果驳回，驳回指向节点后还存在其他正在执行的代办任务，转历史任务，状态都为失效,重走流程。
        oneVoteVeto(task, flowUser.getSkipType(), nextNode.getNodeCode());

        // 更新流程信息
        updateFlowInfo(task, instance, insHisList, addTasks);

        // 处理未完成的任务，当流程完成，还存在代办任务未完成，转历史任务，状态完成。
        handUndoneTask(instance, nextNodes);
        return instance;
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
     * 一票否决（谨慎使用），如果驳回，驳回指向节点后还存在其他正在执行的代办任务，转历史任务，状态都为驳回,重走流程。
     *
     * @param task
     * @param skipType
     * @param nextNodeCode
     * @return
     */
    private void oneVoteVeto(Task task, String skipType, String nextNodeCode) {
        // 一票否决（谨慎使用），如果驳回，驳回指向节点后还存在其他正在执行的代办任务，转历史任务，状态失效,重走流程。
        if (SkipType.isReject(skipType)) {
            List<Task> tasks = FlowFactory.taskService().getByInsId(task.getInstanceId());
            List<Skip> allSkips = FlowFactory.skipService().list(FlowFactory.newSkip()
                    .setDefinitionId(task.getDefinitionId()));
            // 排除执行当前节点的流程跳转
            Map<String, List<Skip>> skipMap = StreamUtils.groupByKeyFilter(skip ->
                    !task.getNodeCode().equals(skip.getNextNodeCode()), allSkips, Skip::getNextNodeCode);
            // 属于驳回指向节点的后置未完成的任务
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
                convertHisTask(noDoneTasks, FlowStatus.INVALID.getKey());
            }
        }
    }

    /**
     * 判断是否属于驳回指向节点的后置未完成的任务
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
     * 校验是否网关节点,如果是重新获取新的后面的节点
     *
     * @param flowUser
     * @param task
     * @param nextNode
     * @return
     */
    private List<Node> checkGateWay(FlowParams flowUser, Task task, Node nextNode) {
        List<Node> nextNodes = new ArrayList<>();
        if (NodeType.isGateWay(nextNode.getNodeType())) {
            List<Skip> skipsGateway = FlowFactory.skipService()
                    .queryByDefAndCode(nextNode.getDefinitionId(), nextNode.getNodeCode());

            if (CollUtil.isEmpty(skipsGateway)) {
                return null;
            }

            if (!NodeType.isStart(nextNode.getNodeType())) {
                skipsGateway = skipsGateway.stream().filter(t -> {
                    if (NodeType.isGateWaySerial(nextNode.getNodeType())) {
                        AssertUtil.isTrue(MapUtil.isEmpty(flowUser.getSkipCondition()), ExceptionCons.MUST_CONDITIONVALUE_NODE);
                        if (ObjectUtil.isNotNull(t.getSkipCondition())) {
                            return ExpressionUtil.eval(t.getSkipCondition(), flowUser.getSkipCondition());
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
                    .getByNodeCodes(nextNodeCodes, task.getDefinitionId());
            AssertUtil.isTrue(CollUtil.isEmpty(nextNodes), ExceptionCons.NOT_NODE_DATA);
        } else {
            nextNodes.add(nextNode);
        }
        return nextNodes;
    }

    /**
     * 构建增代办任务
     *
     * @param flowUser
     * @param task
     * @param instance
     * @param nextNode
     * @return
     */
    private List<Task> buildAddTasks(FlowParams flowUser, Task task, Instance instance
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
                // 结束节点不生成代办任务
                if (!NodeType.isEnd(node.getNodeType())) {
                    Task flowTask = addTask(node, instance, flowUser);
                    // 如果是并行网关节点, 把网关编码传递给新的代办任务
                    if (NodeType.isGateWayParallel(nextNode.getNodeType())) {
                        flowTask.setGateWayNode(task.getGateWayNode());
                    }
                    flowTask.setTenantId(task.getTenantId());
                    addTasks.add(flowTask);
                }
            }
            return addTasks;
        }
        return null;
    }

    /**
     * 处理未完成的任务，当流程完成，还存在代办任务未完成，转历史任务，状态完成。
     *
     * @param instance
     * @param nextNodes
     */
    private void handUndoneTask(Instance instance, List<Node> nextNodes) {
        Long endInstanceId = null;
        for (Node nextNode : nextNodes) {
            // 结束节点不生成代办任务
            if (NodeType.isEnd(nextNode.getNodeType())) {
                endInstanceId = instance.getId();
                break;
            }
        }
        if (ObjectUtil.isNotNull(endInstanceId)) {
            List<Task> taskList = FlowFactory.taskService().getByInsId(endInstanceId);
            if (CollUtil.isNotEmpty(taskList)) {
                convertHisTask(taskList, FlowStatus.FINISHED.getKey());
            }
        }
    }

    /**
     * 代办任务转历史任务。
     *
     * @param taskList
     */
    private void convertHisTask(List<Task> taskList, Integer flowStatus) {
        List<HisTask> insHisList = new ArrayList<>();
        for (Task task : taskList) {
            HisTask insHis = FlowFactory.newHisTask();
            insHis.setId(task.getId());
            insHis.setInstanceId(task.getInstanceId());
            insHis.setDefinitionId(task.getDefinitionId());
            insHis.setNodeCode(task.getNodeCode());
            insHis.setNodeName(task.getNodeName());
            insHis.setNodeType(task.getNodeType());
            insHis.setPermissionFlag(task.getPermissionFlag());
            insHis.setFlowStatus(flowStatus);
            insHis.setCreateTime(new Date());
            insHis.setTenantId(task.getTenantId());
            insHisList.add(insHis);
        }
        FlowFactory.taskService().removeByIds(StreamUtils.toList(taskList, Task::getId));
        FlowFactory.hisTaskService().saveBatch(insHisList);
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
            , List<Task> addTasks) {
        FlowFactory.taskService().removeById(task.getId());
        FlowFactory.hisTaskService().saveBatch(insHisList);
        if (CollUtil.isNotEmpty(addTasks)) {
            FlowFactory.taskService().saveBatch(addTasks);
        }
        updateById(instance);
    }

    private void setSkipInstance(Instance instance, List<Node> nextNodes, List<Task> addTasks
            , FlowParams flowUser) {
        instance.setUpdateTime(new Date());
        for (Node node : nextNodes) {
            // 结束节点不生成代办任务
            if (NodeType.isEnd(node.getNodeType())) {
                instance.setFlowStatus(FlowStatus.FINISHED.getKey());
                return;
            }
        }
        // 如果是汇聚并行网关，不是最后一个执行的分支，addTasks会为空
        if (CollUtil.isNotEmpty(addTasks)) {
            Task task = getNextTask(addTasks);
            instance.setNodeType(task.getNodeType());
            instance.setNodeCode(task.getNodeCode());
            instance.setNodeName(task.getNodeName());
            instance.setFlowStatus(setFlowStatus(task.getNodeType(), flowUser.getSkipType()));
        }
    }

    /**
     * 设置流程历史任务信息
     *
     * @param task
     * @param nextNodes
     * @return
     */
    private HisTask setSkipInsHis(Task task, List<Node> nextNodes, FlowParams flowUser) {
        HisTask insHis = FlowFactory.newHisTask();
        insHis.setId(task.getId());
        insHis.setInstanceId(task.getInstanceId());
        insHis.setDefinitionId(task.getDefinitionId());
        insHis.setNodeCode(task.getNodeCode());
        insHis.setNodeName(task.getNodeName());
        insHis.setNodeType(task.getNodeType());
        insHis.setPermissionFlag(task.getPermissionFlag());
        insHis.setTargetNodeCode(StreamUtils.join(nextNodes, Node::getNodeCode));
        insHis.setTargetNodeName(StreamUtils.join(nextNodes, Node::getNodeName));
        insHis.setFlowStatus(setHisFlowStatus(getNextNode(nextNodes).getNodeType(), flowUser.getSkipType()));
        insHis.setGateWayNode(task.getGateWayNode());
        insHis.setMessage(flowUser.getMessage());
        insHis.setCreateTime(new Date());
        insHis.setApprover(flowUser.getCreateBy());
        insHis.setTenantId(task.getTenantId());
        return insHis;
    }

    /**
     * 下个节点是结束节点，取结束节点类型，否则随便取第一条
     *
     * @param nextNodes
     * @return
     */
    private Node getNextNode(List<Node> nextNodes) {
        if (nextNodes.size() == 1) {
            return nextNodes.get(0);
        }
        for (Node nextNode : nextNodes) {
            if (NodeType.isEnd(nextNode.getNodeType())) {
                return nextNode;
            }
        }
        return nextNodes.get(0);
    }

    /**
     * 并行网关，流程状态,取哪个流程节点
     *
     * @param tasks
     * @return
     */
    private Task getNextTask(List<Task> tasks) {
        if (tasks.size() == 1) {
            return tasks.get(0);
        }
        for (Task task : tasks) {
            if (NodeType.isEnd(task.getNodeType())) {
                return task;
            }
        }
        return tasks.get(0);
    }

    /**
     * 设置流程实例和代码任务流程状态
     *
     * @param nodeType 节点类型（开始节点、中间节点、结束节点）
     * @param skipType 流程条件
     */
    private Integer setFlowStatus(Integer nodeType, String skipType) {
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

    /**
     * 设置流程实例和代码任务流程状态
     *
     * @param skipType 流程条件
     */
    private Integer setHisFlowStatus(Integer nodeType, String skipType) {
        // 根据审批动作确定流程状态
        if (NodeType.isEnd(nodeType)) {
            return FlowStatus.FINISHED.getKey();
        } else if (SkipType.isReject(skipType)) {
            return FlowStatus.REJECT.getKey();
        } else {
            return FlowStatus.PASS.getKey();
        }
    }

    /**
     * 设置流程实例对象
     *
     * @param startNode
     * @param businessId
     * @return
     */
    private Instance setStartInstance(Node startNode, String businessId
            , FlowParams flowUser) {
        Instance instance = FlowFactory.newIns();
        Date now = new Date();
        Long id = IdUtils.nextId();
        instance.setId(id);
        instance.setDefinitionId(startNode.getDefinitionId());
        instance.setBusinessId(businessId);
        instance.setNodeType(startNode.getNodeType());
        instance.setNodeCode(startNode.getNodeCode());
        instance.setNodeName(startNode.getNodeName());
        instance.setFlowStatus(FlowStatus.TOBESUBMIT.getKey());
        // 关联业务id,起始后面可以不用到业务id,传业务id目前来看只是为了批量创建流程的时候能创建出有区别化的流程,也是为了后期需要用到businessId。
        instance.setCreateTime(now);
        instance.setUpdateTime(now);
        instance.setCreateBy(flowUser.getCreateBy());
        instance.setExt(flowUser.getExt());
        return instance;
    }

    /**
     * 设置流程待办任务对象
     *
     * @param node
     * @param instance
     * @return
     */
    private Task addTask(Node node, Instance instance, FlowParams flowUser) {
        Task task = FlowFactory.newTask();
        Date date = new Date();
        task.setId(IdUtils.nextId());
        task.setDefinitionId(instance.getDefinitionId());
        task.setInstanceId(instance.getId());
        task.setNodeCode(node.getNodeCode());
        task.setNodeName(node.getNodeName());
        task.setNodeType(node.getNodeType());
        task.setPermissionFlag(node.getPermissionFlag());
        task.setApprover(flowUser.getCreateBy());
        task.setFlowStatus(setFlowStatus(node.getNodeType(), flowUser.getSkipType()));
        task.setCreateTime(date);
        task.setTenantId(flowUser.getTenantId());
        return task;
    }

    /**
     * 权限和条件校验
     * @param task
     * @param skips
     * @param flowUser
     * @return
     */
    private Skip checkAuthAndCondition(Task task, List<Skip> skips, FlowParams flowUser) {
        if (CollUtil.isEmpty(skips)) {
            return null;
        }
        List<String> permissionFlags = flowUser.getPermissionFlag();
        List<Node> nodes = FlowFactory.nodeService()
                .getByNodeCodes(Collections.singletonList(task.getNodeCode()), task.getDefinitionId());
        Node node = CollUtil.getOne(nodes);
        AssertUtil.isTrue(ObjectUtil.isNull(node), ExceptionCons.NOT_NODE_DATA);

        AssertUtil.isTrue(StringUtils.isNotEmpty(node.getPermissionFlag()) && (CollUtil.isEmpty(permissionFlags)
                || !CollUtil.containsAny(permissionFlags, ArrayUtil.strToArrAy(node.getPermissionFlag(),
                ","))), ExceptionCons.NULL_ROLE_NODE);

        if (!NodeType.isStart(task.getNodeType())) {
            skips = skips.stream().filter(t -> {
                if (StringUtils.isNotEmpty(t.getSkipType())) {
                    return (flowUser.getSkipType()).equals(t.getSkipType());
                }
                return true;
            }).collect(Collectors.toList());
        }
        AssertUtil.isTrue(CollUtil.isEmpty(skips), ExceptionCons.NULL_CONDITIONVALUE_NODE);
        return skips.get(0);
    }

    /**
     * 校验跳转指定节点是否有权限任意退回
     * @param task
     * @param flowUser
     * @return
     */
    private Node checkSkipAppointAuth(Task task, FlowParams flowUser) {
        List<String> permissionFlags = flowUser.getPermissionFlag();
        List<Node> curNodes = FlowFactory.nodeService()
                .getByNodeCodes(Collections.singletonList(task.getNodeCode()), task.getDefinitionId());
        Node curNode = CollUtil.getOne(curNodes);
        // 判断当前节点是否可以任意退回
        AssertUtil.isTrue(ObjectUtil.isNull(curNode), ExceptionCons.NOT_NODE_DATA);
        AssertUtil.isFalse("Y".equals(curNode.getSkipAnyNode()), ExceptionCons.SKIP_ANY_NODE);

        AssertUtil.isTrue(StringUtils.isNotEmpty(curNode.getPermissionFlag()) && (CollUtil.isEmpty(permissionFlags)
                || !CollUtil.containsAny(permissionFlags, ArrayUtil.strToArrAy(curNode.getPermissionFlag(),
                ","))), ExceptionCons.NULL_ROLE_NODE);
        List<Node> nextNodes = FlowFactory.nodeService()
                .getByNodeCodes(Collections.singletonList(flowUser.getNodeCode()), task.getDefinitionId());
        return CollUtil.getOne(nextNodes);
    }

    /**
     * 根据流程id+当前流程节点编码获取与之直接关联(其为源节点)的节点。 definitionId:流程id nodeCode:当前流程状态
     * skipType:跳转条件,没有填写的话不做校验
     *
     * @param task
     * @param flowUser
     * @return
     */
    private Node getNextNode(Task task, FlowParams flowUser) {
        AssertUtil.isNull(task.getDefinitionId(), ExceptionCons.NOT_DEFINITION_ID);
        AssertUtil.isBlank(task.getNodeCode(), ExceptionCons.LOST_NODE_CODE);
        // 如果指定了跳转节点，则判断权限，直接获取节点
        if (StringUtils.isNotEmpty(flowUser.getNodeCode())) {
            return checkSkipAppointAuth(task, flowUser);
        }
        List<Skip> skips = FlowFactory.skipService()
                .queryByDefAndCode(task.getDefinitionId(), task.getNodeCode());
        Skip nextSkip = checkAuthAndCondition(task, skips, flowUser);
        AssertUtil.isTrue(ObjectUtil.isNull(nextSkip), ExceptionCons.NULL_DEST_NODE);
        List<Node> nodes = FlowFactory.nodeService()
                .getByNodeCodes(Collections.singletonList(nextSkip.getNextNodeCode()), task.getDefinitionId());
        AssertUtil.isTrue(CollUtil.isEmpty(nodes), ExceptionCons.NOT_NODE_DATA);
        AssertUtil.isTrue(nodes.size() > 1, "[" + nextSkip.getNextNodeCode() + "]" + ExceptionCons.SAME_NODE_CODE);
        AssertUtil.isTrue(NodeType.isStart(nodes.get(0).getNodeType()), ExceptionCons.FRIST_FORBID_BACK);
        return nodes.get(0);

    }

    /**
     * 有且只能有一个开始节点
     *
     * @param nodes
     * @return
     */
    private Node getFirstBetween(List<Node> nodes) {
        for (Node node : nodes) {
            if (NodeType.isStart(node.getNodeType())) {
                List<Skip> skips = FlowFactory.skipService().queryByDefAndCode(node.getDefinitionId(), node.getNodeCode());
                Skip skip = skips.get(0);
                return FlowFactory.nodeService().getByNodeCode(skip.getNextNodeCode(), skip.getDefinitionId());
            }
        }
        throw new FlowException(ExceptionCons.LOST_START_NODE);
    }

    private boolean toRemoveTask(List<Long> instanceIds) {
        AssertUtil.isTrue(CollUtil.isEmpty(instanceIds), ExceptionCons.NULL_INSTANCE_ID);
        boolean success = FlowFactory.taskService().deleteByInsIds(instanceIds);
        if (success) {
            FlowFactory.hisTaskService().deleteByInsIds(instanceIds);
            return FlowFactory.insService().removeByIds(instanceIds);
        }
        return false;
    }

}
