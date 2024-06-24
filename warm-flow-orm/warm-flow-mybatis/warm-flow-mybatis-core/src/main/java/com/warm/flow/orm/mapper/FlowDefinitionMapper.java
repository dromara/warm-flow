package com.warm.flow.orm.mapper;

import com.warm.flow.orm.entity.FlowDefinition;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 流程定义Mapper接口
 *
 * @author warm
 * @date 2023-03-29
 */
public interface FlowDefinitionMapper extends WarmMapper<FlowDefinition> {


    List<FlowDefinition> queryByCodeList(@Param("flowCodeList") List<String> flowCodeList, @Param("entity") FlowDefinition entity);

    void closeFlowByCodeList(@Param("flowCodeList") List<String> flowCodeList, @Param("entity") FlowDefinition entity);


}
