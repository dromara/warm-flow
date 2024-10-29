package com.warm.flow.ui.convert;


import com.warm.flow.core.FlowFactory;
import com.warm.flow.core.entity.Node;
import com.warm.flow.core.entity.Skip;
import com.warm.flow.core.enums.NodeType;
import com.warm.flow.core.listener.Listener;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract  class NodeConvertAbstract implements NodeConvert {


    public  Node getNode(Map<String, Object> jsonObject) {
        return FlowFactory.newNode();
    };


}
