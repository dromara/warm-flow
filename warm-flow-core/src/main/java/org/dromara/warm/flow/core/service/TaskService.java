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

import org.dromara.warm.flow.core.dto.FlowDto;
import org.dromara.warm.flow.core.dto.FlowParams;
import org.dromara.warm.flow.core.entity.Definition;
import org.dromara.warm.flow.core.entity.Instance;
import org.dromara.warm.flow.core.entity.Node;
import org.dromara.warm.flow.core.entity.Task;
import org.dromara.warm.flow.core.orm.service.IWarmService;

import java.util.List;

/**
 * 待办任务Service接口
 *
 * @author warm
 * @since 2023-03-29
 */
public interface TaskService extends IWarmService<Task> {

    /**
     * 根据任务id，流程跳转
     *
     * @param taskId:流程任务id[必传]
     * @param flowParams:包含流程相关参数的对象 - skipType: 跳转类型(PASS审批通过 REJECT退回) [必传]
     *                               - nodeCode: 如果指定节点,可任意跳转到对应节点，严禁任意退回选择后置节点 [按需传输]
     *                               - permissionFlag: 办理人权限标识，比如用户，角色，部门等, 流程设计时未设置办理人或者ignore为true可不传 [按需传输]
     *                               - message: 审批意见  [按需传输]
     *                               - handler: 办理人唯一标识 [建议传]
     *                               - variable: 流程变量 [按需传输,跳转条件放入流程变量<互斥网关必传>]
     *                               - flowStatus: 流程状态，自定义流程状态 [按需传输]
     *                               - ignore   忽略权限校验（比如管理员不校验），默认不忽略 [按需传输]
     */
    Instance skip(Long taskId, FlowParams flowParams);

    /**
     * 根据实例id，流程跳转，一般是开始节点后第一个节点，用来提交申请，此时不可有同时两个待办任务
     *
     * @param instanceId:流程实例id[必传]
     * @param flowParams:包含流程相关参数的对象 - skipType:跳转类型(PASS审批通过 REJECT退回) [必传]
     *                               - nodeCode: 如果指定节点,可任意跳转到对应节点，严禁任意退回选择后置节点 [按需传输]
     *      *                        - permissionFlag: 办理人权限标识，比如用户，角色，部门等, 流程设计时未设置办理人或者ignore为true可不传 [按需传输]
     *                               - message: 审批意见[按需传输]
     *                               - handler: 办理人唯一标识[建议传]
     *                               - variable: 流程变量[按需传输,跳转条件放入流程变量<互斥网关必传>]
     *                               - flowStatus: 流程状态，自定义流程状态[按需传输]
     *                               - ignore   忽略权限校验（比如管理员不校验），默认不忽略 [按需传输]
     * @return 流程实例
     */
    Instance skipByInsId(Long instanceId, FlowParams flowParams);

    /**
     * 驳回上一个任务
     * @param instanceId:流程实例id[必传]
     * @param flowParams:包含流程相关参数的对象 - permissionFlag: 办理人权限标识，比如用户，角色，部门等, 流程设计时未设置办理人或者ignore为true可不传 [按需传输]
     *                               - message: 审批意见  [按需传输]
     *                               - handler: 办理人唯一标识 [建议传]
     *                               - variable: 流程变量 [按需传输,跳转条件放入流程变量<互斥网关必传>]
     *                               - flowStatus: 流程状态，自定义流程状态 [按需传输]
     *                               - ignore   忽略权限校验（比如管理员不校验），默认不忽略 [按需传输]
     */
    Instance rejectLastByInsId(Long instanceId, FlowParams flowParams);

    /**
     * 驳回上一个任务
     * @param taskId:流程任务id[必传]
     * @param flowParams:包含流程相关参数的对象 - permissionFlag: 办理人权限标识，比如用户，角色，部门等, 流程设计时未设置办理人或者ignore为true可不传 [按需传输]
     *                               - message: 审批意见  [按需传输]
     *                               - handler: 办理人唯一标识 [建议传]
     *                               - variable: 流程变量 [按需传输,跳转条件放入流程变量<互斥网关必传>]
     *                               - flowStatus: 流程状态，自定义流程状态 [按需传输]
     *                               - ignore   忽略权限校验（比如管理员不校验），默认不忽略 [按需传输]
     */
    Instance rejectLast(Long taskId, FlowParams flowParams);

