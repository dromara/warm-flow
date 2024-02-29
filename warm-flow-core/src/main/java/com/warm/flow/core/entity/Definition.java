package com.warm.flow.core.entity;

import java.util.List;

/**
 * 流程定义对象 flow_definition
 *
 * @author warm
 * @date 2023-03-29
 */
public interface Definition extends RootEntity {


    public String getFlowCode();

    public Definition setFlowCode(String flowCode);

    public String getFlowName();

    public Definition setFlowName(String flowName);

    public String getVersion();

    public Definition setVersion(String version);

    public Integer getIsPublish();

    public Definition setIsPublish(Integer isPublish);

    public String getFromCustom();

    public Definition setFromCustom(String fromCustom);

    public String getFromPath();

    public Definition setFromPath(String fromPath);

    public List<Node> getNodeList();

    public Definition setNodeList(List<Node> nodeList);

    public String getXmlString();

    public Definition setXmlString(String xmsString);
}