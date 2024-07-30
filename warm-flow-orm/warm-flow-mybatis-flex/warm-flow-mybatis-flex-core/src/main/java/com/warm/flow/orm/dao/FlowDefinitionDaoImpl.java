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

import com.mybatisflex.core.query.QueryWrapper;
import com.warm.flow.core.dao.FlowDefinitionDao;
import com.warm.flow.core.enums.PublishStatus;
import com.warm.flow.core.invoker.FrameInvoker;
import com.warm.flow.orm.entity.FlowDefinition;
import com.warm.flow.orm.mapper.FlowDefinitionMapper;
import com.warm.flow.orm.utils.TenantDeleteUtil;

import java.util.List;

/**
 * 流程定义Mapper接口
 *
 * @author warm
 * @date 2023-03-29
 */
public class FlowDefinitionDaoImpl extends WarmDaoImpl<FlowDefinition> implements FlowDefinitionDao<FlowDefinition> {

    @Override
    public FlowDefinitionMapper getMapper() {
        return FrameInvoker.getBean(FlowDefinitionMapper.class);
    }

    @Override
    public FlowDefinition newEntity() {
        return new FlowDefinition();
    }

    @Override
    public List<FlowDefinition> queryByCodeList(List<String> flowCodeList) {
        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.in(FlowDefinition::getFlowCode, flowCodeList);
        return getMapper().selectListByQueryAs(queryWrapper, FlowDefinition.class);
    }

    @Override
    public void closeFlowByCodeList(List<String> flowCodeList) {
        QueryWrapper queryWrapper = TenantDeleteUtil.getDefaultWrapper(newEntity());
        queryWrapper.in(FlowDefinition::getFlowCode, flowCodeList);
        getMapper().updateByQuery(new FlowDefinition().setIsPublish(PublishStatus.EXPIRED.getKey()), queryWrapper);
    }

}
