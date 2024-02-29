package com.warm.flow.core.service;

import com.warm.flow.core.entity.Node;
import com.warm.flow.core.orm.service.IWarmService;

import java.util.List;

/**
 * 流程节点Service接口
 *
 * @author warm
 * @date 2023-03-29
 */
public interface NodeService extends IWarmService<Node> {

    /**
     * 跟进流程编码获取流程节点集合
     *
     * @param flowCode
     * @return
     */
    List<Node> getByFlowCode(String flowCode);

    /**
     * 根据流程编码获取开启的唯一流程的流程节点集合
     *
     * @param nodeCodes
     * @return
     */
    List<Node> getByNodeCodes(List<String> nodeCodes, Long definitionId);

    /**
     * 根据流程编码获取开启的唯一流程的流程节点
     *
     * @param nodeCode
     * @return
     */
    Node getByNodeCode(String nodeCode, Long definitionId);

}