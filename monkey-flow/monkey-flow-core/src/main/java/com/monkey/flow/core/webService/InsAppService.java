package com.monkey.flow.core.webService;

import com.monkey.flow.core.constant.FlowConstant;
import com.monkey.flow.core.domain.dto.FlowParams;
import com.monkey.flow.core.domain.entity.*;
import com.monkey.flow.core.enums.ApprovalAction;
import com.monkey.flow.core.enums.FlowStatus;
import com.monkey.flow.core.enums.NodeType;
import com.monkey.flow.core.exception.FlowException;
import com.monkey.flow.core.service.*;
import com.monkey.flow.core.utils.AssertUtil;
import com.monkey.tools.utils.ArrayUtil;
import com.monkey.tools.utils.CollUtil;
import com.monkey.tools.utils.IdUtils;
import com.monkey.tools.utils.StringUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author minliuhua
 * @description: 流程实例对外提供
 * @date: 2023/3/30 15:24
 */
public class InsAppService {

    @Resource
    private IFlowNodeService nodeService;

    @Resource
    private IFlowSkipService skipService;

    @Resource
    private IFlowInstanceService instanceService;

    @Resource
    private IFlowHisTaskService hisTaskService;

    @Resource
    private IFlowTaskService taskService;

    public IFlowInstanceService getService() {
        return instanceService;
    }

    @Transactional(rollbackFor = Exception.class)
    public FlowInstance startFlow(String businessId, FlowParams flowUser) {

        return toStartFlow(Collections.singletonList(businessId), flowUser).get(0);
    }

    @Transactional(rollbackFor = Exception.class)
    public List<FlowInstance> startFlow(List<String> businessIds, FlowParams flowUser) {
        return toStartFlow(businessIds, flowUser);
    }

    @Transactional(rollbackFor = Exception.class)
    public FlowInstance skipFlow(Long instanceId, String conditionValue, FlowParams flowUser) {
        return toSkipFlow(Collections.singletonList(instanceId), conditionValue, null, flowUser).get(0);
    }

    @Transactional(rollbackFor = Exception.class)
    public List<FlowInstance> skipFlow(List<Long> instanceIds, String conditionValue, FlowParams flowUser) {
        return toSkipFlow(instanceIds, conditionValue, null, flowUser);
    }


    @Transactional(rollbackFor = Exception.class)
    public FlowInstance skipFlow(Long instanceId, String conditionValue, String message, FlowParams flowUser) {
        return toSkipFlow(Collections.singletonList(instanceId), conditionValue, message, flowUser).get(0);
    }

