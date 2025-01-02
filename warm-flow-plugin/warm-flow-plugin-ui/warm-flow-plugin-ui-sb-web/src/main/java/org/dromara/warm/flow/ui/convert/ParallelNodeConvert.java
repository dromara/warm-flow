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
package org.dromara.warm.flow.ui.convert;

import org.dromara.warm.flow.core.FlowEngine;
import org.dromara.warm.flow.core.entity.Node;
import org.dromara.warm.flow.core.entity.Skip;
import org.dromara.warm.flow.core.enums.NodeType;
import org.dromara.warm.flow.core.enums.SkipType;

import java.util.*;

public class ParallelNodeConvert extends NodeConvertAbstract{

    @Override
    public List<Node> convert(Map<String, Object> jsonObject, String startNodeId, String endNodeId, String nextNodeId){
        // 并行网关，会在子节点 最前面加一个条件分支， 否则会出问题
        List<Node> seaflowNodeList = new ArrayList<>();
        String startParallelNodeId = (String)jsonObject.get("nodeId");
        Node serialNode = FlowEngine.newNode();
        serialNode.setNodeCode(startParallelNodeId);
        serialNode.setNodeType(NodeType.PARALLEL.getKey());
//        serialNode.setDirection("1");
        //加一个并行网关
        seaflowNodeList.add(serialNode);




        //前
//        seaflowNode startNode = getNode(jsonObject);
        // 后
        Node endNode = FlowEngine.newNode();
        endNode.setNodeCode(UUID.randomUUID().toString());

//        seaflowNodeList.add(startNode);
        List<List<Map<String,Object>>> childNodes =  (List<List<Map<String,Object>>>)jsonObject.get("childNodes");
        List<Skip>  startSkips = new ArrayList<>();
        for (int j = 0; j < childNodes.size(); j++) {
            List<Map<String,Object>> jsonArray = childNodes.get(j);
            jsonArray.get(0).put("nodeType", NodeType.BETWEEN.getValue());
            // 跳转 不变
            Skip startNodeSkip = FlowEngine.newSkip();
            startNodeSkip.setSkipType(SkipType.PASS.getKey());

            startNodeSkip.setNextNodeCode((String)jsonArray.get(0).get("nodeId"));
            startSkips.add(startNodeSkip);

            List<Node> convert = NodeConvertUtil.convert(jsonArray, startNodeId, endNodeId);
            //  子节点的最后一个 还要处理下连线问题
            Node seaflowNode = convert.get(convert.size() - 1);
            Skip flowSkip = FlowEngine.newSkip();
            flowSkip.setSkipType(SkipType.PASS.getKey());
            flowSkip.setNextNodeCode(endNode.getNodeCode());
            seaflowNode.getSkipList().add(flowSkip);

            seaflowNodeList.addAll(convert);

        }
        serialNode.setSkipList(startSkips);

        String emptyNodeCode  = UUID.randomUUID().toString();
        //并行网关（出） 连接空节点
        Skip parallelOutSkip = FlowEngine.newSkip();
        parallelOutSkip.setSkipType(SkipType.PASS.getKey());
        parallelOutSkip.setNextNodeCode(emptyNodeCode);
        endNode.setSkipList(Arrays.asList(parallelOutSkip));

        // 并行网关（出） 后面再加一个空节点
        Node emptyNode = FlowEngine.newNode();
        emptyNode.setNodeCode(emptyNodeCode);
        emptyNode.setNodeType(NodeType.BETWEEN.getKey());
        // 设置空类别自动通过， 区分前端审批人为空的配置
        String emptyApprove = "{\"type\":\"EMPTY\",\"value\":[]}";
//        emptyNode.setEmptyApprove(emptyApprove);
        // 多个用逗号分割， 这里先写死
//        emptyNode.setListenerType(String.join(",", Listener.LISTENER_ASSIGNMENT, Listener.LISTENER_CREATE));
//        emptyNode.setListenerPath(String.join("@@", DefAssignmentListener.class.getName(),
//                DefCreateListener.class.getName()));



        if(nextNodeId != null){
            List<Skip> endSkipList = new ArrayList<>();
            for (String nodeId : nextNodeId.split(",")) {
                Skip endSkip = FlowEngine.newSkip();
                endSkip.setSkipType(SkipType.PASS.getKey());
                endSkip.setNextNodeCode(nodeId);
                endSkipList.add(endSkip);

            }
            emptyNode.setSkipList(endSkipList);

        }


        seaflowNodeList.add(endNode);
        seaflowNodeList.add(emptyNode);
        return seaflowNodeList;
    }

    @Override
    public String getType() {
        return "parallel";
    }
}
