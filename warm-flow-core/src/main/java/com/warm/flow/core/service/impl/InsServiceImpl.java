package com.warm.flow.core.service.impl;

import com.warm.flow.core.FlowFactory;
import com.warm.flow.core.constant.ExceptionCons;
import com.warm.flow.core.domain.dto.FlowParams;
import com.warm.flow.core.domain.entity.*;
import com.warm.flow.core.enums.FlowStatus;
import com.warm.flow.core.enums.NodeType;
import com.warm.flow.core.enums.SkipType;
import com.warm.flow.core.exception.FlowException;
import com.warm.flow.core.mapper.FlowInstanceMapper;
import com.warm.flow.core.service.InsService;
import com.warm.flow.core.utils.AssertUtil;
import com.warm.mybatis.core.service.impl.WarmServiceImpl;
import com.warm.tools.utils.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 流程实例Service业务层处理
 *
 * @author warm
 * @date 2023-03-29
 */
public class InsServiceImpl extends WarmServiceImpl<FlowInstanceMapper, FlowInstance> implements InsService {

    @Override
    public List<FlowInstance> getByIdWithLock(List<Long> ids) {
        AssertUtil.isTrue(CollUtil.isEmpty(ids), ExceptionCons.NOT_FOUNT_INSTANCE_ID);
        for (Long id : ids) {
            AssertUtil.isNull(id, "流程定义id不能为空!");
        }
        return getMapper().getByIdWithLock(ids);
    }

    @Override
    public FlowInstance start(String businessId, FlowParams flowUser) {

        AssertUtil.isNull(flowUser.getFlowCode(), ExceptionCons.NULL_FLOW_CODE);
        AssertUtil.isTrue(StringUtils.isEmpty(businessId), ExceptionCons.NULL_BUSINESS_ID);
        // 获取有意义有用的节点
        List<FlowNode> nodes = FlowFactory.nodeService().getByFlowCode(flowUser.getFlowCode());
        AssertUtil.isTrue(CollUtil.isEmpty(nodes), ExceptionCons.NOT_PUBLISH_NODE);
        // 获取开始结点
        FlowNode startNode = getFirstBetween(nodes);

        AssertUtil.isBlank(businessId, ExceptionCons.NULL_BUSINESS_ID);
        // 设置流程实例对象
        FlowInstance instance = setStartInstance(startNode, businessId, flowUser);
        // 设置流程历史任务记录对象
        FlowTask task = addTask(startNode, instance, flowUser);
        task.setTenantId(flowUser.getTenantId());
        save(instance);
        FlowFactory.taskService().save(task);
        return instance;
    }

    @Override
    public FlowInstance skipByInsId(Long instanceId, FlowParams flowUser) {
        AssertUtil.isTrue(StringUtils.isNotEmpty(flowUser.getMessage())
                && flowUser.getMessage().length() > 500, ExceptionCons.MSG_OVER_LENGTH);
        // 获取当前流程
        FlowInstance instance = getById(instanceId);
        AssertUtil.isTrue(ObjectUtil.isNull(instance), ExceptionCons.NOT_FOUNT_INSTANCE);
        // 获取待办任务
        List<FlowTask> taskList = FlowFactory.taskService().getByInsId(instanceId);
        AssertUtil.isTrue(CollUtil.isEmpty(taskList), ExceptionCons.NOT_FOUNT_TASK);
        AssertUtil.isTrue(taskList.size() > 1, ExceptionCons.TASK_NOT_ONE);
        FlowTask task = taskList.get(0);
        return skip(flowUser, task, instance);
    }

    @Override
    public FlowInstance skip(Long taskId, FlowParams flowUser) {
        AssertUtil.isTrue(StringUtils.isNotEmpty(flowUser.getMessage())
                && flowUser.getMessage().length() > 500, ExceptionCons.MSG_OVER_LENGTH);
        // 获取待办任务
        FlowTask task = FlowFactory.taskService().getById(taskId);
        AssertUtil.isTrue(ObjectUtil.isNull(task), ExceptionCons.NOT_FOUNT_TASK);
        // 获取当前流程
        FlowInstance instance = getById(task.getInstanceId());
        AssertUtil.isTrue(ObjectUtil.isNull(instance), ExceptionCons.NOT_FOUNT_INSTANCE);
        return skip(flowUser, task, instance);
    }


