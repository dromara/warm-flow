package com.warm.flow.core.service;

import com.warm.flow.core.domain.dto.FlowParams;
import com.warm.flow.core.domain.entity.FlowInstance;
import com.warm.mybatis.core.service.IFlowService;

import java.util.List;

/**
 * 流程实例Service接口
 *
 * @author hh
 * @date 2023-03-29
 */
public interface IFlowInstanceService extends IFlowService<FlowInstance> {

    /**
     * 根据id集合进行查询 行锁
     *
     * @param ids
     * @return
     */
    List<FlowInstance> getByIdWithLock(List<Long> ids);

    /**
     * 开启流程
     *
     * @param businessId
     * @param flowUser
     * @return
     */
    FlowInstance startFlow(String businessId, FlowParams flowUser);

    /**
     * 开启流程
     *
     * @param businessIds
     * @param flowUser
     * @return
     */
    List<FlowInstance> startFlow(List<String> businessIds, FlowParams flowUser);

    /**
     * 流程跳转
     *
     * @param instanceId
     * @param conditionValue
     * @param flowUser
     * @return
     */
    FlowInstance skipFlow(Long instanceId, String conditionValue, FlowParams flowUser);

    /**
     * 流程跳转
     *
     * @param instanceIds
     * @param conditionValue
     * @param flowUser
     * @return
     */
    List<FlowInstance> skipFlow(List<Long> instanceIds, String conditionValue, FlowParams flowUser);

    /**
     * 流程跳转
     *
     * @param instanceId
     * @param conditionValue
     * @param message
     * @param flowUser
     * @return
     */
    FlowInstance skipFlow(Long instanceId, String conditionValue, String message, FlowParams flowUser);

    /**
     * 流程跳转
     *
     * @param instanceIds
     * @param conditionValue
     * @param message
     * @param flowUser
     * @return
     */
    List<FlowInstance> skipFlow(List<Long> instanceIds, String conditionValue, String message, FlowParams flowUser);

    /**
     * 流程跳转
     *
     * @param instanceIds
     * @return
     */
    boolean removeTask(List<Long> instanceIds);
}
