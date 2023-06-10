package com.monkey.flow.core;

import com.monkey.flow.core.domain.dto.FlowParams;
import com.monkey.flow.core.domain.entity.FlowInstance;
import com.monkey.flow.core.service.IFlowInstanceService;

import java.util.List;

/**
 * @author minliuhua
 * @description: 流程实例对外提供
 * @date: 2023/3/30 15:24
 */
public interface InsAppService {

    /**
     * 获取流程实例服务
     * @return
     */
    public IFlowInstanceService getService();

    /**
     * 开启流程
     * @param businessId 业务id
     * @param flowUser 用户信息
     * @return 返回流程实例
     */
    public FlowInstance startFlow(String businessId, FlowParams flowUser);

    /**
     * 开启流程
     * @param businessIds 业务ids
     * @param flowUser 用户信息
     * @return 返回流程实例集合
     */
    public List<FlowInstance> startFlow(List<String> businessIds, FlowParams flowUser);

    /**
     * 流程流转,根据conditionValue跳转到指定结点，
     *
     * @param instanceId         流程实例id
     * @param conditionValue 跳转条件 第一个结点条件可以为空
     * @param flowUser 用户信息
     * @return 流程流转后流程实例
     */
    public FlowInstance skipFlow(Long instanceId, String conditionValue, FlowParams flowUser);

    /**
     * 流程流转,根据conditionValue跳转到指定结点，
     *
     * @param instanceIds         流程实例ids
     * @param conditionValue 跳转条件 第一个结点条件可以为空
     * @param flowUser 用户信息
     * @return 流程流转后流程实例集合
     */
    public List<FlowInstance> skipFlow(List<Long> instanceIds, String conditionValue, FlowParams flowUser);

    /**
     * 流程流转,根据conditionValue跳转到指定结点，
     *
     * @param instanceId         流程实例id
     * @param conditionValue 跳转条件 第一个结点条件可以为空
     * @param message 消息
     * @param flowUser 用户信息
     * @return 流程流转后流程实例
     */
    public FlowInstance skipFlow(Long instanceId, String conditionValue, String message, FlowParams flowUser);

    /**
     * 流程流转,根据conditionValue跳转到指定结点，
     *
     * @param instanceIds         流程实例ids
     * @param conditionValue 跳转条件 第一个结点条件可以为空
     * @param message 消息
     * @param flowUser 用户信息
     * @return 流程流转后流程实例集合
     */
    public List<FlowInstance> skipFlow(List<Long> instanceIds, String conditionValue, String message, FlowParams flowUser);

    /**
     * 删除流程
     * @param instanceId
     * @return
     */
    public boolean removeTask(Long instanceId);

    /**
     * 删除流程
     * @param instanceIds
     * @return
     */
    public boolean removeTask(List<Long> instanceIds);

}