    @Transactional(rollbackFor = Exception.class)
    public List<FlowInstance> skipFlow(List<Long> instanceIds, String conditionValue, String message, FlowParams flowUser) {
        return toSkipFlow(instanceIds, conditionValue, message, flowUser);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean removeTask(Long instanceId) {
        return removeTask(Collections.singletonList(instanceId));
    }


    @Transactional(rollbackFor = Exception.class)
    public boolean removeTask(List<Long> instanceIds) {
        return toRemoveTask(instanceIds);
    }

    /**
     * 根据开始的结点,业务id集合开启流程
     *
     * @param businessIds
     * @param flowUser
     * @return
     */
    private List<FlowInstance> toStartFlow(List<String> businessIds, FlowParams flowUser) {
        AssertUtil.isNull(flowUser.getFlowCode(), FlowConstant.NULL_FLOW_CODE);
        AssertUtil.isFalse(CollUtil.isEmpty(businessIds), FlowConstant.NULL_BUSINESS_ID);
        // 根据流程编码获取开启的唯一流程的流程结点集合
        List<FlowNode> nodes = nodeService.getLastByFlowCode(flowUser.getFlowCode());
        AssertUtil.isFalse(CollUtil.isEmpty(nodes), FlowConstant.NOT_PUBLISH_NODE);
        // 获取开始结点
        FlowNode startNode = getFirstNode(nodes);

        List<FlowInstance> instances = new ArrayList<>();
        List<FlowTask> taskList = new ArrayList<>();
        for (int i = 0; i < businessIds.size(); i++) {
            String businessId = businessIds.get(i);
            AssertUtil.isBlank(businessId, FlowConstant.NULL_BUSINESS_ID);
            // 设置流程实例对象
            FlowInstance instance = setStartInstance(startNode, businessId, flowUser);
            // 设置流程历史任务记录对象
            FlowTask task = setStartTask(startNode, instance, flowUser);
            instances.add(instance);
            taskList.add(task);
        }
        instanceService.saveBatch(instances);
        taskService.saveBatch(taskList);
        return instances;
    }

    private List<FlowInstance> toSkipFlow(List<Long> instanceIds, String conditionValue, String message
            , FlowParams flowUser) {
        AssertUtil.isFalse(message != null && message.length() > 500, FlowConstant.MSG_OVER_LENGTH);
        // 获取当前流程
        List<FlowInstance> instances = instanceService.getByIdWithLock(instanceIds);
        AssertUtil.isFalse(CollUtil.isEmpty(instances), FlowConstant.NOT_FOUNT_INSTANCE);
        AssertUtil.isFalse(instances.size() < instanceIds.size(), FlowConstant.LOST_FOUNT_INSTANCE);
        // TODO min 后续考虑并发问题，待办任务和实例表不同步
        // 获取待办任务
        List<FlowTask> taskList = taskService.getByInsIds(instanceIds);
        AssertUtil.isFalse(CollUtil.isEmpty(taskList), FlowConstant.NOT_FOUNT_TASK );
        AssertUtil.isFalse(taskList.size() < instanceIds.size(), FlowConstant.LOST_FOUNT_TASK );
        // 校验这些流程的流程状态是否相同，只有相同的情况下，下面才好做统一处理
        checkSameStatus(taskList);
        Map<Long, FlowTask> taskMap = taskList.stream()
                .collect(Collectors.toMap(FlowTask::getInstanceId, flowTask -> flowTask));

        List<FlowHisTask> insHisList = new ArrayList<>();
        // 获取关联的结点
        FlowNode nextNode = getNextNode(taskList.get(0), conditionValue, flowUser);
        for (FlowInstance instance: instances) {
            // 更新流程实例信息
            setSkipInstance(nextNode, instance, conditionValue, flowUser);
            FlowTask task = taskMap.get(instance.getId());
            // 设置流程历史任务信息
            FlowHisTask insHis = setSkipInsHis(conditionValue, message, task, nextNode, flowUser);
            // 更新待办任务
            setSkipTask(nextNode, task, conditionValue, flowUser);

            insHisList.add(insHis);
        }

        hisTaskService.saveBatch(insHisList);
        taskService.updateBatch(taskList);
        instanceService.updateBatch(instances);
        return instances;
    }

    /**
     * 设置流程实例信息
     * @param nextNode
     * @param instance
     */
    private void setSkipInstance(FlowNode nextNode, FlowInstance instance, String conditionValue, FlowParams flowUser) {
        instance.setNodeType(nextNode.getNodeType());
        instance.setFlowStatus(setFlowStatus(nextNode.getNodeType(), conditionValue, false));
        instance.setUpdateTime(new Date());
    }

    /**
     * 设置待办任务信息
     * @param nextNode
     * @param task
     */
    private void setSkipTask(FlowNode nextNode, FlowTask task, String conditionValue, FlowParams flowUser) {
        task.setNodeCode(nextNode.getNodeCode());
        task.setNodeName(nextNode.getNodeName());
        task.setNodeType(nextNode.getNodeType());
        task.setApprover(flowUser.getCreateBy());
        task.setPermissionFlag(nextNode.getPermissionFlag());
        task.setFlowStatus(setFlowStatus(nextNode.getNodeType(), conditionValue, false));
        task.setUpdateTime(new Date());
        task.setTenantId(flowUser.getTenantId());
    }

    /**
     * 设置流程历史任务信息
     * @param conditionValue
     * @param message
     * @param task
     * @param nextNode
     * @return
     */
    private FlowHisTask setSkipInsHis(String conditionValue, String message, FlowTask task
            , FlowNode nextNode, FlowParams flowUser) {
        FlowHisTask insHis = new FlowHisTask();
        insHis.setId(IdUtils.nextId());
        insHis.setInstanceId(task.getInstanceId());
        insHis.setDefinitionId(task.getDefinitionId());
        insHis.setNodeCode(task.getNodeCode());
        insHis.setNodeName(task.getNodeName());
        insHis.setNodeType(task.getNodeType());
        insHis.setPermissionFlag(task.getPermissionFlag());
        insHis.setTargetNodeCode(nextNode.getNodeCode());
        insHis.setTargetNodeName(nextNode.getNodeName());
        insHis.setConditionValue(conditionValue);
        insHis.setFlowStatus(setFlowStatus(nextNode.getNodeType(), conditionValue, true));
        insHis.setMessage(message);
        insHis.setCreateTime(new Date());
        insHis.setApprover(flowUser.getCreateBy());
        insHis.setTenantId(task.getTenantId());
        return insHis;
    }

    /**
     *
     * @param nodeType 节点类型（开始节点、中间节点、结束节点）
     * @param conditionValue 流程条件
     * @param type 实体类型（历史任务实体为true）
     */
    private Integer setFlowStatus(Integer nodeType, String conditionValue, boolean type) {
        // 根据审批动作确定流程状态
        if (NodeType.END.getKey().equals(nodeType))
        {
            return FlowStatus.FINISHED.getKey();
        } else if (ApprovalAction.REJECT.getKey().equals(conditionValue)) {
            return FlowStatus.REJECT.getKey();
        } else if (type) {
            return FlowStatus.PASS.getKey();
        } else {
            return FlowStatus.APPROVAL.getKey();
        }
    }

    /**
     * 设置流程实例对象
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
        // 关联业务id,起始后面可以不用到业务id,传业务id目前来看只是为了批量创建流程的时候能创建出有区别化的流程,也是为了后期需要用到businessId。
        instance.setNodeType(startNode.getNodeType());
        instance.setFlowStatus(FlowStatus.TOBESUBMIT.getKey());
        instance.setCreateTime(now);
        instance.setUpdateTime(now);
        instance.setCreateBy(flowUser.getCreateBy());
        instance.setExt(flowUser.getExt());
        return instance;
    }

    /**
     * 设置流程待办任务对象
     * @param startNode
     * @param instance
     * @return
     */
    private FlowTask setStartTask(FlowNode startNode, FlowInstance instance, FlowParams flowUser) {
        FlowTask task = new FlowTask();
        Date date = new Date();
        task.setId(IdUtils.nextId());
        task.setDefinitionId(instance.getDefinitionId());
        task.setInstanceId(instance.getId());
        task.setNodeCode(startNode.getNodeCode());
        task.setNodeName(startNode.getNodeName());
        task.setNodeType(startNode.getNodeType());
        task.setPermissionFlag(startNode.getPermissionFlag());
        task.setApprover(flowUser.getCreateBy());
        task.setFlowStatus(FlowStatus.TOBESUBMIT.getKey());
        task.setCreateTime(date);
        task.setUpdateTime(date);
        task.setTenantId(flowUser.getTenantId());
        return task;
    }

    /**
     * 批量流程校验,如批量处理一批流程,其中有些流程在a状态，有些在b状态，这样就会抛MUL_FROM_STATUS异常。
     * 这种情况调整为根据流程状态进行分批批量跳转就可以了
     *
     * @param taskList
     */
    private void checkSameStatus(List<FlowTask> taskList) {
        Map<String, List<FlowTask>> groupMap = taskList.stream().collect(Collectors.groupingBy(t -> t.getDefinitionId() + "_" + t.getNodeCode()));
        if (groupMap.size() > 1) {
            throw new FlowException(FlowConstant.MUL_FROM_STATUS);
        }
    }

    /**
     * 权限和条件校验
     *
     * @param skips
     * @param conditionValue
     * @return
     */
    private FlowSkip checkAuthAndCondition(FlowTask task, List<FlowSkip> skips, String conditionValue
            , FlowParams flowUser) {
        if (CollUtil.isEmpty(skips)) {
            return null;
        }
        List<String> permissionFlags = flowUser.getPermissionFlag();
        FlowNode flowNode = new FlowNode();
        flowNode.setDefinitionId(task.getDefinitionId());
        flowNode.setNodeCode(task.getNodeCode());
        FlowNode node = nodeService.getOne(flowNode);

        AssertUtil.isFalse(StringUtils.isNotEmpty(node.getPermissionFlag()) && (CollUtil.isEmpty(permissionFlags)
                || !CollUtil.containsAny(permissionFlags, ArrayUtil.strToArrAy(node.getPermissionFlag(),
                ","))), FlowConstant.NULL_ROLE_NODE);

        if (!NodeType.START.getKey().equals(task.getNodeType())) {
            if (StringUtils.isEmpty(conditionValue)) {
                skips = skips.stream().filter(t -> StringUtils.isEmpty(t.getConditionValue())).collect(Collectors.toList());
            } else {
                skips = skips.stream().filter(t -> conditionValue.equals(t.getConditionValue())).collect(Collectors.toList());
            }
        }
        AssertUtil.isFalse(skips.isEmpty(), FlowConstant.NULL_CONDITIONVALUE_NODE);
        // 第一个结点
        return skips.get(0);
    }

    /**
     * 根据流程id+当前流程结点编码获取与之直接关联(其为源结点)的结点。 definitionId:流程id nodeCode:当前流程状态
     * conditionValue:跳转条件,没有填写的话不做校验
     *
     * @param task
     * @param conditionValue
     * @param flowUser
     * @return
     */
    private FlowNode getNextNode(FlowTask task, String conditionValue
            , FlowParams flowUser) {
        AssertUtil.isNull(task.getDefinitionId(), FlowConstant.NOT_DEFINITION_ID);
        AssertUtil.isBlank(task.getNodeCode(), FlowConstant.LOST_NODE_CODE);
        FlowSkip skipCondition = new FlowSkip();
        skipCondition.setDefinitionId(task.getDefinitionId());
        skipCondition.setNowNodeCode(task.getNodeCode());
        List<FlowSkip> flowSkips = skipService.list(skipCondition);
        FlowSkip nextSkip = checkAuthAndCondition(task, flowSkips, conditionValue, flowUser);
        AssertUtil.isFalse(nextSkip == null, FlowConstant.NULL_DEST_NODE);
        FlowNode query = new FlowNode();
        query.setDefinitionId(task.getDefinitionId());
        query.setNodeCode(nextSkip.getNextNodeCode());
        List<FlowNode> nodes = nodeService.list(query);
        AssertUtil.isFalse(nodes.size() == 0, FlowConstant.NOT_NODE_DATA);
        AssertUtil.isFalse(nodes.size() > 1, "[" + nextSkip.getNextNodeCode() + "]" + FlowConstant.SAME_NODE_CODE);
        return nodes.get(0);

    }

    /**
     * 有且只能有一个开始结点
     *
     * @param nodes
     * @return
     */
    private FlowNode getFirstNode(List<FlowNode> nodes) {
        for (int i = 0; i < nodes.size(); i++) {
            if (NodeType.START.getKey().equals(nodes.get(i).getNodeType())) {
                return nodes.get(i);
            }
        }
        throw new FlowException(FlowConstant.LOST_START_NODE);
    }

    private boolean toRemoveTask(List<Long> instanceIds) {
        AssertUtil.isFalse(CollUtil.isEmpty(instanceIds), FlowConstant.NULL_INSTANCE_ID);
        boolean success = taskService.deleteByInsIds(instanceIds);
        if(success)
        {
            hisTaskService.deleteByInsIds(instanceIds);
            return instanceService.removeByIds(instanceIds);
        }
        return false;
    }
}
