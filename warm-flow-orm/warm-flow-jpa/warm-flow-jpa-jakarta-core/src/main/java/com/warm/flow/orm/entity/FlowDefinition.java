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

import com.warm.flow.core.entity.Definition;
import com.warm.flow.core.entity.Node;
import com.warm.flow.core.entity.User;
import com.warm.flow.core.enums.PublishStatus;
import com.warm.flow.core.utils.StringUtils;
import com.warm.flow.orm.utils.JPAPredicateFunction;
import com.warm.flow.orm.utils.JPAUpdateMergeFunction;
import com.warm.flow.orm.utils.JPAUtil;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * 流程定义对象 flow_definition
 *
 * @author vanlin
 * @date 2024-05-08
 */
@Entity
@Table(name = "flow_definition")
public class FlowDefinition extends JPARootEntity<FlowDefinition> implements Definition {

    public final static HashMap<String, String> MAPPING = new HashMap<>();

    static {
        JPAUtil.initMapping(FlowDefinition.class, MAPPING);
        MAPPING.putAll(JPARootEntity.JPA_ROOT_ENTITY_MAPPING);
    }

    @Transient
    private JPAPredicateFunction<CriteriaBuilder, Root<FlowDefinition>, List<Predicate>> entityPredicate =
            (criteriaBuilder, root, predicates) -> {
                if (StringUtils.isNotEmpty(this.flowCode)) {
                    predicates.add(criteriaBuilder.equal(root.get("flowCode"), this.flowCode));
                }
                if (StringUtils.isNotEmpty(this.flowName)) {
                    predicates.add(criteriaBuilder.equal(root.get("flowName"), this.flowName));
                }
                if (StringUtils.isNotEmpty(this.version)) {
                    predicates.add(criteriaBuilder.equal(root.get("version"), this.version));
                }
                if (Objects.nonNull(this.isPublish)) {
                    predicates.add(criteriaBuilder.equal(root.get("isPublish"), this.isPublish));
                }
                if (Objects.nonNull(this.formCustom)) {
                    predicates.add(criteriaBuilder.equal(root.get("formCustom"), this.formCustom));
                }
                if (Objects.nonNull(this.formPath)) {
                    predicates.add(criteriaBuilder.equal(root.get("formPath"), this.formPath));
                }
            };

    @Transient
    private JPAUpdateMergeFunction<FlowDefinition> entityMerge = (updateEntity) -> {
        if (StringUtils.isNotEmpty(updateEntity.flowCode)) {
            this.flowCode = updateEntity.flowCode;
        }
        if (StringUtils.isNotEmpty(updateEntity.flowName)) {
            this.flowName = updateEntity.flowName;
        }
        if (StringUtils.isNotEmpty(updateEntity.version)) {
            this.version = updateEntity.version;
        }
        if (Objects.nonNull(updateEntity.isPublish)) {
            this.isPublish = updateEntity.isPublish;
        }
        if (Objects.nonNull(updateEntity.formCustom)) {
            this.formCustom = updateEntity.formCustom;
        }
        if (Objects.nonNull(updateEntity.formPath)) {
            this.formPath = updateEntity.formPath;
        }
        if (Objects.nonNull(updateEntity.getCreateTime())) {
            this.setCreateTime(updateEntity.getCreateTime());
        }
        if (Objects.nonNull(updateEntity.getUpdateTime())) {
            this.setUpdateTime(updateEntity.getUpdateTime());
        }
    };

    @Override
    public JPAPredicateFunction<CriteriaBuilder, Root<FlowDefinition>, List<Predicate>> entityPredicate() {
        return this.entityPredicate;
    }

    @Override
    public JPAUpdateMergeFunction<FlowDefinition> entityMerge() {
        return this.entityMerge;
    }

    @Override
    public String orderByField(String orderByColumn) {
        return MAPPING.get(orderByColumn);
    }

    @Override
    public void initDefaultValue() {
        if (Objects.isNull(this.isPublish)) {
            this.isPublish = PublishStatus.UNPUBLISHED.getKey();
        }
        if (Objects.isNull(this.formCustom)) {
            this.formCustom = "N";
        }
    }

    /**
     * 流程编码
     */
    @Column(name = "flow_code")
    private String flowCode;

    /**
     * 流程名称
     */
    @Column(name = "flow_name")
    private String flowName;

    /**
     * 流程版本
     */
    private String version;

    /**
     * 是否发布（0未开启 1开启）
     */
    @Column(name = "is_publish")
    private Integer isPublish;

    /**
     * 审批表单是否自定义（Y是 2否）
     */
    @Column(name = "from_custom")
    private String formCustom;

    /**
     * 审批表单是否自定义（Y是 2否）
     */
    @Column(name = "from_path")
    private String formPath;

    /**
     * 审批表单是否自定义（Y是 2否）
     */
    @Transient
    private String xmlString;

    @Transient
    private List<Node> nodeList = new ArrayList<>();

    @Transient
    private List<User> userList = new ArrayList<>();

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
    public String getFormCustom() {
        return formCustom;
    }

    @Override
    public FlowDefinition setFormCustom(String formCustom) {
        this.formCustom = formCustom;
        return this;
    }

    @Override
    public String getFormPath() {
        return formPath;
    }

    @Override
    public FlowDefinition setFormPath(String formPath) {
        this.formPath = formPath;
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
                "id=" + super.getId() +
                ", createTime=" + super.getCreateTime() +
                ", updateTime=" + super.getUpdateTime() +
                ", flowCode='" + flowCode + '\'' +
                ", flowName='" + flowName + '\'' +
                ", version='" + version + '\'' +
                ", isPublish=" + isPublish +
                ", formCustom='" + formCustom + '\'' +
                ", formPath='" + formPath + '\'' +
                ", xmlString='" + xmlString + '\'' +
                ", nodeList=" + nodeList +
                '}';
    }
}
