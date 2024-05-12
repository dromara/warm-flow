package com.warm.flow.orm.entity;

import com.warm.flow.core.entity.HisTask;
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
 * 历史任务记录对象 flow_his_task
 *
 * @author vanlin
 * @date 2024-05-08
 */
@Entity
@Table(name = "flow_his_task")
public class FlowHisTask extends JPARootEntity<FlowHisTask> implements HisTask {

    public final static HashMap<String, String> MAPPING = new HashMap<>();

    static {
        JPAUtil.initMapping(FlowHisTask.class, MAPPING);
        MAPPING.putAll(JPARootEntity.JPA_ROOT_ENTITY_MAPPING);
    }

    @Transient
    private JPAPredicateFunction<CriteriaBuilder, Root<FlowHisTask>, List<Predicate>> entityPredicate =
            (criteriaBuilder, root, predicates) -> {
                if (StringUtils.isNotEmpty(this.nodeCode)) {
                    predicates.add(criteriaBuilder.equal(root.get("nodeCode"), this.nodeCode));
                }
                if (StringUtils.isNotEmpty(this.nodeName)) {
                    predicates.add(criteriaBuilder.equal(root.get("nodeName"), this.nodeName));
                }
                if (StringUtils.isNotEmpty(this.targetNodeCode)) {
                    predicates.add(criteriaBuilder.equal(root.get("targetNodeCode"), this.targetNodeCode));
                }
                if (StringUtils.isNotEmpty(this.targetNodeName)) {
                    predicates.add(criteriaBuilder.equal(root.get("targetNodeName"), this.targetNodeName));
                }
                if (StringUtils.isNotEmpty(this.approver)) {
                    predicates.add(criteriaBuilder.equal(root.get("approver"), this.approver));
                }
                if (Objects.nonNull(this.definitionId)) {
                    predicates.add(criteriaBuilder.equal(root.get("definitionId"), this.definitionId));
                }
                if (Objects.nonNull(this.instanceId)) {
                    predicates.add(criteriaBuilder.equal(root.get("instanceId"), this.instanceId));
                }
                if  (Objects.nonNull(this.flowStatus)) {
                    predicates.add(criteriaBuilder.equal(root.get("flowStatus"), this.flowStatus));
                }
                if  (Objects.nonNull(this.permissionFlag)) {
                    predicates.add(criteriaBuilder.equal(root.get("permissionFlag"), this.permissionFlag));
                }
                if (Objects.nonNull(this.message)) {
                    predicates.add(criteriaBuilder.equal(root.get("message"), this.message));
                }
            };

    @Override
    public JPAPredicateFunction<CriteriaBuilder, Root<FlowHisTask>, List<Predicate>> entityPredicate() {
        return this.entityPredicate;
    }

