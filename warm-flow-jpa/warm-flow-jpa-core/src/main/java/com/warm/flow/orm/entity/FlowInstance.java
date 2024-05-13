package com.warm.flow.orm.entity;

import com.warm.flow.core.entity.Instance;
import com.warm.flow.orm.utils.JPAUtil;
import com.warm.flow.orm.utils.JPAPredicateFunction;
import com.warm.tools.utils.StringUtils;

import javax.persistence.*;
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

    @Override
    public JPAPredicateFunction<CriteriaBuilder, Root<FlowInstance>, List<Predicate>> entityPredicate() {
        return this.entityPredicate;
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
    @Column(name="definition_id")
    private Long definitionId;

    /**
     * 流程名称
     */
    @Transient
    private String flowName;

    /**
     * 业务id
     */
    @Column(name="business_id")
    private String businessId;

    /**
     * 节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关）
     */
    @Column(name="node_type")
    private Integer nodeType;

    /**
     * 流程节点编码   每个流程的nodeCode是唯一的,即definitionId+nodeCode唯一,在数据库层面做了控制
     */
    @Column(name="node_code")
    private String nodeCode;

    /**
     * 流程节点名称
     */
    @Column(name="node_name")
    private String nodeName;

    /**
     * 流程变量
     */
    @Column(name="variable")
    private String variable;

    /**
     * 流程状态（0待提交 1审批中 2 审批通过 8已完成 9已退回 10失效）
     */
    @Column(name="flow_status")
    private Integer flowStatus;

    /**
     * 创建者
     */
    @Column(name="create_by")
    private String createBy;

    /**
     * 审批表单是否自定义（Y是 2否）
     */
    @Transient
    private String fromCustom;

    /**
     * 审批表单是否自定义（Y是 2否）
     */
    @Transient
    private String fromPath;

    /**
     * 扩展字段
     */
    @Column(name="ext")
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
    public String getFromCustom() {
        return fromCustom;
    }

    @Override
    public FlowInstance setFromCustom(String fromCustom) {
        this.fromCustom = fromCustom;
        return this;
    }

    @Override
    public String getFromPath() {
        return fromPath;
    }

    @Override
    public FlowInstance setFromPath(String fromPath) {
        this.fromPath = fromPath;
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
                ", definitionId=" + definitionId +
                ", flowName='" + flowName + '\'' +
                ", businessId='" + businessId + '\'' +
                ", tenantId='" + super.getTenantId() + '\'' +
                ", nodeType=" + nodeType +
                ", nodeCode='" + nodeCode + '\'' +
                ", nodeName='" + nodeName + '\'' +
                ", variable='" + variable + '\'' +
                ", flowStatus=" + flowStatus +
                ", createBy='" + createBy + '\'' +
                ", fromCustom='" + fromCustom + '\'' +
                ", fromPath='" + fromPath + '\'' +
                ", ext='" + ext + '\'' +
                "} " + super.toString();
    }
}
