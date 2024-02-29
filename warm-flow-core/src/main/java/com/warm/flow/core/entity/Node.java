package com.warm.flow.core.entity;

import java.util.List;

/**
 * 流程节点对象 flow_node
 *
 * @author warm
 * @date 2023-03-29
 */
public interface Node extends RootEntity {

    public Integer getNodeType();

    public Node setNodeType(Integer nodeType);

    public Long getDefinitionId();

    public Node setDefinitionId(Long definitionId);

    public String getNodeCode();

    public Node setNodeCode(String nodeCode);

    public String getNodeName();

    public Node setNodeName(String nodeName);

    public String getPermissionFlag();

    public Node setPermissionFlag(String permissionFlag);

    public String getCoordinate();

    public Node setCoordinate(String coordinate);

    public String getSkipAnyNode();

    public Node setSkipAnyNode(String skipAnyNode);

    public String getVersion();

    public Node setVersion(String version);

    public List<Skip> getSkipList();

    public Node setSkipList(List<Skip> skipList);
}
