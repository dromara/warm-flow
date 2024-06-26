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
import com.warm.flow.core.enums.FlowStatus;
import com.warm.flow.core.invoker.FrameInvoker;
import com.warm.flow.core.utils.StringUtils;
import com.warm.flow.orm.entity.FlowHisTask;
import com.warm.flow.orm.mapper.FlowHisTaskMapper;
import com.warm.flow.orm.utils.TenantDeleteUtil;

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
    public List<FlowHisTask> getNoReject(String nodeCode, String targetNodeCode, Long instanceId) {
        LambdaQueryWrapper<FlowHisTask> queryWrapper = TenantDeleteUtil.getLambdaWrapperDefault(newEntity());
        queryWrapper.eq(FlowHisTask::getNodeCode, nodeCode)
                .eq(StringUtils.isNotEmpty(targetNodeCode), FlowHisTask::getTargetNodeCode, targetNodeCode)
                .eq(FlowHisTask::getInstanceId, instanceId)
                .eq(FlowHisTask::getFlowStatus, FlowStatus.PASS.getKey())
                .orderByDesc(FlowHisTask::getCreateTime);
        return getMapper().selectList(queryWrapper);
    }

    @Override
    public int deleteByInsIds(List<Long> instanceIds) {
        return delete(newEntity(), (luw) -> luw.in(FlowHisTask::getInstanceId, instanceIds)
                , (lqw) -> lqw.in(FlowHisTask::getInstanceId, instanceIds));
    }

    @Override
    public List<FlowHisTask> listByTaskIdAndCooperateTypes(Long taskId, Integer[] cooperateTypes) {
        LambdaQueryWrapper<FlowHisTask> queryWrapper = TenantDeleteUtil.getLambdaWrapperDefault(newEntity());
        queryWrapper.eq(FlowHisTask::getTaskId, taskId).in(FlowHisTask::getCooperateType, cooperateTypes);
        return getMapper().selectList(queryWrapper);
    }

}
