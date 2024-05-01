package com.warm.flow.core.dao;

import com.warm.flow.core.entity.Node;

import java.util.List;


/**
 * 流程节点Mapper接口
 *
 * @author warm
 * @date 2023-03-29
 */
public interface FlowNodeDao<T extends Node> extends WarmDao<T> {

    /**
     * 跟进流程编码获取流程节点集合
     *
     * @param flowCode
     * @return
     */
    List<T> getByFlowCode(String flowCode);

    List<T> getByNodeCodes(List<String> nodeCodes, Long definitionId);

}
