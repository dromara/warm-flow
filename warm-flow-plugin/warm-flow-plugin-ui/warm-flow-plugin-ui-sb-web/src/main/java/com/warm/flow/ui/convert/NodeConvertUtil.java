package com.warm.flow.ui.convert;

import com.warm.flow.core.FlowFactory;
import com.warm.flow.core.entity.Node;
import com.warm.flow.core.entity.Skip;
import com.warm.flow.core.enums.NodeType;
import com.warm.flow.core.enums.SkipType;
import com.warm.flow.core.utils.StringUtils;

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
                    Node emptyNode = FlowFactory.newNode();
                    emptyNode.setNodeCode(emptyNodeCode);
                    emptyNode.setNodeType(NodeType.BETWEEN.getKey());
                    // 设置空类别自动通过， 区分前端审批人为空的配置
                    emptyNode.setPermissionFlag("auto");


                    List<Skip> emptySkip = new ArrayList<>();
                    Skip emSkip = FlowFactory.newSkip();
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
