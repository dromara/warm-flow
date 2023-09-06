package com.warm.flow.core.service;

import com.warm.flow.core.domain.entity.FlowNode;
import com.warm.mybatis.core.service.IFlowService;

import java.util.List;

/**
 * 流程结点Service接口
 *
 * @author hh
 * @date 2023-03-29
 */
public interface IFlowNodeService extends IFlowService<FlowNode> {
    /**
     * 根据流程编码获取开启的唯一流程的流程结点集合
     *
     * @param flowCode
     * @return
     */
    List<FlowNode> getLastByFlowCode(String flowCode);

}
