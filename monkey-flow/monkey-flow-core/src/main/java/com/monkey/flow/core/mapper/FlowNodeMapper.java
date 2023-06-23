package com.monkey.flow.core.mapper;

import com.monkey.flow.core.domain.entity.FlowNode;
import com.monkey.mybatis.core.mapper.FlowMapper;

import java.util.List;


/**
 * 流程结点Mapper接口
 *
 * @author hh
 * @date 2023-03-29
 */
public interface FlowNodeMapper extends FlowMapper<FlowNode> {
    List<FlowNode> getLastByFlowCode(String flowCode);

    List<FlowNode> getByFlowCodeAndVersion(String flowCode, String version);

    /**
     * 根据ids批量删除
     *
     * @param definitionId 需要删除的数据主键集合
     * @return 结果
     */
    int deleteByDefinitionId(Long definitionId);

}
