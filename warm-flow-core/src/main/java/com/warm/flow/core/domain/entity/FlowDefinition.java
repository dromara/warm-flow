package com.warm.flow.core.domain.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * 流程定义对象 flow_definition
 *
 * @author warm
 * @date 2023-03-29
 */
public class FlowDefinition extends FlowEntity {

    private static final long serialVersionUID = 1L;

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

    private List<FlowNode> nodeList = new ArrayList<>();


    public String getFlowCode() {
        return flowCode;
    }

    public FlowDefinition setFlowCode(String flowCode) {
        this.flowCode = flowCode;
        return this;
    }

    public String getFlowName() {
        return flowName;
    }

    public FlowDefinition setFlowName(String flowName) {
        this.flowName = flowName;
        return this;
    }

    public String getVersion() {
        return version;
    }

    public FlowDefinition setVersion(String version) {
        this.version = version;
        return this;
    }

    public Integer getIsPublish() {
        return isPublish;
    }

    public FlowDefinition setIsPublish(Integer isPublish) {
        this.isPublish = isPublish;
        return this;
    }

    public String getFromCustom() {
        return fromCustom;
    }

    public FlowDefinition setFromCustom(String fromCustom) {
        this.fromCustom = fromCustom;
        return this;
    }

    public String getFromPath() {
        return fromPath;
    }

    public FlowDefinition setFromPath(String fromPath) {
        this.fromPath = fromPath;
        return this;
    }

    public List<FlowNode> getNodeList() {
        return nodeList;
    }

    public FlowDefinition setNodeList(List<FlowNode> nodeList) {
        this.nodeList = nodeList;
        return this;
    }
}
