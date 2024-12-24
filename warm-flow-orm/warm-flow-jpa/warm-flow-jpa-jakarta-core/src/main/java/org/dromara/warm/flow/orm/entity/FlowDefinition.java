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
import org.dromara.warm.flow.core.entity.Definition;
import org.dromara.warm.flow.core.entity.Node;
import org.dromara.warm.flow.core.entity.User;
import org.dromara.warm.flow.core.enums.PublishStatus;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * 流程定义对象 flow_definition
 *
 * @author vanlin
 * @since 2024-05-08
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
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
                if (StringUtils.isNotEmpty(this.category)) {
                    predicates.add(criteriaBuilder.equal(root.get("category"), this.category));
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
                if (Objects.nonNull(this.activityStatus)) {
                    predicates.add(criteriaBuilder.equal(root.get("activityStatus"),this.activityStatus));
                }
                if (StringUtils.isNotEmpty(this.listenerType)) {
                    predicates.add(criteriaBuilder.equal(root.get("listenerType"), this.listenerType));
                }
                if (StringUtils.isNotEmpty(this.listenerPath)) {
                    predicates.add(criteriaBuilder.equal(root.get("listenerPath"), this.listenerPath));
                }
                if (StringUtils.isNotEmpty(this.ext)) {
                    predicates.add(criteriaBuilder.equal(root.get("ext"), this.ext));
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
        if (StringUtils.isNotEmpty(updateEntity.category)) {
            this.category = updateEntity.category;
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
        if (Objects.nonNull(updateEntity.activityStatus)) {
            this.activityStatus = updateEntity.activityStatus;
        }
        if (StringUtils.isNotEmpty(updateEntity.listenerType)) {
            this.listenerType = updateEntity.listenerType;
        }
        if (StringUtils.isNotEmpty(updateEntity.listenerPath)) {
            this.listenerPath = updateEntity.listenerPath;
        }
        if (Objects.nonNull(updateEntity.getCreateTime())) {
            this.setCreateTime(updateEntity.getCreateTime());
        }
        if (Objects.nonNull(updateEntity.getUpdateTime())) {
            this.setUpdateTime(updateEntity.getUpdateTime());
        }
        if (StringUtils.isNotEmpty(updateEntity.ext)) {
            this.ext = updateEntity.ext;
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
     * 流程类别
     */
    @Column(name = "category")
    private String category;

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
    @Column(name = "form_custom")
    private String formCustom;

    /**
     * 审批表单是否自定义（Y是 2否）
     */
    @Column(name = "form_path")
    private String formPath;

    /**
     * 流程激活状态（0挂起 1激活）
     */
    @Column(name = "activity_status")
    private Integer activityStatus;

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
     * 扩展字段，预留给业务系统使用
     */
    @Column(name = "ext")
    private String ext;

    /**
     * 审批表单是否自定义（Y是 2否）
     */
    @Transient
    private String xmlString;

    @Transient
    private List<Node> nodeList = new ArrayList<>();

    @Transient
    private List<User> userList = new ArrayList<>();

}
