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
package org.dromara.warm.flow.core.service;

import org.dromara.warm.flow.core.entity.Task;
import org.dromara.warm.flow.core.entity.User;
import org.dromara.warm.flow.core.orm.service.IWarmService;

import java.util.List;

/**
 * 流程用户Service接口
 *
 * @author xiarg
 * @since 2024/5/10 13:55
 */
public interface UserService extends IWarmService<User> {

    /**
     * 设置流程用户
     *
     * @param addTasks 待办任务
     * @return List<User>
     * @author xiarg
     * @since 2024/5/10 13:59
     */
    List<User> taskAddUsers(List<Task> addTasks);

    /**
     * 待办任务增加流程人员
     *
     * @param task 待办任务任务信息
     * @return List<User>
     * @author xiarg
     * @since 2024/5/10 15:45
     */
    List<User> taskAddUser(Task task);

    /**
     * 根据待办任务id删除流程用户
     *
     * @param ids 待办任务id集合
     * @author xiarg
     * @since 2024/5/10 13:59
     */
    void deleteByTaskIds(List<Long> ids);

    /**
     * 根据(待办任务，实例，历史表，节点等)id查询权限人或者处理人
     *
     * @param associated 待办任务id集合
     * @param type       用户表类型
     * @author xiarg
     * @since 2024/5/120 13:59
     */
    List<String> getPermission(Long associated, String... type);

    /**
     * 根据(待办任务，实例，历史表，节点等)id查询权限人或者处理人
     *
     * @param associated 待办任务id
     * @param types      用户表类型
     * @return List<User>
     */
    List<User> listByAssociatedAndTypes(Long associated, String... types);

    /**
     * 根据(待办任务，实例，历史表，节点等)id查询权限人或者处理人
     *
     * @param associateds (待办任务，实例，历史表，节点等)id集合
     * @param types       用户表类型
     * @return List<User>
     */
    List<User> getByAssociateds(List<Long> associateds, String... types);

    /**
     * 根据办理人查询, 返回集合
     *
     * @param associated  待办任务id
     * @param processedBy 办理人
     * @param types       用户表类型
     * @return List<User>
     */
    List<User> listByProcessedBys(Long associated, String processedBy, String... types);

    /**
     * 根据办理人查询
     *
     * @param associated   待办任务id
     * @param processedBys 办理人id集合
     * @param types        用户表类型
     * @return List<User>
     */
    List<User> getByProcessedBys(Long associated, List<String> processedBys, String... types);

    /**
     * 根据关联id更新权限人
     *
     * @param associated  关联人id
     * @param permissions 权限人
     * @param type        权限人类型
     * @param clear       是否清空待办任务的计划审批人
     * @param handler     存储委派时的办理人
     * @return 结果
     * @author xiarg
     * @since 2024/5/10 11:19
     */
    boolean updatePermission(Long associated, List<String> permissions, String type, boolean clear, String handler);

    /**
     * 构造用户比表信息
     *
     * @param associated     关联id
     * @param permissionList 权限标识集合
     * @param type           用户类型
     * @return 结果
     */
    List<User> structureUser(Long associated, List<String> permissionList, String type);

    /**
     * 构造用户比表信息
     *
     * @param associated 关联id
     * @param permission 权限标识
     * @param type       用户类型
     * @return 结果
     */
    User structureUser(Long associated, String permission, String type);

    /**
     * 构造用户比表信息
     *
     * @param associated     关联id
     * @param permissionList 权限标识集合
     * @param type           用户类型
     * @param handler        办理人（记录委派人）
     * @return 结果
     */
    List<User> structureUser(Long associated, List<String> permissionList, String type, String handler);

    /**
     * 构造用户比表信息
     *
     * @param associated 关联id
     * @param permission 权限标识
     * @param type       用户类型
     * @param handler    办理人（记录委派人）
     * @return 结果
     */
    User structureUser(Long associated, String permission, String type, String handler);

}
