package com.monkey.flow.core.service.impl;

import com.monkey.flow.core.constant.FlowConstant;
import com.monkey.flow.core.domain.entity.FlowTask;
import com.monkey.flow.core.mapper.FlowTaskMapper;
import com.monkey.flow.core.service.IFlowTaskService;
import com.monkey.flow.core.utils.AssertUtil;
import com.monkey.mybatis.core.service.impl.FlowBaseServiceImpl;

import javax.annotation.Resource;
import java.util.List;

/**
 * 待办任务Service业务层处理
 *
 * @author hh
 * @date 2023-03-29
 */
public class FlowTaskServiceImpl extends FlowBaseServiceImpl<FlowTask> implements IFlowTaskService {
    @Resource
    private FlowTaskMapper taskMapper;

    @Override
    public FlowTaskMapper getBaseMapper() {
        return taskMapper;
    }

    @Override
    public List<FlowTask> getByInsIds(List<Long> instanceIds) {
        AssertUtil.isFalse(instanceIds == null || instanceIds.size() == 0, FlowConstant.NOT_FOUNT_INSTANCE_ID);
        for (int i = 0; i < instanceIds.size(); i++) {
            AssertUtil.isNull(instanceIds.get(i), "流程定义id不能为空!");
        }
        return taskMapper.getByInsIds(instanceIds);
    }

    @Override
    public List<FlowTask> toDoList(FlowTask flowTask) {
        return taskMapper.toDoList(flowTask);
    }
}
