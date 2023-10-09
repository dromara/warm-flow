package com.warm.flow.core.service;

import com.warm.flow.core.domain.dto.FlowParams;
import com.warm.flow.core.domain.entity.FlowInstance;
import com.warm.mybatis.core.service.IWarmService;

import java.util.List;

/**
 * 流程实例Service接口
 *
 * @author warm
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
     * 根据开始的节点,业务id集合开启流程
     *
     * @param businessId
     * @param flowUser
     * @return
     */
    FlowInstance start(String businessId, FlowParams flowUser);


    /**
     * 根据实例id，流程跳转，一般是开始节点后第一个节点，用来提交申请，此时不可有同时两个代办任务
     *
     * @param instanceId
     * @param flowUser
     * @return
     */
    FlowInstance skipByInsId(Long instanceId, FlowParams flowUser);

    /**
     * 根据任务id，流程跳转
     *
     * @param taskId
     * @param flowUser
     * @return
     */
    FlowInstance skip(Long taskId, FlowParams flowUser);

    /**
     * 根据实例id，删除流程
     *
     * @param instanceIds
     * @return
     */
    boolean remove(List<Long> instanceIds);
}
