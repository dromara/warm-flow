package com.warm.flow.core.dao;

import com.warm.flow.core.entity.Node;

import java.util.List;


/**
 * 流程节点Mapper接口
 *
 * @author warm
 * @date 2023-03-29
 */
public interface FlowNodeDao extends WarmDao<Node> {

    /**
     * 跟进流程编码获取流程节点集合
     *
     * @param flowCode
     * @return
     */
    List<Node> getByFlowCode(String flowCode);

    List<Node> getByNodeCodes(List<String> nodeCodes, Long definitionId);

}