    @Override
    public boolean remove(List<Long> instanceIds) {
        return toRemoveTask(instanceIds);
    }

    private FlowInstance skip(FlowParams flowUser, FlowTask task, FlowInstance instance) {
        // TODO min 后续考虑并发问题，待办任务和实例表不同步，可给代办任务id加锁，抽取所接口，方便后续兼容分布式锁
        // 非第一个记得跳转类型必传
        if (!NodeType.START.getKey().equals(task.getNodeType())) {
            AssertUtil.isFalse(StringUtils.isNotEmpty(flowUser.getSkipType()), ExceptionCons.NULL_CONDITIONVALUE);
        }

        List<FlowHisTask> insHisList = new ArrayList<>();
        // 获取关联的结点
        List<FlowNode> nextNodes = getNextNode(task, flowUser);
        FlowNode nextNode = nextNodes.get(0);
        Integer gatewayNodeType = nextNode.getNodeType();

        // 多条线路汇聚到并行网关，必须所有任务都完成，才能继续。 根据并行网关节点，查询前面的节点是否都完成，
        // 获取网关所有前置节点，并且查询前置节点是否完成，前置节点前面的节点完成时间是否早于前置节点
        if (NodeType.isGateWayParallel(gatewayNodeType)) {


        }

        // 如果是网关节点，则重新获取后续节点
        nextNodes = checkGateway(flowUser, task, gatewayNodeType, nextNode, nextNodes);

        // 构建增代办任务
        List<FlowTask> addTasks = buildAddTasks(flowUser, task, instance, nextNodes, gatewayNodeType);

        // 设置流程历史任务信息
        insHisList.add(setSkipInsHis(task, nextNodes, flowUser));

        // 更新流程信息
        updateFlowInfo(flowUser, task, instance, nextNodes, insHisList, addTasks);

        // 一票否决（谨慎使用），如果驳回，驳回指向节点后还存在其他正在执行的代办任务，转历史任务，状态失效,重走流程。
        if (SkipType.REJECT.getKey().equals(flowUser.getSkipType())) {


        }

        // 当流程完成，还存在代办任务未完成，转历史任务，状态完成。
        convertInsHis(instance, nextNodes);

        return instance;
    }

    /**
     * 校验是否网关节点,如果纯正获取新的后面的节点
     * @param flowUser
     * @param task
     * @param gatewayNodeType
     * @param nextNode
     * @return
     */
    private List<FlowNode> checkGateway(FlowParams flowUser, FlowTask task, Integer gatewayNodeType
            , FlowNode nextNode, List<FlowNode> nextNodes) {
        if (NodeType.isGateWay(gatewayNodeType)) {
            List<FlowSkip> skipsGateway = FlowFactory.skipService()
                    .queryByDefAndCode(nextNode.getDefinitionId(), nextNode.getNodeCode());

            if (CollUtil.isEmpty(skipsGateway)) {
                return null;
            }

            if (!NodeType.START.getKey().equals(nextNode.getNodeType())) {
                skipsGateway = skipsGateway.stream().filter(t -> {
                    if (NodeType.isGateWaySerial(nextNode.getNodeType())) {
                        if (ObjectUtil.isNotNull(t.getSkipCondition())) {
                            return (flowUser.getSkipCondition()).equals(t.getSkipCondition());
                        }
                        return true;
                    }
                    // 并行网关可以返回多个跳转
                    return true;

                }).collect(Collectors.toList());
            }
            AssertUtil.isTrue(CollUtil.isEmpty(skipsGateway), ExceptionCons.NULL_CONDITIONVALUE_NODE);

            List<String> nextNodeCodes = StreamUtils.toList(skipsGateway, FlowSkip::getNextNodeCode);
            nextNodes = FlowFactory.nodeService()
                    .getByNodeCodes(nextNodeCodes, task.getDefinitionId());
            AssertUtil.isTrue(CollUtil.isEmpty(nextNodes), ExceptionCons.NOT_NODE_DATA);
        }
        return nextNodes;
    }

