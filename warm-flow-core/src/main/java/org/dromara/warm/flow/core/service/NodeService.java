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

import org.dromara.warm.flow.core.dto.FlowCombine;
import org.dromara.warm.flow.core.dto.PathWayData;
import org.dromara.warm.flow.core.entity.Node;
import org.dromara.warm.flow.core.orm.service.IWarmService;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 流程节点Service接口
 *
 * @author warm
 * @since 2023-03-29
 */
public interface NodeService extends IWarmService<Node> {

    /**
     * 根据流程编码获取已发布流程节点集合
     *
     * @param flowCode 流程编码
     * @return List<Node>
     */
    List<Node> getPublishByFlowCode(String flowCode);

    /**
     * 根据流程编码获取开启的唯一流程的流程节点集合
     *
     * @param nodeCodes 流程节点code集合
     * @return List<Node>
     */
    List<Node> getByNodeCodes(List<String> nodeCodes, Long definitionId);

    /**
     * 根据节点id获取所有的前置节点集合
     * @param nodeId 节点id
     * @return 所有的前置节点集合
     */
    List<Node> previousNodeList(Long nodeId);

    /**
     * 根据流程定义id和当前节点code获取所有的前置节点集合
     *
     * @param definitionId 程定义id
     * @param nowNodeCode 当前节点code
     * @return 所有的前置节点集合
     */
    List<Node> previousNodeList(Long definitionId, String nowNodeCode);

    /**
     * 根据节点id获取所有的后置节点集合
     * @param nodeId 节点id
     * @return 所有的后置节点集合
     */
    List<Node> suffixNodeList(Long nodeId);

    /**
     * 根据流程定义id和当前节点code获取所有的后置节点集合
     *
     * @param definitionId 程定义id
     * @param nowNodeCode 当前节点code
     * @return 所有的后置点集合
     */
    List<Node> suffixNodeList(Long definitionId, String nowNodeCode);

    /**
     * 流程数据集合和当前节点code获取所有的后置节点集合
     *
     * @param nowNodeCode 当前节点code
     * @param flowCombine 流程数据集合
     * @return 所有的后置点集合
     */
    List<Node> suffixNodeList(String nowNodeCode, FlowCombine flowCombine);

    /**
     * 根据流程定义id获取流程节点集合
     * @param definitionId 流程定义id
     * @return 所有的节点集合
     */
    List<Node> getByDefId(Long definitionId);

    /**
     * 根据流程定义id和节点编码获取流程节点
     * @param definitionId 流程定义id
     * @return 节点
     */
    Node getByDefIdAndNodeCode(Long definitionId, String nodeCode);

    /**
     * 根据流程定义id获取开始节点
     * @param definitionId 流程定义id
     * @return Node
     */
    Node getStartNode(Long definitionId);

    /**
     * 根据流程定义id获取中间节点集合
     * @param definitionId 流程定义id
     * @return List<Node>
     */
    List<Node> getBetweenNode(Long definitionId);

    /**
     * 根据流程定义id获取结束节点
     * @param definitionId 流程定义id
     * @return Node
     */
    Node getEndNode(Long definitionId);

    /**
     * 根据流程定义和当前节点code获取下一节点,如是网关跳过取下一节点,并行网关返回多个节点
     * anyNodeCode不为空，则可跳转anyNodeCode节点
     *
     * @param definitionId  流程定义id
     * @param nowNodeCode   当前节点code
     * @param anyNodeCode   anyNodeCode不为空，则可跳转anyNodeCode节点（优先级最高）
     * @param skipType      跳转类型（PASS审批通过 REJECT退回）
     * @param variable      流程变量,下一个节点是网关需要判断跳转条件,并行网关返回多个节点
     * @return List<Node>
     * @author xiarg
     * @since 2024/8/21 16:48
     */
    List<Node> getNextNodeList(Long definitionId, String nowNodeCode, String anyNodeCode, String skipType,
                                     Map<String, Object> variable);

    /**
     * 根据当前节点获取下一节点
     * anyNodeCode不为空，则可跳转anyNodeCode节点
     * @param definitionId  流程定义id
     * @param nowNodeCode   当前节点code
     * @param anyNodeCode   anyNodeCode不为空，则可跳转anyNodeCode节点（优先级最高）
     * @param skipType      跳转类型（PASS审批通过 REJECT退回）
     * @return Node
     */
    Node getNextNode(Long definitionId, String nowNodeCode, String anyNodeCode, String skipType);


    /**
     * 当前节点获取下一节点,如是网关跳过取下一节点,并行网关返回多个节点
     * anyNodeCode不为空，则可跳转anyNodeCode节点
     *
     * @param nowNode   当前节点
     * @param anyNodeCode   anyNodeCode不为空，则可跳转anyNodeCode节点（优先级最高）
     * @param skipType      跳转类型（PASS审批通过 REJECT退回）
     * @param variable      流程变量,下一个节点是网关需要判断跳转条件,并行网关返回多个节点
     * @param pathWayData      办理过程中途径数据，用于渲染流程图
     * @param flowCombine 流程数据集合
     * @return List<Node>
     */
    List<Node> getNextNodeList(Node nowNode, String anyNodeCode, String skipType,Map<String, Object> variable,
                               PathWayData pathWayData, FlowCombine flowCombine);

    /**
     * 根据当前节点获取下一节点
     * anyNodeCode不为空，则可跳转anyNodeCode节点
     * @param nowNode 当前节点
     * @param anyNodeCode   anyNodeCode不为空，则可跳转anyNodeCode节点（优先级最高）
     * @param skipType      跳转类型（PASS审批通过 REJECT退回）
     * @param pathWayData      办理过程中途径数据，用于渲染流程图
     * @param flowCombine 流程数据集合
     * @return Node
     */
    Node getNextNode(Node nowNode, String anyNodeCode, String skipType, PathWayData pathWayData, FlowCombine flowCombine);

    /**
     * 校验是否网关节点,如果是重新获取新的后面的节点
     *
     * @param variable      流程变量
     * @param nextNode      下一个节点
     * @param pathWayData      办理过程中途径数据，用于渲染流程图
     * @param flowCombine 流程数据集合
     * @return List<Node>
     */
    List<Node> getNextByCheckGateway(Map<String, Object> variable, Node nextNode, PathWayData pathWayData
            , FlowCombine flowCombine);

    /**
     * 批量删除流程节点
     *
     * @param defIds 需要删除的数据主键集合
     * @return 结果
     */
    int deleteNodeByDefIds(Collection<? extends Serializable> defIds);

}
