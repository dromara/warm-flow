package com.warm.flow.core.domain.entity;

import com.warm.mybatis.core.entity.FlowEntity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 流程定义对象 flow_definition
 *
 * @author hh
 * @date 2023-03-29
 */
public class FlowDefinition implements FlowEntity {
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 流程编码 */
    private String flowCode;

    /** 流程名称 */
    private String flowName;

    /** 流程版本 */
    private String version;

    /** 是否发布（0未开启 1开启） */
    private Integer isPublish;

    /** 审批表单是否自定义（Y是 2否） */
    private String fromCustom;

    /** 审批表单是否自定义（Y是 2否） */
    private String fromPath;

    /** 创建时间 */
    private Date createTime;

    /** 更新时间 */
    private Date updateTime;

    private List<FlowNode> nodeList = new ArrayList<>();


    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getFlowCode() {
        return flowCode;
    }

    public void setFlowCode(String flowCode) {
        this.flowCode = flowCode;
    }

    public String getFlowName() {
        return flowName;
    }

    public void setFlowName(String flowName) {
        this.flowName = flowName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Integer getIsPublish() {
        return isPublish;
    }

    public void setIsPublish(Integer isPublish) {
        this.isPublish = isPublish;
    }

    public String getFromCustom() {
        return fromCustom;
    }

    public void setFromCustom(String fromCustom) {
        this.fromCustom = fromCustom;
    }

    public String getFromPath() {
        return fromPath;
    }

    public void setFromPath(String fromPath) {
        this.fromPath = fromPath;
    }

    @Override
    public Date getCreateTime() {
        return createTime;
    }

    @Override
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public Date getUpdateTime() {
        return updateTime;
    }

    @Override
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public List<FlowNode> getNodeList() {
        return nodeList;
    }

    public void setNodeList(List<FlowNode> nodeList) {
        this.nodeList = nodeList;
    }
}
