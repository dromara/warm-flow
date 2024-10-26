package com.warm.flow.ui.convert;

import com.warm.flow.core.entity.Node;

import java.util.List;
import java.util.Map;

public interface NodeConvert {

    /**
     *  允许返回多个node
     * @param startNodeId
     * @param endNodeId
     * @param nextNodeId
     * @return
     */
    public List<Node> convert(Map<String, Object> jsonObject, String startNodeId, String endNodeId, String nextNodeId);


    public String getType();

}
