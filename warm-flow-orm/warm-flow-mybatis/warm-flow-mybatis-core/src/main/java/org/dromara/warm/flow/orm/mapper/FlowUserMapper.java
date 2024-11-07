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

import org.dromara.warm.flow.orm.entity.FlowUser;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * 流程用户Mapper接口
 *
 * @author xiarg
 * @since 2024/5/10 11:16
 */
public interface FlowUserMapper extends WarmMapper<FlowUser> {

    /**
     * 根据 taskIds 删除
     *
     * @param taskIds 待办任务id集合
     * @return int
     * @author xiarg
     * @since 2024/5/11 11:24
     */
    int deleteByTaskIds(@Param("taskIds") Collection<? extends Serializable> taskIds,
                        @Param("entity") FlowUser entity);

    /**
     * 根据 taskIds 逻辑删除
     *
     * @param taskIds 待办任务id集合
     * @return int
     * @author xiarg
     * @since 2024/5/11 11:24
     */
    int updateByTaskIdsLogic(@Param("taskIds") Collection<? extends Serializable> taskIds,
                             @Param("entity") FlowUser entity,
                             @Param("logicDeleteValue") String logicDeleteValue,
                             @Param("logicNotDeleteValue") String logicNotDeleteValue);

    List<FlowUser> listByAssociatedAndTypes(@Param("types") String[] types
            , @Param("associateds") List<Long> associateds
            , @Param("entity") FlowUser entity,  @Param("dataSourceType") String dataSourceType);

    List<FlowUser> listByProcessedBys(@Param("types") String[] types
            , @Param("processedBys") List<String> processedBys
            , @Param("entity") FlowUser entity,  @Param("dataSourceType") String dataSourceType);

}
