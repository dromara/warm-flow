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
package org.dromara.warm.flow.orm.mapper;

import org.dromara.warm.flow.orm.entity.FlowDefinition;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 流程定义Mapper接口
 *
 * @author warm
 * @since 2023-03-29
 */
public interface FlowDefinitionMapper extends WarmMapper<FlowDefinition> {


    List<FlowDefinition> queryByCodeList(@Param("flowCodeList") List<String> flowCodeList, @Param("entity") FlowDefinition entity);

    void closeFlowByCodeList(@Param("flowCodeList") List<String> flowCodeList, @Param("entity") FlowDefinition entity);


    void updatePublishStatus(@Param("ids") List<Long> ids, @Param("publishStatus") Integer publishStatus
            , @Param("entity") FlowDefinition flowDefinition);
}
