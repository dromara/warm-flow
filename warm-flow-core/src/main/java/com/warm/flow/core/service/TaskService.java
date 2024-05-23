package com.warm.flow.core.service;

import com.warm.flow.core.dto.FlowParams;
import com.warm.flow.core.entity.Instance;
import com.warm.flow.core.entity.Node;
import com.warm.flow.core.entity.Task;
import com.warm.flow.core.enums.CirculationType;
import com.warm.flow.core.orm.service.IWarmService;

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
     * @param flowParams:包含流程相关参数的对象 - skipType:跳转类型(PASS审批通过 REJECT退回) [必传]
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
     * @param flowParams:包含流程相关参数的对象 - skipType:跳转类型(PASS审批通过 REJECT退回) [必传]
     *                               - nodeCode:节点编码 [如果指定跳转节点,必传]
     *                               - permissionFlag:办理人权限标识[按需传输]
     *                               - message:审批意见  [按需传输]
     *                               - createBy:办理人帐号[建议传]
     *                               - nickname:办理人昵称[按需传输]
     *                               - variable:流程变量[按需传输,跳转条件放入流程变量<互斥网关必传>]
     *                               - variableTask:任务变量[按需传输]     * @return
     * @param task:流程任务[必传]
     */
    Instance skip(FlowParams flowParams, Task task);
    /**
     * 受托人处理任务方法
     *
     * @param taskId 任务id
     * @return flowParams 流程参数
     * @author xiarg
     * @date 2024/5/10 11:19
     */
    public Instance handleDepute(Long taskId, FlowParams flowParams);
    /**
     * 终止流程，提前结束流程，将所有代办任务转历史
     *
     * @param taskId:流程任务id[必传]
     * @param flowParams:包含流程相关参数的对象 - message:审批意见  [按需传输]
     *                               - createBy:办理人帐号[建议传]
     */
    Instance termination(Long taskId, FlowParams flowParams);

    /**
     * 终止流程，提前结束流程，将所有代办任务转历史
     *
     * @param instance:流程实例
     * @param task:流程任务
     * @param flowParams:包含流程相关参数的对象 - message:审批意见  [按需传输]
     *                               - createBy:办理人帐号[建议传]
     */
    Instance termination(Instance instance, Task task, FlowParams flowParams);

    /**
     * 根据instanceIds删除
     *
     * @param instanceIds
     * @return
     */
    boolean deleteByInsIds(List<Long> instanceIds);

    /**
     * 根据流程id+当前流程节点编码获取与之直接关联(其为源节点)的节点。 definitionId:流程id nodeCode:当前流程状态
     * skipType:跳转条件,没有填写的话不做校验
     *
     * @param NowNode
     * @param task
     * @param flowParams
     * @return
     */
    Node getNextNode(Node NowNode, Task task, FlowParams flowParams);

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

    /**
     * 转办任务,需要校验办理人权限
     *
     * @param taskId         任务id
     * @param flowParams 流程参数(包含当前处理人的权限，重新指定的权限标识（办理人）)
     */
    boolean transfer(Long taskId, FlowParams flowParams);

    /**
     * 转办任务 ignore默认为true，转办忽略权限校验，比如管理员权限
     *
     * @param taskId         任务id
     * @param flowParams 流程参数(包含当前处理人的权限，重新指定的权限标识（办理人）)
     * @param ignore 转办忽略权限校验（true - 忽略，false - 不忽略）
     * @param clear 清理当前任务的计划审批人（true - 清理，false - 不清理）
     */
    boolean transfer(Long taskId, FlowParams flowParams, boolean ignore, boolean clear);
    /**
     * 加减签,需要校验办理人权限
     *
     * @param taskId         任务id
     * @param flowParams 流程参数(包含当前处理人的权限，重新指定的权限标识（办理人）)
     */
    boolean signature(Long taskId, FlowParams flowParams);

    /**
     * 加减签 ignore默认为true，转办忽略权限校验，比如管理员权限
     *
     * @param taskId         任务id
     * @param flowParams 流程参数(包含当前处理人的权限，重新指定的权限标识（办理人）)
     * @param ignore 转办忽略权限校验（true - 忽略，false - 不忽略）
     */
    boolean signature(Long taskId, FlowParams flowParams, boolean ignore);

    /**
     * 委派,需要校验办理人权限(不清理计划审批人）
     *
     * @param taskId         任务id
     * @param flowParams 流程参数(包含当前处理人的权限，重新指定的权限标识（办理人）)
     */
    boolean depute(Long taskId, FlowParams flowParams);

    /**
     * 委派 ignore默认为true，转办忽略权限校验，比如管理员权限(不清理计划审批人）
     *
     * @param taskId         任务id
     * @param flowParams 流程参数(包含当前处理人的权限，重新指定的权限标识（办理人）)
     * @param ignore 转办忽略权限校验（true - 忽略，false - 不忽略）
     */
    boolean depute(Long taskId, FlowParams flowParams, boolean ignore);

    /**
     * 委派 ignore默认为true，转办忽略权限校验，比如管理员权限,需要自己判断是否需要清理计划审批人
     *
     * @param taskId         任务id
     * @param flowParams 流程参数(包含当前处理人的权限，重新指定的权限标识（办理人）)
     * @param ignore 转办忽略权限校验（true - 忽略，false - 不忽略）
     * @param clear 清理当前任务的计划审批人（true - 清理，false - 不清理）
     */
    boolean depute(Long taskId, FlowParams flowParams, boolean ignore, boolean clear);
    /**
     * 转办，委派，加减签等处理
     *
     * @param taskId         任务id
     * @param flowParams 流程参数(包含当前处理人的权限，重新指定的权限标识（办理人）)
     * @param ignore 为true忽略权限判断，可直接转办
     * @param circulationType 一个任务权限流转类型
     */
    boolean processedHandle(Long taskId, FlowParams flowParams, boolean ignore, CirculationType circulationType);
    /**
     * 并行网关，取结束节点类型，否则随便取id最大的
     *
     * @param tasks
     * @return
     */
    Task getNextTask(List<Task> tasks);


}
