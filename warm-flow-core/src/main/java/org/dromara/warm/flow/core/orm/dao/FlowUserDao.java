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

import org.dromara.warm.flow.core.entity.User;

import java.util.List;

/**
 * 流程用户Mapper接口
 *
 * @author xiarg
 * @since 2024/5/10 11:15
 */
public interface FlowUserDao<T extends User> extends WarmDao<T> {

    /**
     * 根据taskId删除
     *
     * @param taskIdList 待办任务主键集合
     * @return 结果
     * @author xiarg
     * @since 2024/5/10 11:19
     */
    int deleteByTaskIds(List<Long> taskIdList);

    /**
     * 根据(待办任务，实例，历史表，节点等)id查询权限人或者处理人
     *
     * @param associateds (待办任务，实例，历史表，节点等)id集合
     * @param types       用户表类型
     * @return
     */
    List<T> listByAssociatedAndTypes(List<Long> associateds, String[] types);

    /**
     * 根据办理人查询
     *
     * @param associated   待办任务id
     * @param processedBys 办理人id集合
     * @param types        用户表类型
     * @return
     */
    List<T> listByProcessedBys(Long associated, List<String> processedBys, String[] types);
}
