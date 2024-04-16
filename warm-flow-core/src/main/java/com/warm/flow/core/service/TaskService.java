package com.warm.flow.core.service;

import com.warm.flow.core.dto.FlowParams;
import com.warm.flow.core.entity.Instance;
import com.warm.flow.core.entity.Node;
import com.warm.flow.core.entity.Task;
import com.warm.flow.core.orm.service.IWarmService;
import com.warm.tools.utils.page.Page;

import java.util.List;

/**
 * 待办任务Service接口
 *
 * @author warm
 * @date 2023-03-29
 */
public interface TaskService extends IWarmService<Task> {

    /**
     * 根据任务id，流程跳转
     *
     * @param taskId:流程任务id[必传]
     * @param flowParams:包含流程相关参数的对象 - skipType:跳转类型(PASS审批通过 REJECT驳回) [必传]
     *                               - nodeCode:节点编码 [如果指定跳转节点,必传]
     *                               - permissionFlag:办理人权限标识[按需传输]
     *                               - message:审批意见  [按需传输]
     *                               - createBy:办理人帐号[建议传]
     *                               - nickname:办理人昵称[按需传输]
     *                               - variable:流程变量[按需传输,跳转条件放入流程变量<互斥网关必传>]
     *                               - variableTask:任务变量[按需传输]     * @return
     */
    Instance skip(Long taskId, FlowParams flowParams);

    /**
     * 流程跳转
     *
     * @param flowParams:包含流程相关参数的对象 - skipType:跳转类型(PASS审批通过 REJECT驳回) [必传]
     *                               - nodeCode:节点编码 [如果指定跳转节点,必传]
     *                               - permissionFlag:办理人权限标识[按需传输]
     *                               - message:审批意见  [按需传输]
     *                               - createBy:办理人帐号[建议传]
     *                               - nickname:办理人昵称[按需传输]
     *                               - variable:流程变量[按需传输,跳转条件放入流程变量<互斥网关必传>]
     *                               - variableTask:任务变量[按需传输]     * @return
     * @param task:流程任务[必传]
     * @param instance:流程实例[必传]
     */
    Instance skip(FlowParams flowParams, Task task, Instance instance);

    /**
     * 分页查询待办任务
     *
     * @param task 条件实体
     * @param page
     * @return
     */
    @Deprecated
    Page<Task> toDoPage(Task task, Page<Task> page);

    /**
     * 根据instanceIds删除
     *
     * @param instanceIds
     * @return
     */
    boolean deleteByInsIds(List<Long> instanceIds);

    /**
     * 校验是否网关节点,如果是重新获取新的后面的节点
     *
     * @param flowParams
     * @param nextNode
     * @return
     */
    List<Node> getNextByCheckGateWay(FlowParams flowParams, Node nextNode);

    /**
     * 设置流程待办任务对象
     *
     * @param node
     * @param instance
     * @param flowParams
     * @return
     */
    Task addTask(Node node, Instance instance, FlowParams flowParams);

    /**
     * 设置流程实例和代码任务流程状态
     *
     * @param nodeType 节点类型（开始节点、中间节点、结束节点）
     * @param skipType 流程条件
     */
    Integer setFlowStatus(Integer nodeType, String skipType);
}