    /**
     * 构建增代办任务
     * @param flowUser
     * @param task
     * @param instance
     * @param nextNodes
     * @param gatewayNodeType
     * @return
     */
    private List<FlowTask> buildAddTasks(FlowParams flowUser, FlowTask task, FlowInstance instance
            , List<FlowNode> nextNodes, Integer gatewayNodeType) {
        List<FlowTask> addTasks = new ArrayList<>();
        for (FlowNode flowNode : nextNodes) {
            // 结束节点不生成代办任务
            if (!NodeType.END.getKey().equals(flowNode.getNodeType())) {
                FlowTask flowTask = addTask(flowNode, instance, flowUser);
                // 如果是并行网关节点, 把网关编码传递给新的代办任务
                if (NodeType.isGateWayParallel(gatewayNodeType)) {
                    flowTask.setGateWayNode(task.getGateWayNode());
                }
                flowTask.setTenantId(task.getTenantId());
                addTasks.add(flowTask);
            }
        }
        return addTasks;
    }

    /**
     * 当流程完成，还存在代办任务未完成，转历史任务，状态完成。
     *
     * @param instance
     * @param nextNodes
     */
    private void convertInsHis(FlowInstance instance, List<FlowNode> nextNodes) {
        Long endInstanceId = null;
        for (FlowNode nextNode : nextNodes) {
            // 结束节点不生成代办任务
            if (NodeType.END.getKey().equals(nextNode.getNodeType())) {
                endInstanceId = instance.getId();
            }
        }
        if (ObjectUtil.isNotNull(endInstanceId)) {
            List<FlowTask> taskList = FlowFactory.taskService().getNoFinish(endInstanceId);
            List<FlowHisTask> insHisList = convertInsHis(taskList, FlowStatus.FINISHED.getKey());
            FlowFactory.taskService().removeByIds(StreamUtils.toList(taskList, FlowTask::getId));
            FlowFactory.hisTaskService().saveBatch(insHisList);
        }
    }

    /**
     * 代办任务转历史任务。
     *
     * @param taskList
     */
    private List<FlowHisTask> convertInsHis(List<FlowTask> taskList, Integer flowStatus) {
        List<FlowHisTask> insHisList = new ArrayList<>();
        for (FlowTask flowTask : taskList) {
            FlowHisTask insHis = new FlowHisTask();
            insHis.setId(flowTask.getId());
            insHis.setInstanceId(flowTask.getInstanceId());
            insHis.setDefinitionId(flowTask.getDefinitionId());
            insHis.setNodeCode(flowTask.getNodeCode());
            insHis.setNodeName(flowTask.getNodeName());
            insHis.setNodeType(flowTask.getNodeType());
            insHis.setPermissionFlag(flowTask.getPermissionFlag());
            insHis.setFlowStatus(flowStatus);
            insHis.setCreateTime(new Date());
            insHis.setTenantId(flowTask.getTenantId());
            insHisList.add(insHis);
        }
        return insHisList;
    }

    /**
     * 更新流程信息
     *
     * @param flowUser
     * @param task
     * @param instance
     * @param nextNodes
     * @param insHisList
     * @param addTasks
     */
    private void updateFlowInfo(FlowParams flowUser, FlowTask task, FlowInstance instance, List<FlowNode> nextNodes
            , List<FlowHisTask> insHisList, List<FlowTask> addTasks) {
        setSkipInstance(instance, nextNodes, flowUser);
        FlowFactory.taskService().removeById(task);
        FlowFactory.hisTaskService().saveBatch(insHisList);
        if (CollUtil.isNotEmpty(addTasks)) {
            FlowFactory.taskService().saveBatch(addTasks);
        }
        updateById(instance);
    }

