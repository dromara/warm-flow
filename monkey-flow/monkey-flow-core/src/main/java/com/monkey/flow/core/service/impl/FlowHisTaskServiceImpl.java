package com.monkey.flow.core.service.impl;

import com.monkey.flow.core.domain.entity.FlowHisTask;
import com.monkey.flow.core.mapper.FlowHisTaskMapper;
import com.monkey.flow.core.service.IFlowHisTaskService;
import com.monkey.mybatis.core.service.impl.FlowBaseServiceImpl;
import com.monkey.mybatis.core.utils.SqlHelper;

import javax.annotation.Resource;
import java.util.List;

/**
 * 历史任务记录Service业务层处理
 *
 * @author hh
 * @date 2023-03-29
 */
public class FlowHisTaskServiceImpl extends FlowBaseServiceImpl<FlowHisTask> implements IFlowHisTaskService {
    @Resource
    private FlowHisTaskMapper hisTaskMapper;

    @Override
    public FlowHisTaskMapper getBaseMapper() {
        return hisTaskMapper;
    }

    @Override
    public List<FlowHisTask> queryByNewInstanceIds(List<Long> instanceIds) {
        return hisTaskMapper.queryByNewInstanceIds(instanceIds);
    }

    @Override
    public List<FlowHisTask> queryByInstanceIds(List<Long> instanceIds) {
        return hisTaskMapper.queryByInstanceIds(instanceIds);
    }

    @Override
    public boolean deleteByInstanceIds(List<Long> instanceIds) {
        return SqlHelper.retBool(hisTaskMapper.deleteByInstanceIds(instanceIds));
    }
}
