package com.warm.flow.core.dao;

import com.warm.flow.core.entity.Node;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;


/**
 * 流程节点Mapper接口
 *
 * @author warm
 * @date 2023-03-29
 */
public interface FlowNodeDao<T extends Node> extends WarmDao<T> {

    List<T> getByNodeCodes(List<String> nodeCodes, Long definitionId);

    /**
     * 批量删除流程节点
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteNodeByDefIds(Collection<? extends Serializable> ids);
}
