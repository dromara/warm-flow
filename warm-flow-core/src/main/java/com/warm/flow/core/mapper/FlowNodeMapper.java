package com.warm.flow.core.mapper;

import com.warm.flow.core.domain.entity.FlowNode;
import com.warm.mybatis.core.mapper.WarmMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * 流程结点Mapper接口
 *
 * @author warm
 * @date 2023-03-29
 */
public interface FlowNodeMapper extends WarmMapper<FlowNode> {

    /**
     * 跟进流程编码获取流程节点集合
     *
     * @param flowCode
     * @return
     */
    List<FlowNode> getByFlowCode(String flowCode);

    List<FlowNode> getByNodeCodes(@Param("nodeCodes") List<String> nodeCodes, @Param("definitionId") Long definitionId);

    /**
     * 根据ids批量删除
     *
     * @param definitionId 需要删除的数据主键集合
     * @return 结果
     */
    int deleteByDefinitionId(Long definitionId);

}
