package com.warm.flow.core.service;

import com.warm.flow.core.dto.FlowParams;
import com.warm.flow.core.entity.HisTask;
import com.warm.flow.core.entity.Node;
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
     * @param hisTasks 历史任务
     * @param addTasks 代办任务
     * @param flowParams 流程参数
     * @return List<User>
     * @author xiarg
     * @date 2024/5/10 13:59
     */
    List<User> setUser(List<HisTask> hisTasks, List<Task> addTasks, FlowParams flowParams);
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
    List<User> setSkipUser(List<HisTask> hisTasks, List<Task> addTasks, FlowParams flowParams);
    /**
     * 流程定义设置流程计划审批用户
     *
     * @param allNodes 节点集合
     * @return List<User>
     * @author xiarg
     * @date 2024/5/10 13:59
     */
    List<User> setUser(List<Node> allNodes);
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
     * 根据(代办任务，实例，历史表，节点)id删除流程用户
     *
     * @param Ids (代办任务，实例，历史表，节点)id集合
     * @author xiarg
     * @date 2024/5/10 13:59
     */
    void delUser(List<Long> Ids);
}
