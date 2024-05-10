package com.warm.flow.core.service;

import com.warm.flow.core.dto.FlowParams;
import com.warm.flow.core.entity.*;
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
     * @param hisTasks 历史任务
     * @param addTasks 代办任务
     * @param flowParams 流程参数
     * @return List<User>
     * @author xiarg
     * @date 2024/5/10 13:59
     */
    List<User> setUser(List<HisTask> hisTasks, List<Task> addTasks, FlowParams flowParams);
    /**
     * 历史任务增加流程人员
     *
     * @param hisTask 历史任务信息
     * @param flowParams 工作流内置参数
     * @return User
     * @author xiarg
     * @date 2024/5/10 15:45
     */
    User hisTaskAddUser(HisTask hisTask, FlowParams flowParams);

    /**
     * 代办任务增加流程人员
     *
     * @param task 代办任务任务信息
     * @param flowParams 工作流内置参数
     * @return List<User>
     * @author xiarg
     * @date 2024/5/10 15:45
     */
    List<User> taskAddUser(Task task, FlowParams flowParams);
    /**
     * 根据任务id删除流程用户
     *
     * @param taskIds 任务id集合
     * @author xiarg
     * @date 2024/5/10 13:59
     */
    void delUser(List<Long> taskIds);
}
