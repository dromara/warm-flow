package com.monkey.flow.core.service.impl;

import com.monkey.flow.core.constant.FlowConstant;
import com.monkey.flow.core.domain.entity.FlowInstance;
import com.monkey.flow.core.mapper.FlowInstanceMapper;
import com.monkey.flow.core.service.IFlowInstanceService;
import com.monkey.mybatis.core.service.impl.FlowBaseServiceImpl;
import com.monkey.flow.core.utils.AssertUtil;

import javax.annotation.Resource;
import java.util.List;

/**
 * 流程实例Service业务层处理
 *
 * @author hh
 * @date 2023-03-29
 */
public class FlowInstanceServiceImpl extends FlowBaseServiceImpl<FlowInstance> implements IFlowInstanceService {
    @Resource
    private FlowInstanceMapper instanceMapper;

    @Override
    public FlowInstanceMapper getBaseMapper() {
        return instanceMapper;
    }

    @Override
    public List<FlowInstance> queryByidWithUpdateLock(List<Long> instanceIds) {
        AssertUtil.isFalse(instanceIds == null || instanceIds.size() == 0, FlowConstant.NOT_FOUNT_FLOW);
        for (int i = 0; i < instanceIds.size(); i++) {
            AssertUtil.isNull(instanceIds.get(i), "流程定义id不能为空!");
        }
        return instanceMapper.queryByidWithUpdateLock(instanceIds);
    }
}