    /**
     * 驳回上一个任务
     * @param task:流程任务[必传]
     * @param flowParams:包含流程相关参数的对象 - permissionFlag: 办理人权限标识，比如用户，角色，部门等, 流程设计时未设置办理人或者ignore为true可不传 [按需传输]
     *                               - message: 审批意见  [按需传输]
     *                               - handler: 办理人唯一标识 [建议传]
     *                               - variable: 流程变量 [按需传输,跳转条件放入流程变量<互斥网关必传>]
     *                               - flowStatus: 流程状态，自定义流程状态 [按需传输]
     *                               - ignore   忽略权限校验（比如管理员不校验），默认不忽略 [按需传输]
     */
    Instance rejectLast(Task task, FlowParams flowParams);

    /**
     * 流程跳转
     *
     * @param flowParams:包含流程相关参数的对象 - skipType: 跳转类型(PASS审批通过 REJECT退回) [必传]
     *                               - nodeCode: 如果指定节点,可任意跳转到对应节点，严禁任意退回选择后置节点 [按需传输]
     *                               - permissionFlag: 办理人权限标识，比如用户，角色，部门等, 流程设计时未设置办理人或者ignore为true可不传 [按需传输]
     *                               - message: 审批意见 [按需传输]
     *                               - handler: 办理人唯一标识 [建议传]
     *                               - variable: 流程变量 [按需传输,跳转条件放入流程变量<互斥网关必传>]
     *                               - flowStatus: 流程状态，自定义流程状态 [按需传输]
     *                               - ignore   忽略权限校验（比如管理员不校验），默认不忽略 [按需传输]
     * @param task:流程任务[必传]
     */
    Instance skip(FlowParams flowParams, Task task);

    /**
     * 撤销
     *
     * @param instanceId        实例id [必传]
     * @param flowParams:包含流程相关参数的对象 - message: 审批意见  [按需传输]
     *                               - handler: 办理人唯一标识 [必传]
     *                               - variable: 流程变量 [按需传输,跳转条件放入流程变量<互斥网关必传>]
     *                               - flowStatus: 流程状态，自定义流程状态 [按需传输]
     *                               - ignore   忽略权限校验（比如管理员不校验），默认不忽略 [按需传输]
     */
    Instance revoke(Long instanceId, FlowParams flowParams);

    /**
     * 终止流程，提前结束流程，将所有待办任务转历史
     *
     * @param taskId: 流程任务id [必传]
     * @param flowParams:包含流程相关参数的对象 - message: 审批意见 [按需传输]
     *                               - permissionFlag: 办理人权限标识，比如用户，角色，部门等, 流程设计时未设置办理人或者ignore为true可不传 [按需传输]
     *                               - handler: 办理人唯一标识 [建议传]
     *                               - flowStatus: 流程状态，自定义流程状态 [按需传输]
     *                               - ignore   忽略权限校验（比如管理员不校验），默认不忽略 [按需传输]
     */
    Instance termination(Long taskId, FlowParams flowParams);

    /**
     * 终止流程，提前结束流程，将所有待办任务转历史
     *
     * @param task:流程任务
     * @param flowParams:包含流程相关参数的对象 - message: 审批意见  [按需传输]
     *                               - permissionFlag: 办理人权限标识，比如用户，角色，部门等, 流程设计时未设置办理人或者ignore为true可不传 [按需传输]
     *                               - handler: 办理人唯一标识 [建议传]
     *                               - flowStatus: 流程状态，自定义流程状态 [按需传输]
     *                               - ignore   忽略权限校验（比如管理员不校验），默认不忽略 [按需传输]
     */
    Instance termination(Task task, FlowParams flowParams);

    /**
     * 根据instanceIds删除
     *
     * @param instanceIds 流程实例id集合
     * @return boolean
     */
    boolean deleteByInsIds(List<Long> instanceIds);

