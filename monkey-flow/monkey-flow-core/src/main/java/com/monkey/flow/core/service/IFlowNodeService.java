package com.monkey.flow.core.service;

import com.monkey.flow.core.domain.entity.FlowNode;
import com.monkey.mybatis.core.service.IFlowBaseService;

import java.util.List;

/**
 * 流程结点Service接口
 *
 * @author hh
 * @date 2023-03-29
 */
public interface IFlowNodeService extends IFlowBaseService<FlowNode> {
    /**
     * 根据流程编码获取开启的唯一流程的流程结点集合
     *
     * @param flowCode
     * @return
     */
    List<FlowNode> getLastByFlowCode(String flowCode);

}
