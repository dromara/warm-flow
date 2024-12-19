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
package org.dromara.warm.flow.orm.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.dromara.warm.flow.core.entity.Node;
import org.dromara.warm.flow.core.entity.Skip;
import org.dromara.warm.flow.core.utils.StringUtils;
import org.dromara.warm.flow.orm.utils.JPAPredicateFunction;
import org.dromara.warm.flow.orm.utils.JPAUpdateMergeFunction;
import org.dromara.warm.flow.orm.utils.JPAUtil;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * 流程节点对象 flow_node
 *
 * @author vanlin
 * @since 2024-05-08
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "flow_node")
public class FlowNode extends JPARootEntity<FlowNode> implements Node {
    public final static HashMap<String, String> MAPPING = new HashMap<>();

    static {
        JPAUtil.initMapping(FlowNode.class, MAPPING);
        MAPPING.putAll(JPARootEntity.JPA_ROOT_ENTITY_MAPPING);
    }

    @Transient
    private JPAPredicateFunction<CriteriaBuilder, Root<FlowNode>, List<Predicate>> entityPredicate =
            (criteriaBuilder, root, predicates) -> {
                if (Objects.nonNull(this.nodeType)) {
                    predicates.add(criteriaBuilder.equal(root.get("nodeType"), this.nodeType));
                }
                if (Objects.nonNull(this.definitionId)) {
                    predicates.add(criteriaBuilder.equal(root.get("definitionId"), this.definitionId));
                }
                if (StringUtils.isNotEmpty(this.nodeCode)) {
                    predicates.add(criteriaBuilder.equal(root.get("nodeCode"), this.nodeCode));
                }
                if (StringUtils.isNotEmpty(this.nodeName)) {
                    predicates.add(criteriaBuilder.equal(root.get("nodeName"), this.nodeName));
                }
                if (Objects.nonNull(this.nodeRatio)) {
                    predicates.add(criteriaBuilder.equal(root.get("nodeRatio"), this.nodeRatio));
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
                if (StringUtils.isNotEmpty(this.handlerType)) {
                    predicates.add(criteriaBuilder.equal(root.get("handlerType"), this.handlerType));
                }
                if (StringUtils.isNotEmpty(this.handlerPath)) {
                    predicates.add(criteriaBuilder.equal(root.get("handlerPath"), this.handlerPath));
                }
                if (StringUtils.isNotEmpty(this.formCustom)) {
                    predicates.add(criteriaBuilder.equal(root.get("formCustom"), this.formCustom));
                }
                if (StringUtils.isNotEmpty(this.formPath)) {
                    predicates.add(criteriaBuilder.equal(root.get("formPath"), this.formPath));
                }
                if (StringUtils.isNotEmpty(this.version)) {
                    predicates.add(criteriaBuilder.equal(root.get("version"), this.version));
                }
            };

    @Transient
    private JPAUpdateMergeFunction<FlowNode> entityMerge = (updateEntity) -> {
        if (Objects.nonNull(updateEntity.nodeType)) {
            this.nodeType = updateEntity.nodeType;
        }
        if (Objects.nonNull(updateEntity.definitionId)) {
            this.definitionId = updateEntity.definitionId;
        }
        if (StringUtils.isNotEmpty(updateEntity.nodeCode)) {
            this.nodeCode = updateEntity.nodeCode;
        }
        if (StringUtils.isNotEmpty(updateEntity.nodeName)) {
            this.nodeName = updateEntity.nodeName;
        }
        if (Objects.nonNull(updateEntity.nodeRatio)) {
            this.nodeRatio = updateEntity.nodeRatio;
        }
        if (StringUtils.isNotEmpty(updateEntity.coordinate)) {
            this.coordinate = updateEntity.coordinate;
        }
        if (StringUtils.isNotEmpty(updateEntity.skipAnyNode)) {
            this.skipAnyNode = updateEntity.skipAnyNode;
        }
        if (StringUtils.isNotEmpty(updateEntity.listenerType)) {
            this.listenerType = updateEntity.listenerType;
        }
        if (StringUtils.isNotEmpty(updateEntity.listenerPath)) {
            this.listenerPath = updateEntity.listenerPath;
        }
        if (StringUtils.isNotEmpty(updateEntity.handlerType)) {
            this.handlerType = updateEntity.handlerType;
        }
        if (StringUtils.isNotEmpty(updateEntity.handlerPath)) {
            this.handlerPath = updateEntity.handlerPath;
        }
        if (StringUtils.isNotEmpty(updateEntity.formCustom)) {
            this.formCustom = updateEntity.formCustom;
        }
        if (StringUtils.isNotEmpty(updateEntity.formPath)) {
            this.formPath = updateEntity.formPath;
        }
        if (StringUtils.isNotEmpty(updateEntity.version)) {
            this.version = updateEntity.version;
        }
        if (Objects.nonNull(updateEntity.getCreateTime())) {
            this.setCreateTime(updateEntity.getCreateTime());
        }
        if (Objects.nonNull(updateEntity.getUpdateTime())) {
            this.setUpdateTime(updateEntity.getUpdateTime());
        }
    };

    @Override
    public JPAPredicateFunction<CriteriaBuilder, Root<FlowNode>, List<Predicate>> entityPredicate() {
        return this.entityPredicate;
    }

    @Override
    public JPAUpdateMergeFunction<FlowNode> entityMerge() {
        return this.entityMerge;
    }

    @Override
    public String orderByField(String orderByColumn) {
        return MAPPING.get(orderByColumn);
    }

    @Override
    public void initDefaultValue() {
        if (Objects.isNull(this.skipAnyNode)) {
            this.skipAnyNode = "N";
        }
    }

    /**
     * 跳转条件
     */
    @Transient
    List<Skip> skipList = new ArrayList<>();

    /**
     * 节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关）
     */
    @Column(name = "node_type")
    private Integer nodeType;
    /**
     * 流程id
     */
    @Column(name = "definition_id")
    private Long definitionId;
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
     * 权限标识（权限类型:权限标识，可以多个，用逗号隔开)
     */
    @Column(name = "permission_flag")
    private String permissionFlag;

    /**
     * 流程签署比例值
     */
    @Column(name = "node_ratio")
    private BigDecimal nodeRatio;

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
    @Column(name = "skip_any_node")
    private String skipAnyNode;
    /**
     * 监听器类型
     */
    @Column(name = "listener_type")
    private String listenerType;
    /**
     * 监听器路径
     */
    @Column(name = "listener_path")
    private String listenerPath;

    /**
     * 处理器类型
     */
    @Column(name = "handler_type")
    private String handlerType;
    /**
     * 处理器路径
     */
    @Column(name = "handler_path")
    private String handlerPath;

    /**
     * 审批表单是否自定义（Y是 2否）
     */
    @Column(name = "form_custom")
    private String formCustom;

    /**
     * 审批表单是否自定义（Y是 2否）
     */
    @Column(name = "form_path")
    private String formPath;

}
