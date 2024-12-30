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
import org.dromara.warm.flow.core.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class NodeConvertUtil {

    private static Map<String, NodeConvertAbstract> nodeConvertMap = new ConcurrentHashMap<>();

    static {
        nodeConvertMap.put(NodeType.START.getValue(), new StartNodeConvert());
        nodeConvertMap.put(NodeType.BETWEEN.getValue(), new BetweenNodeConvert());
        nodeConvertMap.put(NodeType.SERIAL.getValue(), new SerialNodeConvert());
        nodeConvertMap.put(NodeType.PARALLEL.getValue(), new ParallelNodeConvert());
        nodeConvertMap.put(NodeType.END.getValue(), new EndNodeConvert());
    }

    /**
     *   页面json  转换为node 和 skip, 请根据业务自行修改
     */
    public static List<Node> convert(List<Map<String,Object>> jsonArray, String startNodeId, String endNodeId) {
        List<Node> seaflowNodeList = new ArrayList<>();
        if(StringUtils.isEmpty(startNodeId)){
            startNodeId = (String) jsonArray.get(0).get("nodeId");
        }
        if(StringUtils.isEmpty(endNodeId)){
            endNodeId =  (String) jsonArray.get(jsonArray.size() - 1).get("nodeId");
        }

//        String currentNodeId = null;
        String nextNodeId = null;
        for(int i = 0; i < jsonArray.size(); i++) {
            Map<String,Object> node = jsonArray.get(i);
            String type = (String) node.get("nodeType");
            if((i + 1) < jsonArray.size()){
                Map<String,Object>  nextNode = jsonArray.get(i + 1);
                nextNodeId = (String) nextNode.get("nodeId");

                //当前节点 和下个节点 都为网关时候， 中间加个自动通过节点
                if(NodeType.isGateWay(NodeType.getKeyByValue((String) nextNode.get("nodeType")))
                    && NodeType.isGateWay(NodeType.getKeyByValue(type))){

                    String emptyNodeCode = UUID.randomUUID().toString();
                    Node emptyNode = FlowEngine.newNode();
                    emptyNode.setNodeCode(emptyNodeCode);
                    emptyNode.setNodeType(NodeType.BETWEEN.getKey());
                    // 设置空类别自动通过， 区分前端审批人为空的配置
                    emptyNode.setPermissionFlag("auto");


                    List<Skip> emptySkip = new ArrayList<>();
                    Skip emSkip = FlowEngine.newSkip();
                    emSkip.setSkipType(SkipType.PASS.getKey());
                    emSkip.setNextNodeCode(nextNodeId);
                    emptySkip.add(emSkip);
                    emptyNode.setSkipList(emptySkip);
                    seaflowNodeList.add(emptyNode);
                    // 更新下个节点的nodeId
                    nextNodeId = emptyNodeCode;
                }
            }else{
                nextNodeId = null;
            }



            NodeConvertAbstract nodeConvert = nodeConvertMap.get(type);

            if(nodeConvert != null) {
//                currentNodeId = node.getStr("nodeId");
                List<Node> convert = nodeConvert.convert(node, startNodeId, endNodeId, nextNodeId);
                seaflowNodeList.addAll(convert);
            }
        }
        return seaflowNodeList;
    }

}
