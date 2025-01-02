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
import org.dromara.warm.flow.core.orm.dao.FlowUserDao;
import org.dromara.warm.flow.core.invoker.FrameInvoker;
import org.dromara.warm.flow.core.utils.CollUtil;
import org.dromara.warm.flow.core.utils.StringUtils;
import org.dromara.warm.flow.orm.entity.FlowUser;
import org.dromara.warm.flow.orm.mapper.FlowUserMapper;
import org.dromara.warm.flow.orm.utils.TenantDeleteUtil;

import java.util.List;

/**
 * 流程用户Mapper接口
 *
 * @author warm
 * @since 2023-03-29
 */
public class FlowUserDaoImpl extends WarmDaoImpl<FlowUser> implements FlowUserDao<FlowUser> {

    @Override
    public FlowUserMapper getMapper() {
        return FrameInvoker.getBean(FlowUserMapper.class);
    }

    @Override
    public FlowUser newEntity() {
        return new FlowUser();
    }

    @Override
    public int deleteByTaskIds(List<Long> taskIdList) {
        FlowUser entity = TenantDeleteUtil.getEntity(newEntity());
        if (StringUtils.isNotEmpty(entity.getDelFlag())) {
            return getMapper().updateByTaskIdsLogic(taskIdList, entity, FlowEngine.getFlowConfig().getLogicDeleteValue(),
                    entity.getDelFlag());
        }
        return getMapper().deleteByTaskIds(taskIdList, entity);
    }

    @Override
    public List<FlowUser> listByAssociatedAndTypes(List<Long> associateds, String[] types) {
        String dataSourceType = FlowEngine.dataSourceType();
        if (CollUtil.isNotEmpty(associateds) && associateds.size() == 1) {
            return getMapper().listByAssociatedAndTypes(types, null
                    , TenantDeleteUtil.getEntity(newEntity()).setAssociated(associateds.get(0)), dataSourceType);
        }
        return getMapper().listByAssociatedAndTypes(types, associateds
                , TenantDeleteUtil.getEntity(newEntity()), dataSourceType);
    }

    @Override
    public List<FlowUser> listByProcessedBys(Long associated, List<String> processedBys, String[] types) {
        String dataSourceType = FlowEngine.dataSourceType();
        if (CollUtil.isNotEmpty(processedBys) && processedBys.size() == 1) {
            return getMapper().listByProcessedBys(types, null, TenantDeleteUtil
                    .getEntity(newEntity()).setAssociated(associated).setProcessedBy(processedBys.get(0)), dataSourceType);
        }
        return getMapper().listByProcessedBys(types, processedBys
                , TenantDeleteUtil.getEntity(newEntity()).setAssociated(associated), dataSourceType);
    }
}
