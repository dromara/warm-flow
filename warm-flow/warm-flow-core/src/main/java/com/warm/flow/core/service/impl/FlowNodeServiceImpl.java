package com.warm.flow.core.service.impl;

import com.warm.flow.core.domain.entity.FlowNode;
import com.warm.flow.core.mapper.FlowNodeMapper;
import com.warm.flow.core.service.IFlowNodeService;
import com.warm.mybatis.core.invoker.MapperInvoker;
import com.warm.mybatis.core.service.impl.FlowServiceImpl;

import java.util.List;

/**
 * 流程结点Service业务层处理
 *
 * @author hh
 * @date 2023-03-29
 */
public class FlowNodeServiceImpl extends FlowServiceImpl<FlowNodeMapper, FlowNode> implements IFlowNodeService {

    @Override
    public List<FlowNode> getLastByFlowCode(String flowCode) {
        return MapperInvoker.have(baseMapper -> baseMapper.getLastByFlowCode(flowCode), mapperClass());
    }

}
