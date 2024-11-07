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

import org.dromara.warm.flow.orm.entity.FlowHisTask;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * 历史任务记录Mapper接口
 *
 * @author warm
 * @since 2023-03-29
 */
public interface FlowHisTaskMapper extends WarmMapper<FlowHisTask> {

    /**
     * 根据instanceId获取未退回的历史记录
     *
     * @param instanceId
     * @param entity
     * @return
     */
    List<FlowHisTask> getNoReject(@Param("instanceId") Long instanceId
            , @Param("entity") FlowHisTask entity);

    /**
     * 根据instanceId和流程编码获取未退回的历史记录
     *
     * @param instanceId
     * @param nodeCodes
     * @return
     */
    List<FlowHisTask> getByInsAndNodeCodes(@Param("instanceId") Long instanceId
            , @Param("nodeCodes") List<String> nodeCodes
            , @Param("entity") FlowHisTask entity);


    /**
     * 根据instanceIds删除
     *
     * @param instanceIds 主键
     * @param entity
     * @return 结果
     */
    int deleteByInsIds(@Param("instanceIds") List<Long> instanceIds, @Param("entity") FlowHisTask entity);

    /**
     * 逻辑删除
     *
     * @param instanceIds 需要删除的数据主键集合
     * @return 结果
     */
    int updateByInsIdsLogic(@Param("instanceIds") Collection<? extends Serializable> instanceIds
            , @Param("entity") FlowHisTask entity, @Param("logicDeleteValue") String logicDeleteValue
            , @Param("logicNotDeleteValue") String logicNotDeleteValue);

    List<FlowHisTask> listByTaskIdAndCooperateTypes(@Param("cooperateTypes") Integer[] cooperateTypes
            , @Param("entity") FlowHisTask entity);
}
