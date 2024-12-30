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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SerialNodeConvert extends NodeConvertAbstract{


    @Override
    public List<Node> convert(Map<String,Object> jsonObject, String startNodeId, String endNodeId, String nextNodeId){

        List<Node> seaflowNodeList = new ArrayList<>();
        Node node = FlowEngine.newNode();
        seaflowNodeList.add(node);
        List<Skip> serialSkip = new ArrayList<>();
        // 获取子节点
        List<List<Map<String,Object>>> childNodes = (List<List<Map<String,Object>>>)jsonObject.get("childNodes");
        // childNodes是两层数组
        for (int j = 0; j < childNodes.size(); j++) {
            List<Map<String,Object>> jsonArray = childNodes.get(j);
            // 跳转节点

            if(jsonArray.size() > 1){


                String  subNextNodeId = (String)jsonArray.get(1).get("nodeId");
                String  subNextNodeType = (String)jsonArray.get(1).get("nodeType");
                // 子节点直接连的还是网关节点，需要加一个node
                if(NodeType.isGateWay(NodeType.getKeyByValue(subNextNodeType))){
                    String emptyNodeCode = UUID.randomUUID().toString();
                    Node emptyNode = FlowEngine.newNode();
                    emptyNode.setNodeCode(emptyNodeCode);
                    emptyNode.setNodeType(NodeType.BETWEEN.getKey());
                    // 设置空类别自动通过， 区分前端审批人为空的配置
                    String emptyApprove = "{\"type\":\"EMPTY\",\"value\":[]}";
//                    emptyNode.setEmptyApprove(emptyApprove);
                    // 多个用逗号分割， 这里先写死
//                    emptyNode.setListenerType(String.join(",", Listener.LISTENER_ASSIGNMENT, Listener.LISTENER_CREATE));
//                    emptyNode.setListenerPath(String.join("@@", DefAssignmentListener.class.getName(),
//                            DefCreateListener.class.getName()));

                    List<Skip> emptySkip = new ArrayList<>();
                    Skip emSkip = FlowEngine.newSkip();
                    emSkip.setSkipType(SkipType.PASS.getKey());
                    emSkip.setNextNodeCode(subNextNodeId);
                    emptySkip.add(emSkip);
                    emptyNode.setSkipList(emptySkip);
                    seaflowNodeList.add(emptyNode);

                    Skip flowSkip =  createSkip(jsonArray.get(0), emptyNodeCode);
                    serialSkip.add(flowSkip);
                    // 替换
//                    subNextNodeId = emptyNodeCode;
                }else{
                    Skip flowSkip =  createSkip(jsonArray.get(0), subNextNodeId);
                    serialSkip.add(flowSkip);
                }


                // 去掉第一个
                ArrayList<Map<String, Object>> subArray = new ArrayList<>(jsonArray.subList(1, jsonArray.size()));
                List<Node> convert = NodeConvertUtil.convert(subArray, startNodeId, endNodeId);
                // 如果是每个子节点的最后一个 跳转到父节点的下个节点
                Node seaflowNode = convert.get(convert.size() - 1);

                List<Skip> skipList = seaflowNode.getSkipList();

                String[] nodeIds = nextNodeId.split(",");
                for(String nodeId : nodeIds){
                    //  设置跳转
                    Skip skipParentNext = FlowEngine.newSkip();
                    skipParentNext.setSkipType(SkipType.PASS.getKey());
                    skipParentNext.setNextNodeCode(nodeId);
                    skipList.add(skipParentNext);
                }


                seaflowNodeList.addAll(convert);
            }else {
                String emptyNodeCode = UUID.randomUUID().toString();

                // 加个空任务, 多个网关直连走不通
                Node emptyNode = FlowEngine.newNode();
                emptyNode.setNodeCode(emptyNodeCode);
                emptyNode.setNodeType(NodeType.BETWEEN.getKey());
                // 设置空类别自动通过， 区分前端审批人为空的配置
                String emptyApprove = "{\"type\":\"EMPTY\",\"value\":[]}";
//                emptyNode.setEmptyApprove(emptyApprove);
                // 多个用逗号分割， 这里先写死
//                emptyNode.setListenerType(String.join(",", Listener.LISTENER_ASSIGNMENT, Listener.LISTENER_CREATE));
//                emptyNode.setListenerPath(String.join("@@", DefAssignmentListener.class.getName(),
//                        DefCreateListener.class.getName()));

                // 节点跳转
                String[] nodeIds = nextNodeId.split(",");
                List<Skip> emptySkip = new ArrayList<>();
                for(String nodeId : nodeIds){
                    //  设置跳转
                    Skip skipParentNext = FlowEngine.newSkip();
                    skipParentNext.setSkipType(SkipType.PASS.getKey());
                    skipParentNext.setNextNodeCode(nodeId);
                    emptySkip.add(skipParentNext);
                }
                emptyNode.setSkipList(emptySkip);
                seaflowNodeList.add(emptyNode);

                //  分支网关  连到空网关上
                Skip flowSkip =  createSkip(jsonArray.get(0), emptyNodeCode);
                serialSkip.add(flowSkip);

            }

        }
        node.setSkipList(serialSkip);
        return seaflowNodeList;
    }

    private Skip createSkip(Map<String, Object> subNode, String nextNodeId) {
        //default ：默认跳转配置， 只有条件分支才有
        Skip skip = FlowEngine.newSkip();
        Boolean defaultSerial =  true; //subNode.getBool("default");
        if(defaultSerial != null && defaultSerial){
            // 设置表单时类别 简单模式 或 复杂模式
            String expressionType = "@@default@@|true";
            skip.setSkipCondition( expressionType );
            skip.setSkipName((String)subNode.get("nodeName"));
            skip.setSkipType(SkipType.PASS.getKey());
            skip.setNextNodeCode(nextNodeId);

        }else{
            //设置跳转
            Map<String,Object> config = (Map<String,Object>)subNode.get("config");
            Map<String,Object> conditions = (Map<String,Object>)config.get("conditions");
            // 只有条件分支才有conditions
            if(conditions != null){
                Boolean simple =  true ;//conditions.getBool("simple");
                String group = (String)conditions.get("group");

                String simpleData = (String)conditions.get("simpleData");
                // 设置表单时类别 简单模式 或 复杂模式
                String expressionType = "@@" +  (simple? "simple_"+ group : "complex") + "@@|";
                skip.setSkipCondition( expressionType +  simpleData );
                skip.setSkipName((String)subNode.get("nodeName"));
                skip.setSkipType(SkipType.PASS.getKey());
                skip.setNextNodeCode(nextNodeId);

            }
        }
        return skip;

    }

    @Override
    public String getType() {
        return "serial";
    }
}
