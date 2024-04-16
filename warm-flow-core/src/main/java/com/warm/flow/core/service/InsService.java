package com.warm.flow.core.service;

import com.warm.flow.core.dto.FlowParams;
import com.warm.flow.core.entity.Instance;
import com.warm.flow.core.orm.service.IWarmService;

import java.util.List;

/**
 * 流程实例Service接口
 *
 * @author warm
 * @date 2023-03-29
 */
public interface InsService extends IWarmService<Instance> {

    /**
     * 根据开始的节点,业务id集合开启流程
     *
     * @param businessId: 业务id[必传]
     * @param flowParams: 包含流程相关参数的对象
     *                    - flowCode:流程编码 [必传]
     *                    - createBy:创建人帐号[建议传]
     *                    - nickname:创建人昵称[按需传输]
     *                    - tenantId:租户id [按需传输]
     *                    - variable:流程变量[按需传输]
     *                    - ext:扩展字段[按需传输]
     * @return
     */
    Instance start(String businessId, FlowParams flowParams);


    /**
     * 根据实例id，流程跳转，一般是开始节点后第一个节点，用来提交申请，此时不可有同时两个代办任务
     *
     * @param instanceId:流程实例id[必传]
     * @param flowParams:包含流程相关参数的对象 - skipType:跳转类型(PASS审批通过 REJECT驳回) [必传]
     *                               - nodeCode:节点编码 [如果指定跳转节点,必传]
     *                               - permissionFlag:办理人权限标识[按需传输]
     *                               - message:审批意见  [按需传输]
     *                               - createBy:办理人帐号[建议传]
     *                               - nickname:办理人昵称[按需传输]
     *                               - variable:流程变量[按需传输,跳转条件放入流程变量<互斥网关必传>]
     *                               - variableTask:任务变量[按需传输]
     * @return
     */
    Instance skipByInsId(Long instanceId, FlowParams flowParams);

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
    @Deprecated
    Instance skip(Long taskId, FlowParams flowParams);

    /**
     * 根据实例id，删除流程
     *
     * @param instanceIds: 流程实例集合[必传]
     * @return
     */
    boolean remove(List<Long> instanceIds);

}
