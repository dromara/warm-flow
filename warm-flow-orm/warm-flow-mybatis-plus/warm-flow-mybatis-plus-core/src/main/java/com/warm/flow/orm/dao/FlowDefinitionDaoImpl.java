package com.warm.flow.orm.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.warm.flow.core.dao.FlowDefinitionDao;
import com.warm.flow.core.enums.PublishStatus;
import com.warm.flow.core.invoker.FrameInvoker;
import com.warm.flow.orm.entity.FlowDefinition;
import com.warm.flow.orm.mapper.FlowDefinitionMapper;
import com.warm.flow.orm.utils.TenantDeleteUtil;

import java.util.List;

/**
 * 流程定义Mapper接口
 *
 * @author warm
 * @date 2023-03-29
 */
public class FlowDefinitionDaoImpl extends WarmDaoImpl<FlowDefinition> implements FlowDefinitionDao<FlowDefinition> {

    @Override
    public FlowDefinitionMapper getMapper() {
        return FrameInvoker.getBean(FlowDefinitionMapper.class);
    }

    @Override
    public FlowDefinition newEntity() {
        return new FlowDefinition();
    }

    @Override
    public List<FlowDefinition> queryByCodeList(List<String> flowCodeList) {
        LambdaQueryWrapper<FlowDefinition> queryWrapper = TenantDeleteUtil.getLambdaWrapperDefault(newEntity());
        queryWrapper.in(FlowDefinition::getFlowCode, flowCodeList);
        return getMapper().selectList(queryWrapper);
    }

    @Override
    public void closeFlowByCodeList(List<String> flowCodeList) {
        LambdaQueryWrapper<FlowDefinition> queryWrapper = TenantDeleteUtil.getLambdaWrapperDefault(newEntity());
        queryWrapper.in(FlowDefinition::getFlowCode, flowCodeList);
        getMapper().update(new FlowDefinition().setIsPublish(PublishStatus.EXPIRED.getKey()), queryWrapper);
    }

}
