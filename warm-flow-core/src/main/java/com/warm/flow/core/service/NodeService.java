package com.warm.flow.core.service;

import com.warm.flow.core.domain.entity.FlowNode;
import com.warm.mybatis.core.service.IWarmService;

import java.util.List;

/**
 * 流程结点Service接口
 *
 * @author warm
 * @date 2023-03-29
 */
public interface NodeService extends IWarmService<FlowNode> {
    /**
     * 跟进流程编码获取流程节点集合
     *
     * @param flowCode
     * @return
     */
    List<FlowNode> getByFlowCode(String flowCode);

    /**
     * 根据流程编码获取开启的唯一流程的流程结点集合
     *
     * @param nodeCodes
     * @return
     */
    List<FlowNode> getByNodeCodes(List<String> nodeCodes, Long definitionId);

    /**
     * 根据流程编码获取开启的唯一流程的流程结点
     *
     * @param nodeCode
     * @return
     */
    FlowNode getByNodeCode(String nodeCode, Long definitionId);
}
