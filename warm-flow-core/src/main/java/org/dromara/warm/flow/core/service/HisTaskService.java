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
import org.dromara.warm.flow.core.entity.HisTask;
import org.dromara.warm.flow.core.entity.Node;
import org.dromara.warm.flow.core.entity.Task;
import org.dromara.warm.flow.core.entity.User;
import org.dromara.warm.flow.core.orm.service.IWarmService;

import java.math.BigDecimal;
import java.util.List;

/**
 * 历史任务记录Service接口
 *
 * @author warm
 * @since 2023-03-29
 */
public interface HisTaskService extends IWarmService<HisTask> {

    /**
     * 根据任务id和协作类型查询
     *
     * @param taskId 任务id
     * @param cooperateTypes 协作类型集合
     * @return List<HisTask>
     */
    List<HisTask> listByTaskIdAndCooperateTypes(Long taskId, Integer... cooperateTypes);

    /**
     * 根据实例Id和节点编码查询
     *
     * @param instanceId 流程实例id
     * @param nodeCodes 节点编码集合
     * @return  List<HisTask>
     */
    List<HisTask> getByInsAndNodeCodes(Long instanceId, List<String> nodeCodes);

    /**
     * 根据instanceIds删除
     *
     * @param instanceIds 流程实例id集合
     * @return boolean
     */
    boolean deleteByInsIds(List<Long> instanceIds);

    /**
     * 设置流程历史任务信息
     *
     * @param task       当前任务
     * @param nextNodes  后续任务
     * @param flowParams 参数
     */
    HisTask setSkipInsHis(Task task, List<Node> nextNodes, FlowParams flowParams);

    /**
     * 设置流程历史任务信息
     *
     * @param taskList   当前任务集合
     * @param nextNodes  后续任务
     * @param flowParams 参数
     */
    List<HisTask> setSkipHisList(List<Task> taskList, List<Node> nextNodes, FlowParams flowParams);

    /**
     * 设置协作历史记录
     *
     * @param task          当前任务
     * @param node          当然任务节点
     * @param flowParams    参数
     * @param collaborators 协作人
     */
    HisTask setCooperateHis(Task task, Node node, FlowParams flowParams
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
     * 设置流程历史任务信息
     *
     * @param task              当前任务
     * @param nextNode          跳转的节点
     * @param flowParams        流程参数
     * @return HisTask          历史任务
     * @author xiarg
     * @since 2024/9/30 11:59
     */
    HisTask setSkipHisTask(Task task, Node nextNode, FlowParams flowParams);

    /**
     * 根据流程实例id查询历史任务
     * @param instanceId 流程实例id
     * @return 历史记录集合
     */
    List<HisTask> getByInsId(Long instanceId);

    @Deprecated
    List<HisTask> getNoReject(Long instanceId);

    @Deprecated
    HisTask getNoReject(String nodeCode, String targetNodeCode, List<HisTask> hisTasks);

}
