package com.warm.flow.core.service;

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

}
