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

import com.warm.flow.core.entity.Instance;
import com.warm.flow.core.utils.StringUtils;
import com.warm.flow.orm.utils.JPAPredicateFunction;
import com.warm.flow.orm.utils.JPAUpdateMergeFunction;
import com.warm.flow.orm.utils.JPAUtil;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * 流程实例对象 flow_instance
 *
 * @author vanlin
 * @date 2024-05-08
 */
@Entity
@Table(name = "flow_instance")
public class FlowInstance extends JPARootEntity<FlowInstance> implements Instance {
    public final static HashMap<String, String> MAPPING = new HashMap<>();

    static {
        JPAUtil.initMapping(FlowInstance.class, MAPPING);
        MAPPING.putAll(JPARootEntity.JPA_ROOT_ENTITY_MAPPING);
    }

    @Transient
    private JPAPredicateFunction<CriteriaBuilder, Root<FlowInstance>, List<Predicate>> entityPredicate =
            (criteriaBuilder, root, predicates) -> {
                if (StringUtils.isNotEmpty(this.businessId)) {
                    predicates.add(criteriaBuilder.equal(root.get("businessId"), this.businessId));
                }
                if (StringUtils.isNotEmpty(this.nodeCode)) {
                    predicates.add(criteriaBuilder.equal(root.get("nodeCode"), this.nodeCode));
                }
                if (StringUtils.isNotEmpty(this.nodeName)) {
                    predicates.add(criteriaBuilder.equal(root.get("nodeName"), this.nodeName));
                }
                if (StringUtils.isNotEmpty(this.createBy)) {
                    predicates.add(criteriaBuilder.equal(root.get("createBy"), this.createBy));
                }
                if (Objects.nonNull(this.definitionId)) {
                    predicates.add(criteriaBuilder.equal(root.get("definitionId"), this.definitionId));
                }
                if (Objects.nonNull(this.variable)) {
                    predicates.add(criteriaBuilder.equal(root.get("variable"), this.variable));
                }
                if (Objects.nonNull(this.flowStatus)) {
                    predicates.add(criteriaBuilder.equal(root.get("flowStatus"), this.flowStatus));
                }
                if (Objects.nonNull(this.ext)) {
                    predicates.add(criteriaBuilder.equal(root.get("ext"), this.ext));
                }
            };

    @Transient
    private JPAUpdateMergeFunction<FlowInstance> entityMerge = (updateEntity) -> {
        if (StringUtils.isNotEmpty(updateEntity.businessId)) {
            this.businessId = updateEntity.businessId;
        }
        if (Objects.nonNull(updateEntity.definitionId)) {
            this.definitionId = updateEntity.definitionId;
        }
        if (Objects.nonNull(updateEntity.nodeType)) {
            this.nodeType = updateEntity.nodeType;
        }
        if (StringUtils.isNotEmpty(updateEntity.nodeCode)) {
            this.nodeCode = updateEntity.nodeCode;
        }
        if (StringUtils.isNotEmpty(updateEntity.nodeName)) {
            this.nodeName = updateEntity.nodeName;
        }
        if (Objects.nonNull(updateEntity.variable)) {
            this.variable = updateEntity.variable;
        }
        if (Objects.nonNull(updateEntity.flowStatus)) {
            this.flowStatus = updateEntity.flowStatus;
        }
        if (StringUtils.isNotEmpty(updateEntity.createBy)) {
            this.createBy = updateEntity.createBy;
        }
        if (Objects.nonNull(updateEntity.ext)) {
            this.ext = updateEntity.ext;
        }

        if (Objects.nonNull(updateEntity.getCreateTime())) {
            this.setCreateTime(updateEntity.getCreateTime());
        }
        if (Objects.nonNull(updateEntity.getUpdateTime())) {
            this.setUpdateTime(updateEntity.getUpdateTime());
        }
    };

    @Override
    public JPAPredicateFunction<CriteriaBuilder, Root<FlowInstance>, List<Predicate>> entityPredicate() {
        return this.entityPredicate;
    }

    @Override
    public JPAUpdateMergeFunction<FlowInstance> entityMerge() {
        return this.entityMerge;
    }

    @Override
    public String orderByField(String orderByColumn) {
        return MAPPING.get(orderByColumn);
    }

    @Override
    public void initDefaultValue() {
    }

    /**
     * 对应flow_definition表的id
     */
    @Column(name = "definition_id")
    private Long definitionId;

