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
package com.warm.flow.core.service;

import com.warm.flow.core.dto.FlowParams;
import com.warm.flow.core.dto.ModifyHandler;
import com.warm.flow.core.entity.Definition;
import com.warm.flow.core.entity.Instance;
import com.warm.flow.core.entity.Node;
import com.warm.flow.core.entity.Task;
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
     * @param flowParams:包含流程相关参数的对象 - skipType: 跳转类型(PASS审批通过 REJECT退回) [必传]
     *                               - nodeCode: 如果指定节点,可任意跳转到对应节点 [按需传输]
     *                               - permissionFlag: 办理人权限标识，比如用户，角色，部门等 [只有未设置办理人时可不传]
     *                               - message: 审批意见  [按需传输]
     *                               - handler: 办理人唯一标识 [建议传]
     *                               - variable: 流程变量 [按需传输,跳转条件放入流程变量<互斥网关必传>]
     *                               - flowStatus: 流程状态，自定义流程状态 [按需传输]
     */
    Instance skip(Long taskId, FlowParams flowParams);

    /**
     * 流程跳转
     *
     * @param flowParams:包含流程相关参数的对象 - skipType: 跳转类型(PASS审批通过 REJECT退回) [必传]
     *                               - nodeCode: 节点编码 [如果指定节点,可任意跳转到对应节点,按需传输]
     *                               - permissionFlag: 办理人权限标识，比如用户，角色，部门等[只有未设置办理人时可不传]
     *                               - message: 审批意见 [按需传输]
     *                               - handler: 办理人唯一标识 [建议传]
     *                               - variable: 流程变量 [按需传输,跳转条件放入流程变量<互斥网关必传>]
     *                               - flowStatus: 流程状态，自定义流程状态 [按需传输]
     * @param task:流程任务[必传]
     */
    Instance skip(FlowParams flowParams, Task task);

    /**
     * 终止流程，提前结束流程，将所有待办任务转历史
     *
     * @param taskId:流程任务id[必传]
     * @param flowParams:包含流程相关参数的对象 - message: 审批意见  [按需传输]
     *                               - handler: 办理人唯一标识 [建议传]
     *                               - flowStatus: 流程状态，自定义流程状态 [按需传输]
     */
    Instance termination(Long taskId, FlowParams flowParams);

    /**
     * 终止流程，提前结束流程，将所有待办任务转历史
     *
     * @param instance:流程实例
     * @param task:流程任务
     * @param flowParams:包含流程相关参数的对象 - message: 审批意见  [按需传输]
     *                               - handler: 办理人唯一标识 [建议传]
     *                               - flowStatus: 流程状态，自定义流程状态 [按需传输]
     */
    Instance termination(Instance instance, Task task, FlowParams flowParams);

    /**
     * 根据instanceIds删除
     *
     * @param instanceIds 主键
     * @return boolean
     */
    boolean deleteByInsIds(List<Long> instanceIds);

    /**
     * 转办, 默认删除当然办理用户权限，转办后，当前办理不可办理
     *
     * @param taskId         修改的任务id
     * @param curUser        当前办理人唯一标识
     * @param permissionFlag 用户权限标识集合
     * @param addHandlers    增加办理人：加签，转办，委托
     * @param message        审批意见
     */
    boolean transfer(Long taskId, String curUser, List<String> permissionFlag, List<String> addHandlers, String message);

    /**
     * 委派, 默认删除当然办理用户权限，转办后，当前办理不可办理
     *
     * @param taskId         修改的任务id
     * @param curUser        当前办理人唯一标识
     * @param permissionFlag 用户权限标识集合
     * @param addHandlers    增加办理人：加签，转办，委托
     * @param message        审批意见
     */
    boolean depute(Long taskId, String curUser, List<String> permissionFlag, List<String> addHandlers, String message);

    /**
     * 加签，增加办理人
     *
     * @param taskId         修改的任务id
     * @param curUser        当前办理人唯一标识
     * @param permissionFlag 用户权限标识集合
     * @param addHandlers    增加办理人：加签，转办，委托
     * @param message        审批意见
     */
    boolean addSignature(Long taskId, String curUser, List<String> permissionFlag, List<String> addHandlers, String message);

    /**
     * 减签，减少办理人
     *
     * @param taskId            修改的任务id
     * @param curUser           当前办理人唯一标识
     * @param permissionFlag    用户权限标识集合
     * @param reductionHandlers 增加办理人：加签，转办，委托
     * @param message           审批意见
     */
    boolean reductionSignature(Long taskId, String curUser, List<String> permissionFlag, List<String> reductionHandlers, String message);

    /**
     * 修改办理人
     * @param modifyHandler: 修改办理人参数的对象 - taskId:修改的任务id [必传]
     *                               - curUser:办理人唯一标识 [按需传输]
     *                               - ignore: 转办忽略权限校验（true：忽略，false：不忽略）[按需传输]
     *                               - permissionFlag: 用户所拥有的权限标识 [按需传输，ignore为false，则必传]
     *                               - addHandlers: 增加办理人：加签，转办，委托 [按需传输]
     *                               - reductionHandlers: 减少办理人：减签，委托 [按需传输]
     *                               - message: 审批意见 [按需传输]
     *                               - cooperateType: 协作方式(1审批 2转办 3委派 4会签 5票签 6加签 7减签）[按需传输]
     */
    boolean updateHandler(ModifyHandler modifyHandler);

    /**
     * 设置流程待办任务对象
     *
     * @param node        节点
     * @param instance    流程实例
     * @param flowParams 流程参数
     * @return Task
     */
    Task addTask(Node node, Instance instance, Definition definition, FlowParams flowParams);

    /**
     * 设置流程实例和代码任务流程状态
     *
     * @param nodeType 节点类型（开始节点、中间节点、结束节点）
     * @param skipType 流程条件
     */
    String setFlowStatus(Integer nodeType, String skipType);

    /**
     * 并行网关，取结束节点类型，否则随便取id最大的
     *
     * @param tasks 任务集合
     * @return Task
     */
    Task getNextTask(List<Task> tasks);

    /**
     * 撤销
     *
     * @param flowParams        flowStatus: 自定义流程状态 [按需传输]
     *                          hisStatus: 自定义历史任务状态 [按需传输]
     *                          variable: 流程变量 [按需传输]
     *                          hisTaskExt: 业务详情扩展字段  [按需传输]
     *                          message: 审批意见  [按需传输]
     *                          handler: 当前处理人 [必输]
     *                          nodeCode: 撤销到的节点 [必输]
     *                          permissionFlag: 当前处理人的权限 [必输]
     * @param taskId            任务id [必输]
     * @return Task             取回到的节点任务
     * @author xiarg
     * @date 2024/9/22 13:59
     */
    Task revoke(FlowParams flowParams, Long taskId);

    /**
     * 取回
     *
     * @param flowParams        flowStatus: 自定义流程状态 [按需传输]
     *                          hisStatus: 自定义历史任务状态 [按需传输]
     *                          nodeCode: 自定义取回到的节点编码，如果没传nodeCode，则默认跳转开始节点 [按需传输]
     *                          hisTaskExt: 业务详情扩展字段 [按需传输]
     *                          message: 审批意见 [按需传输]
     *                          handler: 当前处理人 [必输]
     * @param instanceId        实例id [必输]
     * @return Task             取回到的节点任务
     * @author xiarg
     * @date 2024/9/22 13:59
     */
    Task initiatorRetrieve(FlowParams flowParams, Long instanceId);

    /**
     * 取回
     *
     * @param flowParams        flowStatus: 自定义流程实例状态 [必输]
     *                          hisStatus: 自定义历史任务状态 [按需传输]
     *                          hisTaskExt: 业务详情扩展字段 [按需传输]
     *                          message: 审批意见 [按需传输]
     *                          nodeCode: 取回到的节点编码 [必输]
     *                          handler: 当前处理人 [必输]
     * @param instance          流程实例 [必输]
     * @return Task             取回到的节点任务
     * @author xiarg
     * @date 2024/9/22 13:59
     */
    Task commonRetrieve(FlowParams flowParams, Instance instance);
}
