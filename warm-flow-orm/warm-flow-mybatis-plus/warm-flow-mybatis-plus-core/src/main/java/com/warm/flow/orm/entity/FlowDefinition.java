/*
 *    Copyright 2024-2025, Warm-Flow (290631660@qq.com).
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.warm.flow.orm.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.warm.flow.core.entity.Definition;
import com.warm.flow.core.entity.Node;
import com.warm.flow.core.entity.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 流程定义对象 flow_definition
 *
 * @author warm
 * @date 2023-03-29
 */
@TableName("flow_definition")
public class FlowDefinition implements Definition {

    /**
     * 主键
     */
    @TableId
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
     * 租户ID
     */
    private String tenantId;

    /**
     * 删除标记
     */
    private String delFlag;

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
    @TableField(exist = false)
    private String xmlString;

    @TableField(exist = false)
    private List<Node> nodeList = new ArrayList<>();

    @TableField(exist = false)
    private List<User> userList = new ArrayList<>();

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
    public String getTenantId() {
        return tenantId;
    }

    @Override
    public FlowDefinition setTenantId(String tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    @Override
    public String getDelFlag() {
        return delFlag;
    }

    @Override
    public FlowDefinition setDelFlag(String delFlag) {
        this.delFlag = delFlag;
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
    public List<User> getUserList() {
        return userList;
    }

    @Override
    public Definition setUserList(List<User> userList) {
        this.userList = userList;
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
