package com.warm.flow.core.service.impl;

import com.warm.flow.core.constant.FlowConstant;
import com.warm.flow.core.domain.entity.FlowInstance;
import com.warm.flow.core.mapper.FlowInstanceMapper;
import com.warm.flow.core.service.IFlowInstanceService;
import com.warm.flow.core.utils.AssertUtil;
import com.warm.mybatis.core.service.impl.FlowServiceImpl;

import javax.annotation.Resource;
import java.util.List;

/**
 * 流程实例Service业务层处理
 *
 * @author hh
 * @date 2023-03-29
 */
public class FlowInstanceServiceImpl extends FlowServiceImpl<FlowInstance> implements IFlowInstanceService {
    @Resource
    private FlowInstanceMapper instanceMapper;

    @Override
    public FlowInstanceMapper getBaseMapper() {
        return instanceMapper;
    }

    @Override
    public List<FlowInstance> getByIdWithLock(List<Long> ids) {
        AssertUtil.isFalse(ids == null || ids.size() == 0, FlowConstant.NOT_FOUNT_INSTANCE_ID);
        for (int i = 0; i < ids.size(); i++) {
            AssertUtil.isNull(ids.get(i), "流程定义id不能为空!");
        }
        return instanceMapper.getByIdWithLock(ids);
    }

}
