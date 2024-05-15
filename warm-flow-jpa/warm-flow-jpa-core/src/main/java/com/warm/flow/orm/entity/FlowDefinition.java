package com.warm.flow.orm.entity;

import com.warm.flow.core.entity.Definition;
import com.warm.flow.core.entity.Node;
import com.warm.flow.core.entity.User;
import com.warm.flow.core.enums.PublishStatus;
import com.warm.flow.orm.utils.JPAUtil;
import com.warm.flow.orm.utils.JPAPredicateFunction;
import com.warm.tools.utils.StringUtils;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
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
                if  (Objects.nonNull(this.fromCustom)) {
                    predicates.add(criteriaBuilder.equal(root.get("fromCustom"), this.fromCustom));
                }
                if (Objects.nonNull(this.fromPath)) {
                    predicates.add(criteriaBuilder.equal(root.get("fromPath"), this.fromPath));
                }
    };

    @Override
    public JPAPredicateFunction<CriteriaBuilder, Root<FlowDefinition>, List<Predicate>> entityPredicate() {
        return this.entityPredicate;
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
        if (Objects.isNull(this.fromCustom)) {
            this.fromCustom = "N";
        }
    }

    @Override
    public void mergeUpdate(FlowDefinition updateEntity) {
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
        if (Objects.nonNull(updateEntity.fromCustom)) {
            this.fromCustom = updateEntity.fromCustom;
        }
        if (Objects.nonNull(updateEntity.fromPath)) {
            this.fromPath = updateEntity.fromPath;
        }
        if (Objects.nonNull(updateEntity.getCreateTime())) {
            this.setCreateTime(updateEntity.getCreateTime());
        }
        if (Objects.nonNull(updateEntity.getUpdateTime())) {
            this.setUpdateTime(updateEntity.getUpdateTime());
        }
    }

    /**
     * 流程编码
     */
    @Column(name="flow_code")
    private String flowCode;

    /**
     * 流程名称
     */
    @Column(name="flow_name")
    private String flowName;

    /**
     * 流程版本
     */
    private String version;

    /**
     * 是否发布（0未开启 1开启）
     */
    @Column(name="is_publish")
    private Integer isPublish;

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
                "id=" + super.getId() +
                ", createTime=" + super.getCreateTime() +
                ", updateTime=" + super.getUpdateTime() +
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
