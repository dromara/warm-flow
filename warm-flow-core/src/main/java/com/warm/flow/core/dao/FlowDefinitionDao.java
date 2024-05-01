package com.warm.flow.core.dao;

import com.warm.flow.core.entity.Definition;

import java.util.List;

/**
 * 流程定义Mapper接口
 *
 * @author warm
 * @date 2023-03-29
 */
public interface FlowDefinitionDao<T extends Definition> extends WarmDao<T> {


    List<T> queryByCodeList(List<String> flowCodeList);

    void closeFlowByCodeList(List<String> flowCodeList);

}
