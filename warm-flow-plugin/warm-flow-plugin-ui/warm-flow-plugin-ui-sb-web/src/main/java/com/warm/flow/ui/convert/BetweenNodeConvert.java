package com.warm.flow.ui.convert;

import com.warm.flow.core.FlowFactory;
import com.warm.flow.core.entity.Node;
import com.warm.flow.core.entity.Skip;
import com.warm.flow.core.enums.SkipType;

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
