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
import com.warm.flow.core.entity.HisTask;
import com.warm.flow.core.entity.Node;
import com.warm.flow.core.entity.Task;
import com.warm.flow.core.entity.User;
import com.warm.flow.core.orm.service.IWarmService;

import java.math.BigDecimal;
import java.util.List;

/**
 * 历史任务记录Service接口
 *
 * @author warm
 * @date 2023-03-29
 */
public interface HisTaskService extends IWarmService<HisTask> {

    /**
     * 根据任务id和协作类型查询
     *
     * @param taskId
     * @param cooperateTypes
     * @return
     */
    List<HisTask> listByTaskIdAndCooperateTypes(Long taskId, Integer... cooperateTypes);

    /**
     * 根据instanceId获取未退回的历史记录
     *
     * @param instanceId
     */
    List<HisTask> getNoReject(Long instanceId);

    /**
     * 根据nodeCode和targetNodeCode获取未退回的历史记录
     * @param nodeCode
     * @param targetNodeCode
     * @param hisTasks
     * @return
     */
    HisTask getNoReject(String nodeCode, String targetNodeCode, List<HisTask> hisTasks);

    /**
     * 根据instanceId和流程编码获取未退回的历史记录
     *
     * @param instanceId
     * @param nodeCodes
     * @return
     */
    List<HisTask> getByInsAndNodeCodes(Long instanceId, List<String> nodeCodes);

    /**
     * 根据instanceIds删除
     *
     * @param instanceIds
     * @return
     */
    boolean deleteByInsIds(List<Long> instanceIds);

    /**
     * 设置流程历史任务信息集合
     *
     * @param task       当前任务
     * @param nextNodes  后续任务
     * @param flowParams 参数
     * return List<HisTask> 历史任务集合
     */
    List<HisTask> setSkipInsHis(Task task, List<Node> nextNodes, FlowParams flowParams);

    /**
     * 设置协作历史记录
     *
     * @param task          当前任务
     * @param node          当然任务节点
     * @param flowParams    参数
     * @param collaborators 协作人
     * @return List<HisTask> 历史任务集合
     */
    List<HisTask> setCooperateHis(Task task, Node node, FlowParams flowParams
            , List<String> collaborators);

    /**
     * 委派历史任务
     *
     * @param task          当前任务
     * @param flowParams    参数
     * @param entrustedUser 委托人
     * @return HisTask 历史任务
     */
    HisTask setDeputeHisTask(Task task, FlowParams flowParams, User entrustedUser);

    /**
     * 设置会签票签历史任务
     *
     * @param task       当前任务
     * @param flowParams 参数
     * @param nodeRatio  节点比率
     * @param isPass     是否通过
     * @return HisTask 历史任务
     */
    HisTask setSignHisTask(Task task, FlowParams flowParams, BigDecimal nodeRatio, boolean isPass);

    /**
     * 自动完成历史任务
     *
     * @param flowParams 流程参数
     * @param flowStatus 流程状态
     * @param task       当前任务
     * @param userList   用户列表
     */
    List<HisTask> autoHisTask(FlowParams flowParams, String flowStatus, Task task, List<User> userList, Integer cooperateType);

    /**
     * 设置流程历史任务信息
     *
     * @param task              当前任务
     * @param nextNode          跳转的节点
     * @param flowParams        流程参数
     * @return HisTask          历史任务
     * @author xiarg
     * @date 2024/9/30 11:59
     */
    HisTask setSkipHisTask(Task task, Node nextNode, FlowParams flowParams);

}