    private void setSkipInstance(FlowInstance instance, List<FlowNode> nextNodes, FlowParams flowUser) {
        FlowNode flowNode = getNextNode(nextNodes);
        instance.setUpdateTime(new Date());
        instance.setNodeType(flowNode.getNodeType());
        instance.setNodeCode(flowNode.getNodeCode());
        instance.setNodeName(flowNode.getNodeName());
        instance.setFlowStatus(setFlowStatus(flowNode.getNodeType(), flowUser.getSkipType()));
    }

    /**
     * 设置流程历史任务信息
     *
     * @param task
     * @param nextNodes
     * @return
     */
    private FlowHisTask setSkipInsHis(FlowTask task, List<FlowNode> nextNodes, FlowParams flowUser) {
        FlowHisTask insHis = new FlowHisTask();
        insHis.setId(task.getId());
        insHis.setInstanceId(task.getInstanceId());
        insHis.setDefinitionId(task.getDefinitionId());
        insHis.setNodeCode(task.getNodeCode());
        insHis.setNodeName(task.getNodeName());
        insHis.setNodeType(task.getNodeType());
        insHis.setPermissionFlag(task.getPermissionFlag());
        insHis.setTargetNodeCode(StreamUtils.join(nextNodes, FlowNode::getNodeCode));
        insHis.setTargetNodeName(StreamUtils.join(nextNodes, FlowNode::getNodeName));
        insHis.setFlowStatus(setFlowStatus(getNextNode(nextNodes).getNodeType(), flowUser.getSkipType()));
        insHis.setGateWayNode(task.getGateWayNode());
        insHis.setMessage(flowUser.getMessage());
        insHis.setCreateTime(new Date());
        insHis.setApprover(flowUser.getCreateBy());
        insHis.setTenantId(task.getTenantId());
        return insHis;
    }

    /**
     * 并行网关， 下个节点是结束节点，取结束节点类型，否则随便取第一条
     *
     * @param nextNodes
     * @return
     */
    private FlowNode getNextNode(List<FlowNode> nextNodes) {
        for (FlowNode nextNode : nextNodes) {
            if (NodeType.END.getKey().equals(nextNode.getNodeType())) {
                return nextNode;
            }
        }
        return nextNodes.get(0);
    }

    /**
     * 并行网关，流程状态,取哪个流程节点
     *
     * @param flowTasks
     * @return
     */
    private FlowTask getGateWayTask(List<FlowTask> flowTasks) {
        for (FlowTask flowTask : flowTasks) {
            if (NodeType.END.getKey().equals(flowTask.getNodeType())) {
                return flowTask;
            }
        }
        return flowTasks.get(0);
    }

    /**
     * @param nodeType 节点类型（开始节点、中间节点、结束节点）
     * @param skipType 流程条件
     */
    private Integer setFlowStatus(Integer nodeType, String skipType) {
        // 根据审批动作确定流程状态
        if (NodeType.START.getKey().equals(nodeType)) {
            return FlowStatus.TOBESUBMIT.getKey();
        } else if (NodeType.END.getKey().equals(nodeType)) {
            return FlowStatus.FINISHED.getKey();
        } else if (SkipType.REJECT.getKey().equals(skipType)) {
            return FlowStatus.REJECT.getKey();
        } else {
            return FlowStatus.APPROVAL.getKey();
        }
    }

