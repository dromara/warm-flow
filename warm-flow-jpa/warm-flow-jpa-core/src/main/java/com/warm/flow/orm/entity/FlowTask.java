package com.warm.flow.orm.entity;

import com.warm.flow.core.entity.Task;
import com.warm.flow.core.entity.User;
import com.warm.flow.orm.utils.JPAUpdateMergeFunction;
import com.warm.flow.orm.utils.JPAUtil;
import com.warm.flow.orm.utils.JPAPredicateFunction;
import com.warm.flow.core.utils.StringUtils;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * 待办任务记录对象 flow_task
 *
 * @author vanlin
 * @date 2024-05-08
 */
@Entity
@Table(name = "flow_task")
public class FlowTask extends JPARootEntity<FlowTask> implements Task {
    public final static HashMap<String, String> MAPPING = new HashMap<>();

    static {
        JPAUtil.initMapping(FlowTask.class, MAPPING);
        MAPPING.putAll(JPARootEntity.JPA_ROOT_ENTITY_MAPPING);
    }

    @Transient
    private JPAPredicateFunction<CriteriaBuilder, Root<FlowTask>, List<Predicate>> entityPredicate =
            (criteriaBuilder, root, predicates) -> {
                if (StringUtils.isNotEmpty(this.nodeCode)) {
                    predicates.add(criteriaBuilder.equal(root.get("nodeCode"), this.nodeCode));
                }
                if (StringUtils.isNotEmpty(this.nodeName)) {
                    predicates.add(criteriaBuilder.equal(root.get("nodeName"), this.nodeName));
                }
                if  (Objects.nonNull(this.nodeType)) {
                    predicates.add(criteriaBuilder.equal(root.get("nodeType"), this.nodeType));
                }
                if  (Objects.nonNull(this.definitionId)) {
                    predicates.add(criteriaBuilder.equal(root.get("definitionId"), this.definitionId));
                }
                if  (Objects.nonNull(this.instanceId)) {
                    predicates.add(criteriaBuilder.equal(root.get("instanceId"), this.instanceId));
                }
            };

    @Transient
    private JPAUpdateMergeFunction<FlowTask> entityMerge = (updateEntity) -> {
        if (StringUtils.isNotEmpty(updateEntity.nodeCode)) {
            this.nodeCode = updateEntity.nodeCode;
        }
        if (StringUtils.isNotEmpty(updateEntity.nodeName)) {
            this.nodeName = updateEntity.nodeName;
        }
        if (Objects.nonNull(updateEntity.nodeType)) {
            this.nodeType = updateEntity.nodeType;
        }
        if (Objects.nonNull(updateEntity.definitionId)) {
            this.definitionId = updateEntity.definitionId;
        }
        if (Objects.nonNull(updateEntity.instanceId)) {
            this.instanceId = updateEntity.instanceId;
        }
        if (Objects.nonNull(updateEntity.getCreateTime())) {
            this.setCreateTime(updateEntity.getCreateTime());
        }
        if (Objects.nonNull(updateEntity.getUpdateTime())) {
            this.setUpdateTime(updateEntity.getUpdateTime());
        }
    };

    @Override
    public JPAPredicateFunction<CriteriaBuilder, Root<FlowTask>, List<Predicate>> entityPredicate() {
        return this.entityPredicate;
    }

    @Override
    public JPAUpdateMergeFunction<FlowTask> entityMerge() {
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
    @Column(name="definition_id")
    private Long definitionId;

    /**
     * 流程实例表id
     */
    @Column(name="instance_id")
    private Long instanceId;

    /**
     * 流程名称
     */
    @Transient
    private String flowName;

    /**
     * 业务id
     */
    @Transient
    private String businessId;

    /**
     * 节点编码
     */
    @Column(name="node_code")
    private String nodeCode;

    /**
     * 节点名称
     */
    @Column(name="node_name")
    private String nodeName;

    /**
     * 节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关）
     */
    @Column(name="node_type")
    private Integer nodeType;

    /**
     * 权限标识 permissionFlag的list形式
     */
    @Transient
    private List<String> permissionList;

    /**
     * 审批表单是否自定义（Y是 2否）
     */
    @Transient
    private String fromCustom;

    /**
     * 流程用户列表
     */
    @Transient
    private List<User> userList;

    /**
     * 审批表单是否自定义（Y是 2否）
     */
    @Transient
    private String fromPath;


    @Override
    public Long getDefinitionId() {
        return definitionId;
    }

    @Override
    public FlowTask setDefinitionId(Long definitionId) {
        this.definitionId = definitionId;
        return this;
    }

    @Override
    public Long getInstanceId() {
        return instanceId;
    }

    @Override
    public FlowTask setInstanceId(Long instanceId) {
        this.instanceId = instanceId;
        return this;
    }

    @Override
    public String getFlowName() {
        return flowName;
    }

    @Override
    public FlowTask setFlowName(String flowName) {
        this.flowName = flowName;
        return this;
    }

    @Override
    public String getBusinessId() {
        return businessId;
    }

    @Override
    public FlowTask setBusinessId(String businessId) {
        this.businessId = businessId;
        return this;
    }

    @Override
    public String getNodeCode() {
        return nodeCode;
    }

    @Override
    public FlowTask setNodeCode(String nodeCode) {
        this.nodeCode = nodeCode;
        return this;
    }

    @Override
    public String getNodeName() {
        return nodeName;
    }

    @Override
    public FlowTask setNodeName(String nodeName) {
        this.nodeName = nodeName;
        return this;
    }

    @Override
    public Integer getNodeType() {
        return nodeType;
    }

    @Override
    public FlowTask setNodeType(Integer nodeType) {
        this.nodeType = nodeType;
        return this;
    }

    @Override
    public List<String> getPermissionList() {
        return permissionList;
    }

    @Override
    public FlowTask setPermissionList(List<String> permissionList) {
        this.permissionList = permissionList;
        return this;
    }

    @Override
    public List<User> getUserList() {
        return userList;
    }

    @Override
    public FlowTask setUserList(List<User> userList) {
        this.userList = userList;
        return this;
    }

    @Override
    public String getFromCustom() {
        return fromCustom;
    }

    @Override
    public FlowTask setFromCustom(String fromCustom) {
        this.fromCustom = fromCustom;
        return this;
    }

    @Override
    public String getFromPath() {
        return fromPath;
    }

    @Override
    public FlowTask setFromPath(String fromPath) {
        this.fromPath = fromPath;
        return this;
    }

    @Override
    public String toString() {
        return "FlowTask{" +
                "id=" + super.getId() +
                ", createTime=" + super.getCreateTime() +
                ", updateTime=" + super.getUpdateTime() +
                ", tenantId='" + super.getTenantId() + '\'' +
                ", delFlag='" + super.getDelFlag() + '\'' +
                ", definitionId=" + definitionId +
                ", instanceId=" + instanceId +
                ", flowName='" + flowName + '\'' +
                ", businessId='" + businessId + '\'' +
                ", nodeCode='" + nodeCode + '\'' +
                ", nodeName='" + nodeName + '\'' +
                ", nodeType=" + nodeType +
                ", permissionList=" + permissionList +
                ", userList=" + userList +
                ", fromCustom='" + fromCustom + '\'' +
                ", fromPath='" + fromPath + '\'' +
                "} ";
    }
}
