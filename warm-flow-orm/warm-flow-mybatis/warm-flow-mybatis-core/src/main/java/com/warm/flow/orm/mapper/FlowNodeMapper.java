package com.warm.flow.orm.mapper;

import com.warm.flow.orm.entity.FlowNode;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;


/**
 * 流程节点Mapper接口
 *
 * @author warm
 * @date 2023-03-29
 */
public interface FlowNodeMapper extends WarmMapper<FlowNode> {

    List<FlowNode> getByNodeCodes(@Param("nodeCodes") List<String> nodeCodes, @Param("definitionId") Long definitionId
            , @Param("entity") FlowNode entity);

    /**
     * 批量删除流程节点
     *
     * @param defIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteNodeByDefIds(@Param("defIds") Collection<? extends Serializable> defIds, @Param("entity") FlowNode entity);

    /**
     * 逻辑删除
     *
     * @param defIds 需要删除的数据主键集合
     * @return 结果
     */
    int updateNodeByDefIdsLogic(@Param("defIds") Collection<? extends Serializable> defIds, @Param("entity") FlowNode entity
            , @Param("logicDeleteValue") String logicDeleteValue
            , @Param("logicNotDeleteValue") String logicNotDeleteValue);
}
