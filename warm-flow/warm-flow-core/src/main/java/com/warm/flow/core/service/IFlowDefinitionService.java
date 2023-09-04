package com.warm.flow.core.service;

import com.warm.flow.core.domain.entity.FlowDefinition;
import com.warm.mybatis.core.service.IFlowService;

import java.util.List;

/**
 * 流程定义Service接口
 *
 * @author hh
 * @date 2023-03-29
 */
public interface IFlowDefinitionService extends IFlowService<FlowDefinition> {
    List<FlowDefinition> queryByCodeList(List<String> flowCodeList);

    void closeFlowByCodeList(List<String> flowCodeList);

    /**
     * 校验后新增
     *
     * @param flowDefinition
     * @return
     */
    boolean checkAndSave(FlowDefinition flowDefinition);

    /**
     * 删除流程定义
     *
     * @param ids
     * @return
     */
    boolean removeDef(List<Long> ids);

    /**
     * 发布流程定义
     *
     * @param id
     * @return
     */
    boolean publish(Long id);

    /**
     * 取消发布流程定义
     *
     * @param id
     * @return
     */
    boolean unPublish(Long id);
}
