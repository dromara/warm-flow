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

import org.dromara.warm.flow.core.FlowEngine;
import org.dromara.warm.flow.core.invoker.FrameInvoker;
import org.dromara.warm.flow.core.orm.dao.FlowTaskDao;
import org.dromara.warm.flow.core.utils.StringUtils;
import org.dromara.warm.flow.orm.entity.FlowTask;
import org.dromara.warm.flow.orm.mapper.FlowTaskMapper;
import org.dromara.warm.flow.orm.utils.TenantDeleteUtil;

import java.util.List;

/**
 * 待办任务Mapper接口
 *
 * @author warm
 * @since 2023-03-29
 */
public class FlowTaskDaoImpl extends WarmDaoImpl<FlowTask> implements FlowTaskDao<FlowTask> {

    @Override
    public FlowTaskMapper getMapper() {
        return FrameInvoker.getBean(FlowTaskMapper.class);
    }

    @Override
    public FlowTask newEntity() {
        return new FlowTask();
    }

    /**
     * 根据instanceIds删除
     *
     * @param instanceIds 主键
     * @return 结果
     */
    @Override
    public int deleteByInsIds(List<Long> instanceIds) {
        FlowTask entity = TenantDeleteUtil.getEntity(newEntity());
        if (StringUtils.isNotEmpty(entity.getDelFlag())) {
            return getMapper().updateByInsIdsLogic(instanceIds, entity, FlowEngine.getFlowConfig().getLogicDeleteValue(), entity.getDelFlag());
        }
        return getMapper().deleteByInsIds(instanceIds, entity);
    }

    @Override
    public List<FlowTask> getByInsIdAndNodeCodes(Long instanceId, List<String> nodeCodes) {
        return getMapper().getByInsIdAndNodeCodes(instanceId, nodeCodes, TenantDeleteUtil.getEntity(newEntity()));
    }
}
