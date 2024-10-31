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

import org.dromara.warm.flow.core.FlowFactory;
import org.dromara.warm.flow.core.entity.Node;
import org.dromara.warm.flow.core.entity.Skip;
import org.dromara.warm.flow.core.enums.SkipType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public  class  BetweenNodeConvert extends NodeConvertAbstract{
    @Override
    public List<Node> convert(Map<String, Object> jsonObject, String startNodeId, String endNodeId, String nextNodeId) {
        Node node = getNode(jsonObject);
        // 拒绝直接结束
        List<Skip> skipList = new ArrayList<>();
        Skip skipBack = FlowFactory.newSkip();
        skipBack.setSkipType(SkipType.REJECT.getKey());
        skipBack.setNextNodeCode(endNodeId);
        skipList.add(skipBack);
        node.setSkipList(skipList);
        return Arrays.asList(node);
    }

    @Override
    public String getType() {
        return "between";
    }
}
