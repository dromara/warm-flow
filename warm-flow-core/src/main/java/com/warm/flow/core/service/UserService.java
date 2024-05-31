package com.warm.flow.core.service;

import com.warm.flow.core.entity.Task;
import com.warm.flow.core.entity.User;
import com.warm.flow.core.orm.service.IWarmService;

import java.util.List;

/**
 * 流程用户Service接口
 *
 * @author xiarg
 * @date 2024/5/10 13:55
 */
public interface UserService extends IWarmService<User> {

    /**
     * 设置流程用户
     *
     * @param addTasks 代办任务
     * @return List<User>
     * @author xiarg
     * @date 2024/5/10 13:59
     */
    List<User> taskAddUsers(List<Task> addTasks);
    /**
     * 设置流程用户
     *
     * @param addTasks 代办任务
     * @param taskId 任务id
     * @return List<User>
     * @author xiarg
     * @date 2024/5/10 13:59
     */
    List<User> setSkipUser(List<Task> addTasks, Long taskId);

    /**
     * 代办任务增加流程人员
     *
     * @param task 代办任务任务信息
     * @return List<User>
     * @author xiarg
     * @date 2024/5/10 15:45
     */
    List<User> taskAddUser(Task task);
    /**
     * 根据代办任务id删除流程用户
     *
     * @param ids 代办任务id集合
     * @author xiarg
     * @date 2024/5/10 13:59
     */
    void deleteByTaskIds(List<Long> ids);

    /**
     * 根据(代办任务，实例，历史表，节点等)id查询权限人或者处理人
     *
     * @param associated (代办任务，实例，历史表，节点等)id集合
     * @param type 用户表类型
     * @author xiarg
     * @date 2024/5/120 13:59
     */
    List<String> getPermission(Long associated, String... type);

    /**
     * 根据(代办任务，实例，历史表，节点等)id查询权限人或者处理人
     *
     * @param associated (代办任务，实例，历史表，节点等)id集合
     * @param types 用户表类型
     * @return
     */
    List<User> listByAssociatedAndTypes(Long associated, String[] types);

    /**
     * 根据关联id更新权限人
     *
     * @param associated 关联人id
     * @param permissions 权限人
     * @param type 权限人类型
     * @param clear 是否清空代办任务的计划审批人
     * @param createBy 存储委派时的创建人
     * @return 结果
     * @author xiarg
     * @date 2024/5/10 11:19
     */
    boolean updatePermission(Long associated, List<String> permissions, String type, boolean clear, String createBy);

    /**
     * 构造用户比表信息
     * @param associated 关联id
     * @param permissionList 权限标识集合
     * @param type 用户类型
     * @return 结果
     */
    List<User> structureUser(Long associated, List<String> permissionList, String type);

    /**
     * 构造用户比表信息
     * @param associated 关联id
     * @param permission 权限标识
     * @param type 用户类型
     * @return 结果
     */
    User structureUser(Long associated, String permission, String type);

    /**
     * 构造用户比表信息
     *
     * @param associated 关联id
     * @param permissionList 权限标识集合
     * @param type 用户类型
     * @param createBy 创建人（记录委派人）
     * @return 结果
     */
    List<User> structureUser(Long associated, List<String> permissionList, String type, String createBy);

    /**
     * 构造用户比表信息
     *
     * @param associated 关联id
     * @param permission 权限标识
     * @param type 用户类型
     * @param createBy 创建人（记录委派人）
     * @return 结果
     */
    User structureUser(Long associated, String permission, String type, String createBy);

}
