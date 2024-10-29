package com.warm.flow.ui.convert;

import com.warm.flow.core.FlowFactory;
import com.warm.flow.core.constant.FlowCons;
import com.warm.flow.core.entity.Node;
import com.warm.flow.core.entity.Skip;
import com.warm.flow.core.enums.SkipType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class StartNodeConvert extends NodeConvertAbstract{
    @Override
    public List<Node> convert(Map<String,Object> jsonObject, String startNodeId, String endNodeId, String nextNodeId) {
        // 节点
        Node seaflowNode = FlowFactory.newNode();
        // 设置发起人为自己
        seaflowNode.setPermissionFlag(FlowCons.WARMFLOWINITIATOR);

//        seaflowNode.setListenerType(String.join(",", Listener.LISTENER_ASSIGNMENT, Listener.LISTENER_CREATE));
//        seaflowNode.setListenerPath(String.join("@@", GlobalAssignmentListener.class.getName(),
//                GlobalCreateListener.class.getName()));
        // 跳转
        Skip flowSkip = FlowFactory.newSkip();
        flowSkip.setNowNodeCode(seaflowNode.getNodeCode());
        flowSkip.setSkipType(SkipType.PASS.getKey());
        flowSkip.setNextNodeCode(nextNodeId);
        List<Skip> skipList = new ArrayList<>();
        skipList.add(flowSkip);
        seaflowNode.setSkipList(skipList);

        return Arrays.asList(seaflowNode);
    }

    @Override
    public String getType() {
        return "start";
    }
}
