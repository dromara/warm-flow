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
import org.dromara.warm.flow.core.constant.FlowCons;
import org.dromara.warm.flow.core.entity.Node;
import org.dromara.warm.flow.core.entity.Skip;
import org.dromara.warm.flow.core.enums.SkipType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class StartNodeConvert extends NodeConvertAbstract{
    @Override
    public List<Node> convert(Map<String,Object> jsonObject, String startNodeId, String endNodeId, String nextNodeId) {
        // 节点
        Node seaflowNode = FlowEngine.newNode();
        // 设置发起人为自己
        seaflowNode.setPermissionFlag(FlowCons.WARMFLOWINITIATOR);

//        seaflowNode.setListenerType(String.join(",", Listener.LISTENER_ASSIGNMENT, Listener.LISTENER_CREATE));
//        seaflowNode.setListenerPath(String.join("@@", DefAssignmentListener.class.getName(),
//                DefCreateListener.class.getName()));
        // 跳转
        Skip flowSkip = FlowEngine.newSkip();
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
