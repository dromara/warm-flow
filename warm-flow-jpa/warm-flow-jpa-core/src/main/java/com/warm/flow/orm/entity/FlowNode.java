package com.warm.flow.orm.entity;

import com.warm.flow.core.entity.Node;
import com.warm.flow.core.entity.Skip;
import com.warm.flow.core.orm.agent.WarmQuery;
import com.warm.flow.orm.utils.JPAUtil;
import com.warm.tools.utils.StringUtils;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * 流程节点对象 flow_node
 *
 * @author vanlin
 * @date 2024-05-08
 */
@Entity
@Table(name = "flow_node")
public class FlowNode extends JPARootEntity<FlowNode> implements Node {
    public final static HashMap<String, String> MAPPING = new HashMap<>();

    static {
        JPAUtil.initMapping(FlowNode.class, MAPPING);
        MAPPING.putAll(JPARootEntity.JPA_ROOT_ENTITY_MAPPING);
    }

    @Override
    protected void customPredicates(CriteriaBuilder criteriaBuilder,
                                    Root<FlowNode> root,
                                    List<Predicate> predicates) {
        if  (Objects.nonNull(this.nodeType)) {
            predicates.add(criteriaBuilder.equal(root.get("nodeType"), this.nodeType));
        }
        if  (Objects.nonNull(this.definitionId)) {
            predicates.add(criteriaBuilder.equal(root.get("definitionId"), this.definitionId));
        }
        if (StringUtils.isNotEmpty(this.nodeCode)) {
            predicates.add(criteriaBuilder.equal(root.get("nodeCode"), this.nodeCode));
        }
        if (StringUtils.isNotEmpty(this.nodeName)) {
            predicates.add(criteriaBuilder.equal(root.get("nodeName"), this.nodeName));
        }
        if (StringUtils.isNotEmpty(this.permissionFlag)) {
            predicates.add(criteriaBuilder.equal(root.get("permissionFlag"), this.permissionFlag));
        }
        if (StringUtils.isNotEmpty(this.coordinate)) {
            predicates.add(criteriaBuilder.equal(root.get("coordinate"), this.coordinate));
        }
        if (StringUtils.isNotEmpty(this.skipAnyNode)) {
            predicates.add(criteriaBuilder.equal(root.get("skipAnyNode"), this.skipAnyNode));
        }
        if (StringUtils.isNotEmpty(this.listenerType)) {
            predicates.add(criteriaBuilder.equal(root.get("listenerType"), this.listenerType));
        }
        if (StringUtils.isNotEmpty(this.listenerPath)) {
            predicates.add(criteriaBuilder.equal(root.get("listenerPath"), this.listenerPath));
        }
        if (StringUtils.isNotEmpty(this.version)) {
            predicates.add(criteriaBuilder.equal(root.get("version"), this.version));
        }
    }

    @Override
    protected String customOrderByField(String orderByColumn) {
        return MAPPING.get(orderByColumn);
    }

    /**
     * 跳转条件
     */
    @Transient
    List<Skip> skipList = new ArrayList<>();

    /**
     * 节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关）
     */
    @Column(name="node_type")
    private Integer nodeType;
    /**
     * 流程id
     */
    @Column(name="definition_id")
    private Long definitionId;
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
     * 权限标识（权限类型:权限标识，可以多个，如role:1,role:2)
     */
    @Column(name="permission_flag")
    private String permissionFlag;
    /**
     * 动态权限标识（权限类型:权限标识，可以多个，如role:1,role:2)
     */
    @Transient
    private String dynamicPermissionFlag;
    /**
     * 流程节点坐标
     */
    private String coordinate;
    /**
     * 版本
     */
    private String version;
    /**
     * 是否可以跳转任意节点（Y是 N否）
     */
    @Column(name="skip_any_node")
    private String skipAnyNode;
    /**
     * 监听器类型
     */
    @Column(name="listener_type")
    private String listenerType;
    /**
     * 监听器路径
     */
    @Column(name="listener_path")
    private String listenerPath;

    @Override
    public Integer getNodeType() {
        return nodeType;
    }

    @Override
    public FlowNode setNodeType(Integer nodeType) {
        this.nodeType = nodeType;
        return this;
    }

    @Override
    public Long getDefinitionId() {
        return definitionId;
    }

    @Override
    public FlowNode setDefinitionId(Long definitionId) {
        this.definitionId = definitionId;
        return this;
    }

    @Override
    public String getNodeCode() {
        return nodeCode;
    }

    @Override
    public FlowNode setNodeCode(String nodeCode) {
        this.nodeCode = nodeCode;
        return this;
    }

    @Override
    public String getNodeName() {
        return nodeName;
    }

    @Override
    public FlowNode setNodeName(String nodeName) {
        this.nodeName = nodeName;
        return this;
    }

    @Override
    public String getPermissionFlag() {
        return permissionFlag;
    }

    @Override
    public FlowNode setPermissionFlag(String permissionFlag) {
        this.permissionFlag = permissionFlag;
        return this;
    }

    @Override
    public String getDynamicPermissionFlag() {
        return dynamicPermissionFlag;
    }

    @Override
    public FlowNode setDynamicPermissionFlag(String dynamicPermissionFlag) {
        this.dynamicPermissionFlag = dynamicPermissionFlag;
        return this;
    }

    @Override
    public String getCoordinate() {
        return coordinate;
    }

    @Override
    public FlowNode setCoordinate(String coordinate) {
        this.coordinate = coordinate;
        return this;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public FlowNode setVersion(String version) {
        this.version = version;
        return this;
    }

    @Override
    public String getSkipAnyNode() {
        return skipAnyNode;
    }

    @Override
    public FlowNode setSkipAnyNode(String skipAnyNode) {
        this.skipAnyNode = skipAnyNode;
        return this;
    }

    @Override
    public String getListenerType() {
        return listenerType;
    }

    @Override
    public FlowNode setListenerType(String listenerType) {
        this.listenerType = listenerType;
        return this;
    }

    @Override
    public String getListenerPath() {
        return listenerPath;
    }

    @Override
    public FlowNode setListenerPath(String listenerPath) {
        this.listenerPath = listenerPath;
        return this;
    }

    @Override
    public List<Skip> getSkipList() {
        return skipList;
    }

    @Override
    public FlowNode setSkipList(List<Skip> skipList) {
        this.skipList = skipList;
        return this;
    }

    @Override
    public String toString() {
        return "FlowNode{" +
                "id=" + super.getId() +
                ", createTime=" + super.getCreateTime() +
                ", updateTime=" + super.getUpdateTime() +
                ", nodeType=" + nodeType +
                ", definitionId=" + definitionId +
                ", nodeCode='" + nodeCode + '\'' +
                ", nodeName='" + nodeName + '\'' +
                ", permissionFlag='" + permissionFlag + '\'' +
                ", coordinate='" + coordinate + '\'' +
                ", version='" + version + '\'' +
                ", skipAnyNode='" + skipAnyNode + '\'' +
                ", listenerType='" + listenerType + '\'' +
                ", listenerPath='" + listenerPath + '\'' +
                ", skipList=" + skipList +
                "} " + super.toString();
    }
}
