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

    /**
     * 根据流程编码获取流程节点集合
     *
     * @param flowCode
     * @return
     */
    List<FlowNode> getByFlowCode(String flowCode);

    List<FlowNode> getByNodeCodes(@Param("nodeCodes") List<String> nodeCodes, @Param("definitionId") Long definitionId);

    /**
     * 批量删除流程节点
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteNodeByDefIds(Collection<? extends Serializable> ids);
}