    /**
     * 转办, 默认删除当然办理用户权限，转办后，当前办理不可办理
     *
     * @param taskId         修改的任务id [必传]
     * @param flowParams:包含流程相关参数的对象 - handler     当前办理人唯一标识 [必传]
     *                                     - permissionFlag 用户所拥有的权限标识[按需传输，ignore为false，则必传]
     *                                     - addHandlers    转办对象 [必传]
     *                                     - message        审批意见 [按需传输]
     *                                     - ignore   忽略权限校验（比如管理员不校验），默认不忽略 [按需传输]
     */
    boolean transfer(Long taskId, FlowParams flowParams);

    /**
     * 委派, 默认删除当然办理用户权限，委派后，当前办理不可办理
     *
     * @param taskId         修改的任务id [必传]
     * @param flowParams:包含流程相关参数的对象 - handler     当前办理人唯一标识 [必传]
     *                                     - permissionFlag 用户权限标识集合 [必传]
     *                                     - addHandlers    委托对象 [必传]
     *                                     - message        审批意见 [按需传输]
     *                                     - ignore   忽略权限校验（比如管理员不校验），默认不忽略 [按需传输]
     */
    boolean depute(Long taskId, FlowParams flowParams);

    /**
     * 加签，增加办理人
     *
     * @param taskId         修改的任务id [必传]
     * @param flowParams:包含流程相关参数的对象 - handler     当前办理人唯一标识 [必传]
     *                                     - permissionFlag 用户权限标识集合 [必传]
     *                                     - addHandlers    加签对象 [必传]
     *                                     - message        审批意见 [按需传输]
     *                                     - ignore   忽略权限校验（比如管理员不校验），默认不忽略 [按需传输]
     */
    boolean addSignature(Long taskId, FlowParams flowParams);

    /**
     * 减签，减少办理人
     *
     * @param taskId         修改的任务id [必传]
     * @param flowParams:包含流程相关参数的对象 - handler     当前办理人唯一标识 [必传]
     *                                     - permissionFlag 用户权限标识集合 [必传]
     *                                     - reductionHandlers 减签对象 [必传]
     *                                     - message        审批意见 [按需传输]
     *                                     - ignore   忽略权限校验（比如管理员不校验），默认不忽略 [按需传输]
     */
    boolean reductionSignature(Long taskId, FlowParams flowParams);

    /**
     * 修改办理人
     *
     * @param taskId         修改的任务id [必传]
     * @param flowParams:包含流程相关参数的对象 - handler     当前办理人唯一标识 [必传]
     *                                      - permissionFlag: 用户所拥有的权限标识[按需传输，ignore为false，则必传]
     *                                      - addHandlers: 增加办理人：加签，转办，委托[按需传输]
     *                                      - reductionHandlers: 减签对象：减签，委托[按需传输]
     *                                      - message: 审批意见[按需传输]
     *                                      - cooperateType: 协作方式(2转办 3委派 6加签 7减签）[按需传输]
     *                                      - ignore: 转办忽略权限校验,默认忽略（true：忽略，false：不忽略）[按需传输]
     */
    boolean updateHandler(Long taskId, FlowParams flowParams);

    /**
     * 设置流程待办任务对象
     *
     * @param node 节点
     * @param instance 流程实例
     * @param flowParams 流程参数
     * @return Task
     */
    Task addTask(Node node, Instance instance, Definition definition, FlowParams flowParams);

    /**
     * 根据流程实例id获取流程任务集合
     * @param instanceId 流程实例id
     * @return 任务集合
     */
    List<Task> getByInsId(Long instanceId);

    /**
     * 获取表单及数据(使用表单场景)
     *
     * @param taskId
     * @param flowParams
     * @return
     */
    FlowDto load(Long taskId, FlowParams flowParams);

    /**
     * 获取表单及数据(使用表单场景)
     *
     * @param hisTaskId
     * @param flowParams
     * @return
     */
    FlowDto hisLoad(Long hisTaskId, FlowParams flowParams);
}
