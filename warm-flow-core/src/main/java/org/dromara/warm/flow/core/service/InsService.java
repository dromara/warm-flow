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

import org.dromara.warm.flow.core.dto.FlowParams;
import org.dromara.warm.flow.core.entity.Instance;
import org.dromara.warm.flow.core.orm.service.IWarmService;

import java.util.List;

/**
 * 流程实例Service接口
 *
 * @author warm
 * @since 2023-03-29
 */
public interface InsService extends IWarmService<Instance> {

    /**
     * 传入业务id开启流程
     *
     * @param businessId: 业务id[必传]
     * @param flowParams: 包含流程相关参数的对象
     *                    - flowCode: 流程编码 [必传]
     *                    - handler: 办理人唯一标识[建议传]
     *                    - variable: 流程变量[按需传输]
     *                    - flowStatus: 流程状态，自定义流程状态[按需传输]
     *                    - ext: 扩展字段，预留给业务系统使用[按需传输]
     * @return
     */
    Instance start(String businessId, FlowParams flowParams);


    /**
     * 根据实例id，流程跳转，一般是开始节点后第一个节点，用来提交申请，此时不可有同时两个待办任务
     *
     * @param instanceId:流程实例id[必传]
     * @param flowParams:包含流程相关参数的对象 - skipType:跳转类型(PASS审批通过 REJECT退回) [必传]
     *                               - nodeCode: 节点编码 [如果指定节点,可任意跳转到对应节点,按需传输]
     *                               - permissionFlag: 办理人权限标识，比如用户，角色，部门等[只有未设置办理人时可不传]
     *                               - message: 审批意见[按需传输]
     *                               - handler: 办理人唯一标识[建议传]
     *                               - variable: 流程变量[按需传输,跳转条件放入流程变量<互斥网关必传>]
     *                               - flowStatus: 流程状态，自定义流程状态[按需传输]
     *                               - ignore   转办忽略权限校验，默认不忽略（true：忽略，false：不忽略）[按需传输]
     * @return
     */
    Instance skipByInsId(Long instanceId, FlowParams flowParams);

    /**
     * 终止流程，提前结束流程，将所有待办任务转历史
     *
     * @param instanceId:流程实例id[必传]
     * @param flowParams:包含流程相关参数的对象 - message: 审批意见  [按需传输]
     *                               - handler: 办理人唯一标识[建议传]
     *                               - flowStatus: 流程状态，自定义流程状态[按需传输]
     *                               - ignore   转办忽略权限校验，默认不忽略（true：忽略，false：不忽略）[按需传输]
     */
    Instance termination(Long instanceId, FlowParams flowParams);

    /**
     * 根据实例ids，删除流程
     *
     * @param instanceIds: 流程实例集合[必传]
     */
    boolean remove(List<Long> instanceIds);

    /**
     * 激活实例
     * @param id 流程实例id: [必传]
     */
    boolean active(Long id);

    /**
     * 挂起实例，流程实例挂起后，该流程实例无法继续流转
     * @param id 流程实例id: [必传]
     */
    boolean unActive(Long id);
}
