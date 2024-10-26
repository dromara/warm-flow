package com.warm.flow.ui.convert;

import com.warm.flow.core.entity.Node;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public  class EndNodeConvert extends NodeConvertAbstract {
    @Override
    public String getType() {
        return "end";
    }

    @Override
    public List<Node> convert(Map<String, Object> jsonObject, String startNodeId, String endNodeId, String nextNodeId){

        Node node = getNode(jsonObject);
       ;
        return Arrays.asList(node);
    }
}
