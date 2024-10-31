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
package org.dromara.warm.flow.orm.dao;

import com.mybatisflex.core.query.QueryWrapper;
import org.dromara.warm.flow.core.dao.FlowHisTaskDao;
import org.dromara.warm.flow.core.enums.SkipType;
import org.dromara.warm.flow.core.invoker.FrameInvoker;
import org.dromara.warm.flow.orm.entity.FlowHisTask;
import org.dromara.warm.flow.orm.mapper.FlowHisTaskMapper;
import org.dromara.warm.flow.orm.utils.TenantDeleteUtil;

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
        QueryWrapper queryWrapper = TenantDeleteUtil.getDefaultWrapper(newEntity());
        queryWrapper.eq(FlowHisTask::getInstanceId, instanceId)
                .eq(FlowHisTask::getSkipType, SkipType.PASS.getKey())
                .orderBy(FlowHisTask::getCreateTime, false);
        return getMapper().selectListByQuery(queryWrapper);
    }

    @Override
    public List<FlowHisTask> getByInsAndNodeCodes(Long instanceId, List<String> nodeCodes) {
        QueryWrapper queryWrapper = TenantDeleteUtil.getDefaultWrapper(newEntity());
        queryWrapper.eq(FlowHisTask::getInstanceId, instanceId)
                .in(FlowHisTask::getNodeCode, nodeCodes)
                .orderBy(FlowHisTask::getCreateTime, false);
        return getMapper().selectListByQuery(queryWrapper);
    }

    @Override
    public int deleteByInsIds(List<Long> instanceIds) {
        return delete(newEntity(), (uq) -> uq.in(FlowHisTask::getInstanceId, instanceIds)
                , (qw) -> qw.in(FlowHisTask::getInstanceId, instanceIds));
    }

    @Override
    public List<FlowHisTask> listByTaskIdAndCooperateTypes(Long taskId, Integer[] cooperateTypes) {
        QueryWrapper queryWrapper = TenantDeleteUtil.getDefaultWrapper(newEntity());
        queryWrapper.eq(FlowHisTask::getTaskId, taskId).in(FlowHisTask::getCooperateType, (Object) cooperateTypes);
        return getMapper().selectListByQuery(queryWrapper);
    }

}
