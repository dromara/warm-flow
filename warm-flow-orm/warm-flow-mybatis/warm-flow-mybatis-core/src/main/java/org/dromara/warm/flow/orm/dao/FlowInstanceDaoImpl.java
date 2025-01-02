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

import org.dromara.warm.flow.core.orm.dao.FlowInstanceDao;
import org.dromara.warm.flow.core.invoker.FrameInvoker;
import org.dromara.warm.flow.orm.entity.FlowInstance;
import org.dromara.warm.flow.orm.mapper.FlowInstanceMapper;
import org.dromara.warm.flow.orm.utils.TenantDeleteUtil;

import java.util.List;

/**
 * 流程实例Mapper接口
 *
 * @author warm
 * @since 2023-03-29
 */
public class FlowInstanceDaoImpl extends WarmDaoImpl<FlowInstance> implements FlowInstanceDao<FlowInstance> {

    @Override
    public FlowInstanceMapper getMapper() {
        return FrameInvoker.getBean(FlowInstanceMapper.class);
    }

    @Override
    public FlowInstance newEntity() {
        return new FlowInstance();
    }

    @Override
    public List<FlowInstance> getByDefIds(List<Long> defIds) {
        return getMapper().getByDefIds(defIds, TenantDeleteUtil.getEntity(newEntity()));
    }
}
