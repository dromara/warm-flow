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
     * 根据流程编码获取流程节点集合
     *
     * @param flowCode
     * @return
     */
    List<Node> getByFlowCode(String flowCode);

    /**
     * 根据流程编码获取开启的唯一流程的流程节点集合
     *
     * @param nodeCodes
     * @return
     */
    List<Node> getByNodeCodes(List<String> nodeCodes, Long definitionId);

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
     * 根据流程定义和当前节点code获取下一节点(不考虑下个节点是网关的情况)
     * anyNodeCode不为空，则可跳转anyNodeCode节点
     *
     * @param definitionId  流程定义id
     * @param nowNodeCode   当前节点code
     * @param anyNodeCode   anyNodeCode不为空，则可跳转anyNodeCode节点（优先级最高）
     * @param skipType      跳转类型（PASS审批通过 REJECT退回）
     * @return List<Node>
     * @author xiarg
     * @since 2024/8/21 16:48
     */
    Node getNextNode(Long definitionId, String nowNodeCode, String anyNodeCode, String skipType);

    /**
     * 批量删除流程节点
     *
     * @param defIds 需要删除的数据主键集合
     * @return 结果
     */
    int deleteNodeByDefIds(Collection<? extends Serializable> defIds);

    /**
     * 校验是否网关节点,如果是重新获取新的后面的节点
     *
     * @param variable      流程变量
     * @param nextNode      下一个节点
     * @return List<Node>
     * @author xiarg
     * @since 2024/8/21 16:48
     */
    List<Node> getNextByCheckGateway(Map<String, Object> variable, Node nextNode);
}