    /**
     * 流程名称
     */
    @Transient
    private String flowName;

    /**
     * 业务id
     */
    @Column(name = "business_id")
    private String businessId;

    /**
     * 节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关）
     */
    @Column(name = "node_type")
    private Integer nodeType;

    /**
     * 流程节点编码   每个流程的nodeCode是唯一的,即definitionId+nodeCode唯一,在数据库层面做了控制
     */
    @Column(name = "node_code")
    private String nodeCode;

    /**
     * 流程节点名称
     */
    @Column(name = "node_name")
    private String nodeName;

    /**
     * 流程变量
     */
    @Column(name = "variable")
    private String variable;

    /**
     * 流程状态（0待提交 1审批中 2 审批通过 3自动通过 8已完成 9已退回 10失效）
     */
    @Column(name = "flow_status")
    private Integer flowStatus;

    /**
     * 创建者
     */
    @Column(name = "create_by")
    private String createBy;

    /**
     * 审批表单是否自定义（Y是 2否）
     */
    @Transient
    private String formCustom;

    /**
     * 审批表单是否自定义（Y是 2否）
     */
    @Transient
    private String formPath;

    /**
     * 扩展字段，预留给业务系统使用
     */
    @Column(name = "ext")
    private String ext;


    @Override
    public Long getDefinitionId() {
        return definitionId;
    }

    @Override
    public FlowInstance setDefinitionId(Long definitionId) {
        this.definitionId = definitionId;
        return this;
    }

    @Override
    public String getFlowName() {
        return flowName;
    }

    @Override
    public FlowInstance setFlowName(String flowName) {
        this.flowName = flowName;
        return this;
    }

    @Override
    public String getBusinessId() {
        return businessId;
    }

    @Override
    public FlowInstance setBusinessId(String businessId) {
        this.businessId = businessId;
        return this;
    }

    @Override
    public Integer getNodeType() {
        return nodeType;
    }

    @Override
    public FlowInstance setNodeType(Integer nodeType) {
        this.nodeType = nodeType;
        return this;
    }

    @Override
    public String getNodeCode() {
        return nodeCode;
    }

    @Override
    public FlowInstance setNodeCode(String nodeCode) {
        this.nodeCode = nodeCode;
        return this;
    }

    @Override
    public String getNodeName() {
        return nodeName;
    }

    @Override
    public FlowInstance setNodeName(String nodeName) {
        this.nodeName = nodeName;
        return this;
    }

    @Override
    public String getVariable() {
        return variable;
    }

    @Override
    public FlowInstance setVariable(String variable) {
        this.variable = variable;
        return this;
    }

    @Override
    public Integer getFlowStatus() {
        return flowStatus;
    }

    @Override
    public FlowInstance setFlowStatus(Integer flowStatus) {
        this.flowStatus = flowStatus;
        return this;
    }

    @Override
    public String getCreateBy() {
        return createBy;
    }

    @Override
    public FlowInstance setCreateBy(String createBy) {
        this.createBy = createBy;
        return this;
    }

    @Override
    public String getFormCustom() {
        return formCustom;
    }

    @Override
    public FlowInstance setFormCustom(String formCustom) {
        this.formCustom = formCustom;
        return this;
    }

    @Override
    public String getFormPath() {
        return formPath;
    }

    @Override
    public FlowInstance setFormPath(String formPath) {
        this.formPath = formPath;
        return this;
    }

    @Override
    public String getExt() {
        return ext;
    }

    @Override
    public FlowInstance setExt(String ext) {
        this.ext = ext;
        return this;
    }

    @Override
    public String toString() {
        return "FlowInstance{" +
                "id=" + super.getId() +
                ", createTime=" + super.getCreateTime() +
                ", updateTime=" + super.getUpdateTime() +
                ", tenantId='" + super.getTenantId() + '\'' +
                ", delFlag='" + super.getDelFlag() + '\'' +
                ", definitionId=" + definitionId +
                ", flowName='" + flowName + '\'' +
                ", businessId='" + businessId + '\'' +
                ", nodeType=" + nodeType +
                ", nodeCode='" + nodeCode + '\'' +
                ", nodeName='" + nodeName + '\'' +
                ", variable='" + variable + '\'' +
                ", flowStatus=" + flowStatus +
                ", createBy='" + createBy + '\'' +
                ", formCustom='" + formCustom + '\'' +
                ", formPath='" + formPath + '\'' +
                ", ext='" + ext + '\'' +
                "}";
    }
}
