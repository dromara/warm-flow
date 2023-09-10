package com.warm.flow.core.service;

import com.warm.flow.core.domain.dto.FlowParams;
import com.warm.flow.core.domain.entity.FlowInstance;
import com.warm.mybatis.core.service.IWarmService;

import java.util.Collections;
import java.util.List;

/**
 * 流程实例Service接口
 *
 * @author hh
 * @date 2023-03-29
 */
public interface InsService extends IWarmService<FlowInstance> {

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
     * @param flowUser
     * @return
     */
    FlowInstance skipFlow(Long instanceId, FlowParams flowUser);

    /**
     * 流程跳转
     *
     * @param instanceIds
     * @param flowUser
     * @return
     */
    List<FlowInstance> skipFlow(List<Long> instanceIds, FlowParams flowUser);

    /**
     * 流程跳转
     *
     * @param instanceId
     * @return
     */
    boolean removeTask(Long instanceId);

    /**
     * 流程跳转
     *
     * @param instanceIds
     * @return
     */
    boolean removeTask(List<Long> instanceIds);
}
