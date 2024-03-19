package com.warm.flow.orm.entity;

import com.warm.flow.core.entity.Definition;
import com.warm.flow.core.entity.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * 流程定义对象 flow_definition
 *
 * @author warm
 * @date 2023-03-29
 */
public class FlowDefinition extends FlowEntity implements Definition {

    /**
     * 流程编码
     */
    private String flowCode;

    /**
     * 流程名称
     */
    private String flowName;

    /**
     * 流程版本
     */
    private String version;

    /**
     * 是否发布（0未开启 1开启）
     */
    private Integer isPublish;

    /**
     * 审批表单是否自定义（Y是 2否）
     */
    private String fromCustom;

    /**
     * 审批表单是否自定义（Y是 2否）
     */
    private String fromPath;

    /**
     * 审批表单是否自定义（Y是 2否）
     */
    private String xmlString;

    private List<Node> nodeList = new ArrayList<>();

    @Override
    public String getFlowCode() {
        return flowCode;
    }

    @Override
    public Definition setFlowCode(String flowCode) {
        this.flowCode = flowCode;
        return this;
    }

    @Override
    public String getFlowName() {
        return flowName;
    }

    @Override
    public Definition setFlowName(String flowName) {
        this.flowName = flowName;
        return this;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public Definition setVersion(String version) {
        this.version = version;
        return this;
    }

    @Override
    public Integer getIsPublish() {
        return isPublish;
    }

    @Override
    public Definition setIsPublish(Integer isPublish) {
        this.isPublish = isPublish;
        return this;
    }

    @Override
    public String getFromCustom() {
        return fromCustom;
    }

    @Override
    public Definition setFromCustom(String fromCustom) {
        this.fromCustom = fromCustom;
        return this;
    }

    @Override
    public String getFromPath() {
        return fromPath;
    }

    @Override
    public Definition setFromPath(String fromPath) {
        this.fromPath = fromPath;
        return this;
    }

    @Override
    public List<Node> getNodeList() {
        return nodeList;
    }

    @Override
    public Definition setNodeList(List<Node> nodeList) {
        this.nodeList = nodeList;
        return this;
    }

    @Override
    public String getXmlString() {
        return xmlString;
    }

    @Override
    public Definition setXmlString(String xmsString) {
        this.xmlString = xmsString;
        return this;
    }

    @Override
    public String toString() {
        return "FlowDefinition{" +
                "flowCode='" + flowCode + '\'' +
                ", flowName='" + flowName + '\'' +
                ", version='" + version + '\'' +
                ", isPublish=" + isPublish +
                ", fromCustom='" + fromCustom + '\'' +
                ", fromPath='" + fromPath + '\'' +
                ", xmlString='" + xmlString + '\'' +
                ", nodeList=" + nodeList +
                "} " + super.toString();
    }
}