    @Override
    public String orderByField(String orderByColumn) {
        return MAPPING.get(orderByColumn);
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
     * 流程实例表id
     */
    @Column(name="instance_id")
    private Long instanceId;

    /**
     * 业务id
     */
    @Transient
    private String businessId;

    /**
     * 开始节点编码
     */
    @Column(name="node_code")
    private String nodeCode;

    /**
     * 开始节点名称
     */
    @Column(name="node_name")
    private String nodeName;

    /**
     * 开始节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关）
     */
    @Column(name="node_type")
    private Integer nodeType;

    /**
     * 目标节点编码
     */
    @Column(name="target_node_code")
    private String targetNodeCode;

    /**
     * 结束节点名称
     */
    @Column(name="target_node_name")
    private String targetNodeName;

    /**
     * 审批者
     */
    @Column(name="approver")
    private String approver;

    /**
     * 权限标识（权限类型:权限标识，可以多个，如role:1,role:2)
     */
    @Column(name="permission_flag")
    private String permissionFlag;

    /**
     * 权限标识 permissionFlag的list形式
     */
    @Transient
    private List<String> permissionList;

    /**
     * 流程状态（0待提交 1审批中 2 审批通过 8已完成 9已退回 10失效）
     */
    @Column(name="flow_status")
    private Integer flowStatus;

    /**
     * 审批意见
     */
    @Column(name="message")
    private String message;

    /**
     * 创建者
     */
    @Transient
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


    @Override
    public Long getDefinitionId() {
        return definitionId;
    }

    @Override
    public FlowHisTask setDefinitionId(Long definitionId) {
        this.definitionId = definitionId;
        return this;
    }

    @Override
    public String getFlowName() {
        return flowName;
    }

    @Override
    public FlowHisTask setFlowName(String flowName) {
        this.flowName = flowName;
        return this;
    }

    @Override
    public Long getInstanceId() {
        return instanceId;
    }

    @Override
    public FlowHisTask setInstanceId(Long instanceId) {
        this.instanceId = instanceId;
        return this;
    }

    @Override
    public String getBusinessId() {
        return businessId;
    }

    @Override
    public FlowHisTask setBusinessId(String businessId) {
        this.businessId = businessId;
        return this;
    }

    @Override
    public String getNodeCode() {
        return nodeCode;
    }

    @Override
    public FlowHisTask setNodeCode(String nodeCode) {
        this.nodeCode = nodeCode;
        return this;
    }

    @Override
    public String getNodeName() {
        return nodeName;
    }

    @Override
    public FlowHisTask setNodeName(String nodeName) {
        this.nodeName = nodeName;
        return this;
    }

    @Override
    public Integer getNodeType() {
        return nodeType;
    }

    @Override
    public FlowHisTask setNodeType(Integer nodeType) {
        this.nodeType = nodeType;
        return this;
    }

    @Override
    public String getTargetNodeCode() {
        return targetNodeCode;
    }

    @Override
    public FlowHisTask setTargetNodeCode(String targetNodeCode) {
        this.targetNodeCode = targetNodeCode;
        return this;
    }

    @Override
    public String getTargetNodeName() {
        return targetNodeName;
    }

    @Override
    public FlowHisTask setTargetNodeName(String targetNodeName) {
        this.targetNodeName = targetNodeName;
        return this;
    }

    @Override
    public String getApprover() {
        return approver;
    }

    @Override
    public FlowHisTask setApprover(String approver) {
        this.approver = approver;
        return this;
    }

    @Override
    public String getPermissionFlag() {
        return permissionFlag;
    }

    @Override
    public FlowHisTask setPermissionFlag(String permissionFlag) {
        this.permissionFlag = permissionFlag;
        return this;
    }

    @Override
    public List<String> getPermissionList() {
        return permissionList;
    }

    @Override
    public FlowHisTask setPermissionList(List<String> permissionList) {
        this.permissionList = permissionList;
        return this;
    }

    @Override
    public Integer getFlowStatus() {
        return flowStatus;
    }

    @Override
    public FlowHisTask setFlowStatus(Integer flowStatus) {
        this.flowStatus = flowStatus;
        return this;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public FlowHisTask setMessage(String message) {
        this.message = message;
        return this;
    }

    @Override
    public String getCreateBy() {
        return createBy;
    }

    @Override
    public FlowHisTask setCreateBy(String createBy) {
        this.createBy = createBy;
        return this;
    }

    @Override
    public String getFromCustom() {
        return fromCustom;
    }

    @Override
    public FlowHisTask setFromCustom(String fromCustom) {
        this.fromCustom = fromCustom;
        return this;
    }

    @Override
    public String getFromPath() {
        return fromPath;
    }

    @Override
    public FlowHisTask setFromPath(String fromPath) {
        this.fromPath = fromPath;
        return this;
    }

    @Override
    public String toString() {
        return "FlowHisTask{" +
                "id=" + super.getId() +
                ", createTime=" + super.getCreateTime() +
                ", updateTime=" + super.getUpdateTime() +
                ", definitionId=" + definitionId +
                ", flowName='" + flowName + '\'' +
                ", instanceId=" + instanceId +
                ", tenantId='" + super.getTenantId() + '\'' +
                ", businessId='" + businessId + '\'' +
                ", nodeCode='" + nodeCode + '\'' +
                ", nodeName='" + nodeName + '\'' +
                ", nodeType=" + nodeType +
                ", targetNodeCode='" + targetNodeCode + '\'' +
                ", targetNodeName='" + targetNodeName + '\'' +
                ", approver='" + approver + '\'' +
                ", permissionFlag='" + permissionFlag + '\'' +
                ", permissionList=" + permissionList +
                ", flowStatus=" + flowStatus +
                ", message='" + message + '\'' +
                ", createBy='" + createBy + '\'' +
                ", fromCustom='" + fromCustom + '\'' +
                ", fromPath='" + fromPath + '\'' +
                "} " + super.toString();
    }
}
