package com.warm.flow.core.service;

import com.warm.flow.core.dto.FlowParams;
import com.warm.flow.core.entity.Instance;
import com.warm.flow.core.orm.service.IWarmService;

import java.util.List;

/**
 * 流程实例Service接口
 *
 * @author warm
 * @date 2023-03-29
 */
public interface InsService extends IWarmService<Instance> {

    /**
     * 根据id集合进行查询 行锁
     *
     * @param ids
     * @return
     */
    List<Instance> getByIdWithLock(List<Long> ids);

    /**
     * 根据开始的节点,业务id集合开启流程
     *
     * @param businessId
     * @param flowUser
     * @return
     */
    Instance start(String businessId, FlowParams flowUser);


    /**
     * 根据实例id，流程跳转，一般是开始节点后第一个节点，用来提交申请，此时不可有同时两个代办任务
     *
     * @param instanceId
     * @param flowUser
     * @return
     */
    Instance skipByInsId(Long instanceId, FlowParams flowUser);

    /**
     * 根据任务id，流程跳转
     *
     * @param taskId
     * @param flowUser
     * @return
     */
    Instance skip(Long taskId, FlowParams flowUser);

    /**
     * 根据实例id，删除流程
     *
     * @param instanceIds
     * @return
     */
    boolean remove(List<Long> instanceIds);

}
