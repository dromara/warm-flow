package com.warm.flow.orm.entity;

import com.warm.flow.core.entity.Definition;
import com.warm.flow.core.entity.Node;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 流程定义对象 flow_definition
 *
 * @author warm
 * @date 2023-03-29
 */
public class FlowDefinition implements Definition, Serializable {

    private static final long serialVersionUID = -5481202456975752152L;
    /**
     * 主键
     */
    private Long id;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

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
    public Long getId() {
        return id;
    }

    @Override
    public FlowDefinition setId(Long id) {
        this.id = id;
        return this;
    }

    @Override
    public Date getCreateTime() {
        return createTime;
    }

    @Override
    public FlowDefinition setCreateTime(Date createTime) {
        this.createTime = createTime;
        return this;
    }

    @Override
    public Date getUpdateTime() {
        return updateTime;
    }

    @Override
    public FlowDefinition setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
        return this;
    }

    @Override
    public String getFlowCode() {
        return flowCode;
    }

    @Override
    public FlowDefinition setFlowCode(String flowCode) {
        this.flowCode = flowCode;
        return this;
    }

    @Override
    public String getFlowName() {
        return flowName;
    }

    @Override
    public FlowDefinition setFlowName(String flowName) {
        this.flowName = flowName;
        return this;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public FlowDefinition setVersion(String version) {
        this.version = version;
        return this;
    }

    @Override
    public Integer getIsPublish() {
        return isPublish;
    }

    @Override
    public FlowDefinition setIsPublish(Integer isPublish) {
        this.isPublish = isPublish;
        return this;
    }

    @Override
    public String getFromCustom() {
        return fromCustom;
    }

    @Override
    public FlowDefinition setFromCustom(String fromCustom) {
        this.fromCustom = fromCustom;
        return this;
    }

    @Override
    public String getFromPath() {
        return fromPath;
    }

    @Override
    public FlowDefinition setFromPath(String fromPath) {
        this.fromPath = fromPath;
        return this;
    }

    @Override
    public String getXmlString() {
        return xmlString;
    }

    @Override
    public FlowDefinition setXmlString(String xmlString) {
        this.xmlString = xmlString;
        return this;
    }

    @Override
    public List<Node> getNodeList() {
        return nodeList;
    }

    @Override
    public FlowDefinition setNodeList(List<Node> nodeList) {
        this.nodeList = nodeList;
        return this;
    }

    @Override
    public String toString() {
        return "FlowDefinition{" +
                "id=" + id +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", flowCode='" + flowCode + '\'' +
                ", flowName='" + flowName + '\'' +
                ", version='" + version + '\'' +
                ", isPublish=" + isPublish +
                ", fromCustom='" + fromCustom + '\'' +
                ", fromPath='" + fromPath + '\'' +
                ", xmlString='" + xmlString + '\'' +
                ", nodeList=" + nodeList +
                '}';
    }
}
