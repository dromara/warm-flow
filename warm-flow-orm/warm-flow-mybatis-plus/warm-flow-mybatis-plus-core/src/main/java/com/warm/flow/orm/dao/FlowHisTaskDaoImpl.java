/*
 *    Copyright 2024-2025, Warm-Flow (290631660@qq.com).
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.warm.flow.orm.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.warm.flow.core.dao.FlowHisTaskDao;
import com.warm.flow.core.enums.SkipType;
import com.warm.flow.core.invoker.FrameInvoker;
import com.warm.flow.orm.entity.FlowHisTask;
import com.warm.flow.orm.mapper.FlowHisTaskMapper;

import java.util.Arrays;
import java.util.List;

/**
 * 历史任务记录Mapper接口
 *
 * @author warm
 * @date 2023-03-29
 */
public class FlowHisTaskDaoImpl extends WarmDaoImpl<FlowHisTask> implements FlowHisTaskDao<FlowHisTask> {

    @Override
    public FlowHisTaskMapper getMapper() {
        return FrameInvoker.getBean(FlowHisTaskMapper.class);
    }

    @Override
    public FlowHisTask newEntity() {
        return new FlowHisTask();
    }

    @Override
    public List<FlowHisTask> getNoReject(Long instanceId) {
        LambdaQueryWrapper<FlowHisTask> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FlowHisTask::getInstanceId, instanceId)
                .eq(FlowHisTask::getSkipType, SkipType.PASS.getKey())
                .orderByDesc(FlowHisTask::getCreateTime);
        return getMapper().selectList(queryWrapper);
    }

    @Override
    public List<FlowHisTask> getByInsAndNodeCodes(Long instanceId, List<String> nodeCodes) {
        LambdaQueryWrapper<FlowHisTask> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FlowHisTask::getInstanceId, instanceId)
                .in(FlowHisTask::getNodeCode, nodeCodes)
                .orderByDesc(FlowHisTask::getCreateTime);
        return getMapper().selectList(queryWrapper);
    }

    @Override
    public int deleteByInsIds(List<Long> instanceIds) {
        return getMapper().delete(new LambdaQueryWrapper<FlowHisTask>().in(FlowHisTask::getInstanceId, instanceIds));
    }

    @Override
    public List<FlowHisTask> listByTaskIdAndCooperateTypes(Long taskId, Integer[] cooperateTypes) {
        LambdaQueryWrapper<FlowHisTask> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FlowHisTask::getTaskId, taskId).in(FlowHisTask::getCooperateType,  Arrays.asList(cooperateTypes));
        return getMapper().selectList(queryWrapper);
    }

}
