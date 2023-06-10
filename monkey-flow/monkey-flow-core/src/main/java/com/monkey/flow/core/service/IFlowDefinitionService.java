package com.monkey.flow.core.service;

import com.monkey.flow.core.domain.entity.FlowDefinition;
import com.monkey.mybatis.core.service.IFlowBaseService;

import java.util.List;

/**
 * 流程定义Service接口
 *
 * @author hh
 * @date 2023-03-29
 */
public interface IFlowDefinitionService extends IFlowBaseService<FlowDefinition> {
    List<FlowDefinition> queryByCodeList(List<String> flowCodeList);

    void closeFlowByCodeList(List<String> flowCodeList);

    /**
     * 删除流程定义
     * @param ids
     */
    boolean remove(List<Long> ids);

    /**
     * 发布流程定义
     * @param id
     * @return
     */
    boolean publish(Long id);

    /**
     * 取消发布流程定义
     * @param id
     * @return
     */
    boolean unPublish(Long id);
}