    /**
     * 设置流程实例对象
     *
     * @param startNode
     * @param businessId
     * @return
     */
    private FlowInstance setStartInstance(FlowNode startNode, String businessId
            , FlowParams flowUser) {
        FlowInstance instance = new FlowInstance();
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
     * @param flowNode
     * @param instance
     * @return
     */
    private FlowTask addTask(FlowNode flowNode, FlowInstance instance, FlowParams flowUser) {
        FlowTask task = new FlowTask();
        Date date = new Date();
        task.setId(IdUtils.nextId());
        task.setDefinitionId(instance.getDefinitionId());
        task.setInstanceId(instance.getId());
        task.setNodeCode(flowNode.getNodeCode());
        task.setNodeName(flowNode.getNodeName());
        task.setNodeType(flowNode.getNodeType());
        task.setPermissionFlag(flowNode.getPermissionFlag());
        task.setApprover(flowUser.getCreateBy());
        task.setFlowStatus(setFlowStatus(flowNode.getNodeType(), flowUser.getSkipType()));
        task.setCreateTime(date);
        return task;
    }

    /**
     * 权限和条件校验
     *
     * @param skips
     * @return
     */
    private FlowSkip checkAuthAndCondition(FlowTask task, List<FlowSkip> skips, FlowParams flowUser) {
        if (CollUtil.isEmpty(skips)) {
            return null;
        }
        List<String> permissionFlags = flowUser.getPermissionFlag();
        List<FlowNode> nodes = FlowFactory.nodeService()
                .getByNodeCodes(Collections.singletonList(task.getNodeCode()), task.getDefinitionId());
        FlowNode node = CollUtil.getOne(nodes);
        AssertUtil.isTrue(ObjectUtil.isNull(node), ExceptionCons.NOT_NODE_DATA);

        AssertUtil.isTrue(StringUtils.isNotEmpty(node.getPermissionFlag()) && (CollUtil.isEmpty(permissionFlags)
                || !CollUtil.containsAny(permissionFlags, ArrayUtil.strToArrAy(node.getPermissionFlag(),
                ","))), ExceptionCons.NULL_ROLE_NODE);

        if (!NodeType.START.getKey().equals(task.getNodeType())) {
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
     * 根据流程id+当前流程结点编码获取与之直接关联(其为源结点)的结点。 definitionId:流程id nodeCode:当前流程状态
     * skipType:跳转条件,没有填写的话不做校验
     *
     * @param task
     * @param flowUser
     * @return
     */
    private List<FlowNode> getNextNode(FlowTask task, FlowParams flowUser) {
        AssertUtil.isNull(task.getDefinitionId(), ExceptionCons.NOT_DEFINITION_ID);
        AssertUtil.isBlank(task.getNodeCode(), ExceptionCons.LOST_NODE_CODE);
        List<FlowSkip> flowSkips = FlowFactory.skipService()
                .queryByDefAndCode(task.getDefinitionId(), task.getNodeCode());
        FlowSkip nextSkip = checkAuthAndCondition(task, flowSkips, flowUser);
        AssertUtil.isTrue(ObjectUtil.isNull(nextSkip), ExceptionCons.NULL_DEST_NODE);
        List<FlowNode> nodes = FlowFactory.nodeService()
                .getByNodeCodes(Collections.singletonList(nextSkip.getNextNodeCode()), task.getDefinitionId());
        AssertUtil.isTrue(CollUtil.isEmpty(nodes), ExceptionCons.NOT_NODE_DATA);
        AssertUtil.isTrue(nodes.size() > 1, "[" + nextSkip.getNextNodeCode() + "]" + ExceptionCons.SAME_NODE_CODE);
        AssertUtil.isTrue(NodeType.START.getKey().equals(nodes.get(0).getNodeType()), ExceptionCons.FRIST_FORBID_BACK);
        return nodes;

    }

    /**
     * 有且只能有一个开始结点
     *
     * @param nodes
     * @return
     */
    private FlowNode getFirstBetween(List<FlowNode> nodes) {
        for (FlowNode flowNode : nodes) {
            if (NodeType.START.getKey().equals(flowNode.getNodeType())) {
                List<FlowSkip> flowSkips = FlowFactory.skipService().queryByDefAndCode(flowNode.getDefinitionId(), flowNode.getNodeCode());
                FlowSkip flowSkip = flowSkips.get(0);
                return FlowFactory.nodeService().getByNodeCode(flowSkip.getNextNodeCode(), flowSkip.getDefinitionId());
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
