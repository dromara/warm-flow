package com.monkey.flow.core.webService;

import com.monkey.flow.core.InsAppService;
import com.monkey.flow.core.constant.FlowConstant;
import com.monkey.flow.core.domain.dto.FlowParams;
import com.monkey.flow.core.domain.entity.FlowHisTask;
import com.monkey.flow.core.domain.entity.FlowInstance;
import com.monkey.flow.core.domain.entity.FlowNode;
import com.monkey.flow.core.domain.entity.FlowSkip;
import com.monkey.flow.core.enums.ApprovalAction;
import com.monkey.flow.core.enums.FlowStatus;
import com.monkey.flow.core.enums.NodeType;
import com.monkey.flow.core.exception.FlowException;
import com.monkey.flow.core.service.*;
import com.monkey.flow.core.utils.AssertUtil;
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
public class InsAppServiceImpl implements InsAppService {

    @Resource
    private IFlowHisTaskService hisTaskService;

    @Resource
    private IFlowNodeService nodeService;

    @Resource
    private IFlowSkipService skipService;

    @Resource
    private IFlowInstanceService instanceService;

    @Resource
    private IFlowDefinitionService definitionService;

    @Override
    public IFlowInstanceService getService() {
        return instanceService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FlowInstance startFlow(String businessId, FlowParams flowUser) {

        return toStartFlow(Arrays.asList(businessId), flowUser).get(0);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<FlowInstance> startFlow(List<String> businessIds, FlowParams flowUser) {
        return toStartFlow(businessIds, flowUser);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FlowInstance skipFlow(Long instanceId, String conditionValue, FlowParams flowUser) {
        return toSkipFlow(Arrays.asList(instanceId), conditionValue, null, flowUser).get(0);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<FlowInstance> skipFlow(List<Long> instanceIds, String conditionValue, FlowParams flowUser) {
        return toSkipFlow(instanceIds, conditionValue, null, flowUser);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public FlowInstance skipFlow(Long instanceId, String conditionValue, String message, FlowParams flowUser) {
        return toSkipFlow(Arrays.asList(instanceId), conditionValue, message, flowUser).get(0);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<FlowInstance> skipFlow(List<Long> instanceIds, String conditionValue, String message, FlowParams flowUser) {
        return toSkipFlow(instanceIds, conditionValue, message, flowUser);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeTask(Long instanceId) {
        return removeTask(Arrays.asList(instanceId));
    }


    @Override
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
        List<FlowNode> nodes = nodeService.queryNewVersionFlowNodeByFlowCode(flowUser.getFlowCode());
        AssertUtil.isFalse(CollUtil.isEmpty(nodes), FlowConstant.NOT_PUBLISH_NODE);
        // 获取开始结点
        FlowNode startNode = getFirstNode(nodes);

        List<FlowInstance> instances = new ArrayList<>();
        List<FlowHisTask> insHisList = new ArrayList<>();
        for (int i = 0; i < businessIds.size(); i++) {
            String businessId = businessIds.get(i);
            AssertUtil.isBlank(businessId, FlowConstant.NULL_BUSINESS_ID);
            // 设置流程实例对象
            FlowInstance instance = setStartInstance(startNode, businessId, flowUser);
            // 设置流程历史任务记录对象
            FlowHisTask insHis = setStartInsHis(startNode, instance);
            instances.add(instance);
            insHisList.add(insHis);
        }
        instanceService.batchInsert(instances);
        hisTaskService.batchInsert(insHisList);
        return instances;
    }

    private List<FlowInstance> toSkipFlow(List<Long> instanceIds, String conditionValue, String message
            , FlowParams flowUser) {
        AssertUtil.isFalse(message != null && message.length() > 500, FlowConstant.MSG_OVER_LENGTH);
        // 获取当前流程
        List<FlowInstance> instances = instanceService.queryByidWithUpdateLock(instanceIds);
        AssertUtil.isFalse(instances == null || instances.size() == 0, FlowConstant.NOT_FOUNT_INSTANCE);
        AssertUtil.isFalse(instances.size() < instanceIds.size(), FlowConstant.LOST_FOUNT_FLOW);
        // 校验这些流程的流程状态是否相同，只有相同的情况下，下面才好做统一处理
        checkSameStatus(instances);
        List<FlowHisTask> insHisList = new ArrayList<>();

        // 获取关联的结点
        FlowNode nextNode = getNextNode(instances.get(0), conditionValue, flowUser);
        for (int i = 0; i < instances.size(); i++) {
            FlowInstance instance = instances.get(i);
            // 设置流程历史任务信息
            FlowHisTask insHis = setSkipInsHis(conditionValue, message, instance, nextNode, flowUser);
            insHisList.add(insHis);

            // 设置流程实例信息
            setSkipInstance(nextNode, instance, conditionValue, flowUser);
        }

        hisTaskService.batchInsert(insHisList);
        instanceService.batchUpdate(instances);
        return instances;
    }

    /**
     * 设置流程实例信息
     * @param nextNode
     * @param instance
     */
    private void setSkipInstance(FlowNode nextNode, FlowInstance instance, String conditionValue, FlowParams flowUser) {
        instance.setUpdateTime(new Date());
        instance.setNodeCode(nextNode.getNodeCode());
        instance.setNodeName(nextNode.getNodeName());
        instance.setNodeType(nextNode.getNodeType());
        instance.setUserCode(flowUser.getCreateBy());
        instance.setFlowStatus(setFlowStatus(nextNode.getNodeType(), conditionValue));
    }

    private Integer setFlowStatus(Integer nodeType, String conditionValue) {
        // 根据审批动作确定流程状态
        if (NodeType.END.getKey().equals(nodeType))
        {
            return FlowStatus.FINISHED.getKey();
        } else if (ApprovalAction.REJECT.getKey().equals(conditionValue) ) {
            return FlowStatus.REJECT.getKey();
        } else {
            return FlowStatus.APPROVAL.getKey();
        }
    }

    /**
     * 设置流程历史任务信息
     * @param conditionValue
     * @param message
     * @param instance
     * @param nextNode
     * @return
     */
    private FlowHisTask setSkipInsHis(String conditionValue, String message, FlowInstance instance
            , FlowNode nextNode, FlowParams flowUser) {
        FlowHisTask insHis = new FlowHisTask();
        insHis.setId(IdUtils.nextId());
        insHis.setInstanceId(instance.getId());
        insHis.setDefinitionId(instance.getDefinitionId());
        insHis.setNodeFrom(instance.getNodeCode());
        insHis.setNodeFromName(instance.getNodeName());
        insHis.setNodeType(instance.getNodeType());
        insHis.setNodeTo(nextNode.getNodeCode());
        insHis.setNodeToName(nextNode.getNodeName());
        insHis.setConditionValue(conditionValue);
        insHis.setFlowStatus(setFlowStatus(nextNode.getNodeType(), conditionValue));
        insHis.setMessage(message);
        insHis.setCreateTime(new Date());
        insHis.setUserCode(flowUser.getCreateBy());
        return insHis;
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
        instance.setNodeCode(startNode.getNodeCode());
        instance.setNodeName(startNode.getNodeName());
        instance.setNodeType(startNode.getNodeType());
        instance.setFlowStatus(FlowStatus.TOBESUBMIT.getKey());
        instance.setFlowVersion(startNode.getVersion());
        instance.setCreateTime(now);
        instance.setUpdateTime(now);
        instance.setCreateBy(flowUser.getCreateBy());
        instance.setUserCode(flowUser.getCreateBy());
        instance.setExt(flowUser.getExt());
        return instance;
    }

    /**
     * 设置流程历史任务记录对象
     * @param startNode
     * @param instance
     * @return
     */
    private FlowHisTask setStartInsHis(FlowNode startNode, FlowInstance instance) {
        FlowHisTask insHis = new FlowHisTask();
        insHis.setId(IdUtils.nextId());
        insHis.setInstanceId(instance.getId());
        insHis.setDefinitionId(instance.getDefinitionId());
        insHis.setNodeTo(startNode.getNodeCode());
        insHis.setNodeToName(startNode.getNodeName());
        insHis.setNodeType(startNode.getNodeType());
        insHis.setFlowStatus(instance.getFlowStatus());
        insHis.setCreateTime(new Date());
        return insHis;
    }

    /**
     * 批量流程校验,如批量处理一批流程,其中有些流程在a状态，有些在b状态，这样就会抛MUL_FROM_STATUS异常。
     * 这种情况调整为根据流程状态进行分批批量跳转就可以了
     *
     * @param instances
     */
    private void checkSameStatus(List<FlowInstance> instances) {
        Map<String, List<FlowInstance>> groupMap = instances.stream().collect(Collectors.groupingBy(t -> t.getDefinitionId() + "_" + t.getNodeCode()));
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
    private FlowSkip checkAuthAndCondition(FlowInstance instance, List<FlowSkip> skips, String conditionValue
            , FlowParams flowUser) {
        if (skips == null || skips.size() == 0) {
            return null;
        }
        List<String> roleList = flowUser.getPermissionFlag();
        // AssertUtil.isFalse(roleList == null || roleList.size() == 0, FlowConstant.NOT_ROLE_PERMISSIONS);
        FlowNode flowNode = new FlowNode();
        flowNode.setDefinitionId(instance.getDefinitionId());
        flowNode.setNodeCode(instance.getNodeCode());
        FlowNode node = nodeService.selectOne(flowNode);

        AssertUtil.isFalse(StringUtils.isNotEmpty(node.getPermissionFlag()) && (CollUtil.isEmpty(roleList)
                || !CollUtil.containsAny(roleList, StringUtils.strToArrAy(node.getPermissionFlag(), ","))), FlowConstant.NULL_ROLE_NODE);

        if (!NodeType.START.getKey().equals(instance.getNodeType())) {
            if (StringUtils.isEmpty(conditionValue)) {
                skips = skips.stream().filter(t -> StringUtils.isEmpty(t.getConditionValue())).collect(Collectors.toList());
            } else {
                skips = skips.stream().filter(t -> conditionValue.equals(t.getConditionValue())).collect(Collectors.toList());
            }
        }
        AssertUtil.isFalse(skips.size() == 0, FlowConstant.NULL_CONDITIONVALUE_NODE);
        // 第一个结点
        return skips.get(0);
    }

    /**
     * 根据流程id+当前流程结点编码获取与之直接关联(其为源结点)的结点。 definitionId:流程id nodeCode:当前流程状态
     * conditionValue:跳转条件,没有填写的话不做校验
     *
     * @param instance
     * @param conditionValue
     * @param flowUser
     * @return
     */
    private FlowNode getNextNode(FlowInstance instance, String conditionValue
            , FlowParams flowUser) {
        AssertUtil.isNull(instance.getDefinitionId(), FlowConstant.NOT_DEFINITION_ID);
        AssertUtil.isBlank(instance.getNodeCode(), FlowConstant.LOST_NODE_CODE);
        FlowSkip skipCondition = new FlowSkip();
        skipCondition.setDefinitionId(instance.getDefinitionId());
        skipCondition.setNowNodeCode(instance.getNodeCode());
        List<FlowSkip> flowSkips = skipService.selectList(skipCondition);
        FlowSkip nextSkip = checkAuthAndCondition(instance, flowSkips, conditionValue, flowUser);
        AssertUtil.isFalse(nextSkip == null, FlowConstant.NULL_DEST_NODE);
        FlowNode query = new FlowNode();
        query.setDefinitionId(instance.getDefinitionId());
        query.setNodeCode(nextSkip.getNextNodeCode());
        List<FlowNode> nodes = nodeService.selectList(query);
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
        boolean success = hisTaskService.deleteByInstanceIds(instanceIds);
        if(success)
        {
            return instanceService.deleteByIds(instanceIds);
        }
        return false;
    }
}
