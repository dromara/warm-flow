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
package org.dromara.warm.flow.core.orm.dao;

import org.dromara.warm.flow.core.entity.HisTask;

import java.util.List;

/**
 * 历史任务记录Mapper接口
 *
 * @author warm
 * @since 2023-03-29
 */
public interface FlowHisTaskDao<T extends HisTask> extends WarmDao<T> {

    /**
     * 根据instanceId获取未退回的历史记录
     *
     * @param instanceId
     * @return
     */
    List<T> getNoReject( Long instanceId);


    /**
     * 根据instanceId和流程编码获取未退回的历史记录
     *
     * @param instanceId
     * @param nodeCodes
     * @return
     */
    List<T> getByInsAndNodeCodes(Long instanceId, List<String> nodeCodes);

    /**
     * 根据instanceIds删除
     *
     * @param instanceIds 主键
     * @return 结果
     */
    int deleteByInsIds(List<Long> instanceIds);

    /**
     * 根据任务id和协作类型查询
     *
     * @param taskId
     * @param cooperateTypes
     * @return
     */
    List<T> listByTaskIdAndCooperateTypes(Long taskId, Integer[] cooperateTypes);
}
