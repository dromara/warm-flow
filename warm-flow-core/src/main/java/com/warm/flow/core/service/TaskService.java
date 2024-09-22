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
     *                               - nodeCode: 节点编码 [如果指定节点,可任意跳转到对应节点,按需传输]
     *                               - permissionFlag: 办理人权限标识，比如用户，角色，部门等[只有未设置办理人时可不传]
     *                               - message: 审批意见  [按需传输]
     *                               - handler: 办理人唯一标识[建议传]
     *                               - variable: 流程变量[按需传输,跳转条件放入流程变量<互斥网关必传>]
     *                               - flowStatus: 流程状态，自定义流程状态[按需传输]
     */
    Instance skip(Long taskId, FlowParams flowParams);

    /**
     * 流程跳转
     *
     * @param flowParams:包含流程相关参数的对象 - skipType: 跳转类型(PASS审批通过 REJECT退回) [必传]
     *                               - nodeCode: 节点编码 [如果指定节点,可任意跳转到对应节点,按需传输]
     *                               - permissionFlag: 办理人权限标识，比如用户，角色，部门等[只有未设置办理人时可不传]
     *                               - message: 审批意见  [按需传输]
     *                               - handler: 办理人唯一标识[建议传]
     *                               - variable: 流程变量[按需传输,跳转条件放入流程变量<互斥网关必传>]
     *                               - flowStatus: 流程状态，自定义流程状态[按需传输]
     * @param task:流程任务[必传]
     */
    Instance skip(FlowParams flowParams, Task task);

    /**
     * 终止流程，提前结束流程，将所有待办任务转历史
     *
     * @param taskId:流程任务id[必传]
     * @param flowParams:包含流程相关参数的对象 - message: 审批意见  [按需传输]
     *                               - handler: 办理人唯一标识[建议传]
     *                               - flowStatus: 流程状态，自定义流程状态[按需传输]
     */
    Instance termination(Long taskId, FlowParams flowParams);

    /**
     * 终止流程，提前结束流程，将所有待办任务转历史
     *
     * @param instance:流程实例
     * @param task:流程任务
     * @param flowParams:包含流程相关参数的对象 - message: 审批意见  [按需传输]
     *                               - handler: 办理人唯一标识[建议传]
     *                               - flowStatus: 流程状态，自定义流程状态[按需传输]
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
     * @param modifyHandler: 修改办理人参数的对象 - taskId:修改的任务id[必传]
     *                               - curUser:办理人唯一标识[按需传输]
     *                               - ignore: 转办忽略权限校验（true：忽略，false：不忽略）[按需传输]
     *                               - permissionFlag: 用户所拥有的权限标识[按需传输，ignore为false，则必传]
     *                               - addHandlers: 增加办理人：加签，转办，委托[按需传输]
     *                               - reductionHandlers: 减少办理人：减签，委托[按需传输]
     *                               - message: 审批意见[按需传输]
     *                               - cooperateType: 协作方式(1审批 2转办 3委派 4会签 5票签 6加签 7减签）[按需传输]
     */
    boolean updateHandler(ModifyHandler modifyHandler);

    /**
     * 校验是否网关节点,如果是重新获取新的后面的节点
     *
     * @param flowParams
     * @param nextNode
     * @return
     */
    @Deprecated
    List<Node> getNextByCheckGateWay(FlowParams flowParams, Node nextNode);

    /**
     * 设置流程待办任务对象
     *
     * @param node
     * @param instance
     * @param flowParams
     * @return
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
     * @param tasks
     * @return
     */
    Task getNextTask(List<Task> tasks);

    /**
     * 撤销 (校验下个节点是否已经处理完毕，如果处理完毕，不能撤销)
     * 1、只能撤销自己处理过，下一个新任务还未被办理的节点
     * 2、撤销之后返回到当前节点未处理状态
     * 3、处理人取历史任务表中得处理人
     * 4、结束节点不能撤销
     * 5、记录留痕
     * 流程实例状态，我根据他传的值改实例状态，历史任务表状态是这次操作记录的时候，我保存数据用这个历史任务状态。
     *
     * @param flowParams            流程状态，历史任务状态
     *                              nodeCode-节点编码（必输）
     * @param modifyHandler         当前处理人，当前处理人的权限
     *                              taskId-任务id（必输）
     * @return Task
     * @author xiarg
     * @date 2024/9/22 13:59
     */
    Task revoke(FlowParams flowParams, ModifyHandler modifyHandler);

    /**
     * 撤销
     * 1、只能撤销自己处理过，下一个新任务还未被办理的节点
     * 2、撤销之后返回到当前节点未处理状态
     * 3、处理人取历史任务表中得处理人
     * 4、结束节点不能撤销
     * 5、记录留痕
     * 流程实例状态，我根据他传的值改实例状态，历史任务表状态是这次操作记录的时候，我保存数据用这个历史任务状态。
     *
     * @param flowParams            nodeCode-节点编码（必输）
     *                              hisStatus-历史任务表状态(自定义了流程状态就必输)
     *                              hisTaskExt-业务详情扩展字段
     *                              message-审批意见
     * @param modifyHandler         curUser-当前处理人（必输）
     *                              taskId-任务id（必输）
     * @param checkNextHandled      是否校验下一个的节点是否已经处理了
     * @return Task
     * @author xiarg
     * @date 2024/9/22 13:59
     */
    Task revoke(FlowParams flowParams, ModifyHandler modifyHandler, boolean checkNextHandled);
    /**
     * 取回
     * 1、流程发起人发起
     * 2、取回之后到流程发起节点
     * 3、记录留痕
     *
     * @param flowParams        flowStatus-流程状态（必输）
     *                          hisStatus-历史任务状态（自定义了流程状态就必输）
     *                          variable-流程不变量
     *                          hisTaskExt-业务详情扩展字段
     *                          message-审批意见
     * @param modifyHandler     curUser-当前处理人（必输）
     *                          permissionFlag-当前处理人的权限（必输）
     *                          taskId-任务id（必输）
     * @return Task
     * @author Instance
     * @date 2024/9/22 13:59
     */
    Instance retrieve(FlowParams flowParams, ModifyHandler modifyHandler);
}
