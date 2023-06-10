package com.monkey.flow.core.service.impl;

import com.monkey.flow.core.constant.FlowConstant;
import com.monkey.flow.core.domain.entity.FlowDefinition;
import com.monkey.flow.core.enums.PublishStatus;
import com.monkey.flow.core.exception.FlowException;
import com.monkey.flow.core.mapper.FlowDefinitionMapper;
import com.monkey.flow.core.service.IFlowDefinitionService;
import com.monkey.flow.core.service.IFlowNodeService;
import com.monkey.flow.core.service.IFlowSkipService;
import com.monkey.mybatis.core.service.impl.FlowBaseServiceImpl;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * 流程定义Service业务层处理
 *
 * @author hh
 * @date 2023-03-29
 */
public class FlowDefinitionServiceImpl extends FlowBaseServiceImpl<FlowDefinition> implements IFlowDefinitionService {
    @Resource
    private FlowDefinitionMapper definitionMapper;

    @Resource
    private IFlowNodeService nodeService;

    @Resource
    private IFlowSkipService skipService;

    @Override
    public FlowDefinitionMapper getBaseMapper() {
        return definitionMapper;
    }

    @Override
    public List<FlowDefinition> queryByCodeList(List<String> flowCodeList) {
        return definitionMapper.queryByCodeList(flowCodeList);
    }

    @Override
    public void closeFlowByCodeList(List<String> flowCodeList) {
        definitionMapper.closeFlowByCodeList(flowCodeList);
    }

    @Override
    public void checkSave(FlowDefinition flowDefinition)
    {
        List<String> flowCodeList = Arrays.asList(flowDefinition.getFlowCode());
        List<FlowDefinition> flowDefinitions = queryByCodeList(flowCodeList);
        for (FlowDefinition beforeDefinition : flowDefinitions) {
            if (flowDefinition.getFlowCode().equals(beforeDefinition.getFlowCode()) && flowDefinition.getVersion().equals(beforeDefinition.getVersion())) {
                throw new FlowException(flowDefinition.getFlowCode() + "(" + flowDefinition.getVersion() + ")" + FlowConstant.ALREADY_EXIST);
            }
        }
    }

    /**
     * 删除流程定义
     * @param ids
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean remove(List<Long> ids)
    {
        definitionMapper.deleteFlowNodeByDefinitionIds(ids);
        definitionMapper.deleteFlowSkipByDefinitionIds(ids);
        return deleteByIds(ids);
    }

    @Override
    public boolean publish(Long id)
    {
        FlowDefinition definition = selectById(id);
        List<String> flowCodeList = Arrays.asList(definition.getFlowCode());
        // 把之前的流程定义改为已失效
        closeFlowByCodeList(flowCodeList);

        FlowDefinition flowDefinition = new FlowDefinition();
        flowDefinition.setId(id);
        flowDefinition.setIsPublish(PublishStatus.PUBLISHED.getKey());
        return updateById(flowDefinition);
    }

    @Override
    public boolean unPublish(Long id)
    {
        FlowDefinition flowDefinition = new FlowDefinition();
        flowDefinition.setId(id);
        flowDefinition.setIsPublish(PublishStatus.UNPUBLISHED.getKey());
        return updateById(flowDefinition);
    }

}
