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
import org.dromara.warm.flow.core.entity.HisTask;
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

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * 历史任务记录对象 flow_his_task
 *
 * @author vanlin
 * @since 2024-05-08
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
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
                if (Objects.nonNull(this.cooperateType)) {
                    predicates.add(criteriaBuilder.equal(root.get("cooperateType"), this.cooperateType));
                }
                if (StringUtils.isNotEmpty(this.approver)) {
                    predicates.add(criteriaBuilder.equal(root.get("approver"), this.approver));
                }
                if (StringUtils.isNotEmpty(this.collaborator)) {
                    predicates.add(criteriaBuilder.equal(root.get("collaborator"), this.collaborator));
                }
                if (Objects.nonNull(this.definitionId)) {
                    predicates.add(criteriaBuilder.equal(root.get("definitionId"), this.definitionId));
                }
                if (Objects.nonNull(this.instanceId)) {
                    predicates.add(criteriaBuilder.equal(root.get("instanceId"), this.instanceId));
                }
                if (Objects.nonNull(this.taskId)) {
                    predicates.add(criteriaBuilder.equal(root.get("taskId"), this.taskId));
                }
                if (StringUtils.isNotEmpty(this.skipType)) {
                    predicates.add(criteriaBuilder.equal(root.get("skipType"), this.skipType));
                }
                if (Objects.nonNull(this.flowStatus)) {
                    predicates.add(criteriaBuilder.equal(root.get("flowStatus"), this.flowStatus));
                }
                if (Objects.nonNull(this.message)) {
                    predicates.add(criteriaBuilder.equal(root.get("message"), this.message));
                }
                if (StringUtils.isNotEmpty(this.variable)) {
                    predicates.add(criteriaBuilder.equal(root.get("variable"), this.variable));
                }
                if (Objects.nonNull(this.ext)) {
                    predicates.add(criteriaBuilder.equal(root.get("ext"), this.ext));
                }
                if (StringUtils.isNotEmpty(this.formCustom)) {
                    predicates.add(criteriaBuilder.equal(root.get("formCustom"), this.formCustom));
                }
                if (StringUtils.isNotEmpty(this.formPath)) {
                    predicates.add(criteriaBuilder.equal(root.get("formPath"), this.formPath));
                }
            };

    @Transient
    private JPAUpdateMergeFunction<FlowHisTask> entityMerge = (updateEntity) -> {
        if (StringUtils.isNotEmpty(updateEntity.nodeCode)) {
            this.nodeCode = updateEntity.nodeCode;
        }
        if (StringUtils.isNotEmpty(updateEntity.nodeName)) {
            this.nodeName = updateEntity.nodeName;
        }
        if (Objects.nonNull(updateEntity.nodeType)) {
            this.nodeType = updateEntity.nodeType;
        }
        if (Objects.nonNull(updateEntity.cooperateType)) {
            this.cooperateType = updateEntity.cooperateType;
        }
        if (StringUtils.isNotEmpty(updateEntity.targetNodeCode)) {
            this.targetNodeCode = updateEntity.targetNodeCode;
        }
        if (StringUtils.isNotEmpty(updateEntity.targetNodeName)) {
            this.targetNodeName = updateEntity.targetNodeName;
        }
        if (StringUtils.isNotEmpty(updateEntity.approver)) {
            this.approver = updateEntity.approver;
        }
        if (StringUtils.isNotEmpty(updateEntity.collaborator)) {
            this.collaborator = updateEntity.collaborator;
        }
        if (Objects.nonNull(updateEntity.definitionId)) {
            this.definitionId = updateEntity.definitionId;
        }
        if (Objects.nonNull(updateEntity.instanceId)) {
            this.instanceId = updateEntity.instanceId;
        }
        if (Objects.nonNull(updateEntity.taskId)) {
            this.taskId = updateEntity.taskId;
        }
        if (StringUtils.isNotEmpty(updateEntity.skipType)) {
            this.skipType = updateEntity.skipType;
        }
        if (Objects.nonNull(updateEntity.flowStatus)) {
            this.flowStatus = updateEntity.flowStatus;
        }
        if (Objects.nonNull(updateEntity.message)) {
            this.message = updateEntity.message;
        }
        if (StringUtils.isNotEmpty(updateEntity.variable)) {
            this.variable = updateEntity.variable;
        }
        if (Objects.nonNull(updateEntity.ext)) {
            this.ext = updateEntity.ext;
        }
        if (StringUtils.isNotEmpty(updateEntity.formCustom)) {
            this.formCustom = updateEntity.formCustom;
        }
        if (StringUtils.isNotEmpty(updateEntity.formPath)) {
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
    public JPAPredicateFunction<CriteriaBuilder, Root<FlowHisTask>, List<Predicate>> entityPredicate() {
        return this.entityPredicate;
    }

    @Override
    public JPAUpdateMergeFunction<FlowHisTask> entityMerge() {
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
     * 协作方式(1审批 2转办 3委派 4会签 5票签 6加签 7减签)
     */
    @Column(name = "cooperate_type")
    private Integer cooperateType;

    /**
     * 流程名称
     */
    @Transient
    private String flowName;

    /**
     * 流程实例表id
     */
    @Column(name = "instance_id")
    private Long instanceId;

    /**
     * 任务表id
     */
    @Column(name = "task_id")
    private Long taskId;

    /**
     * 业务id
     */
    @Transient
    private String businessId;

    /**
     * 开始节点编码
     */
    @Column(name = "node_code")
    private String nodeCode;

    /**
     * 开始节点名称
     */
    @Column(name = "node_name")
    private String nodeName;

    /**
     * 开始节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关）
     */
    @Column(name = "node_type")
    private Integer nodeType;

    /**
     * 目标节点编码
     */
    @Column(name = "target_node_code")
    private String targetNodeCode;

    /**
     * 结束节点名称
     */
    @Column(name = "target_node_name")
    private String targetNodeName;

    /**
     * 审批者
     */
    @Column(name = "approver")
    private String approver;

    /**
     * 协作人(只有转办、会签、票签、委派)
     */
    @Column(name = "collaborator")
    private String collaborator;

    /**
     * 权限标识 permissionFlag的list形式
     */
    @Transient
    private List<String> permissionList;

    /**
     * 流程流转（PASS REJECT NONE）
     */
    @Column(name = "skip_type")
    private String skipType;

    /**
     * 流程状态（1审批中 2 审批通过 9已退回 10失效）
     */
    @Column(name = "flow_status")
    private String flowStatus;

    /**
     * 审批意见
     */
    @Column(name = "message")
    private String message;

    /**
     * 流程变量
     */
    @Column(name = "variable")
    private String variable;

    /**
     * 扩展字段 可用于存储业务详情
     */
    @Column(name = "ext")
    private String ext;

    /**
     * 创建者
     */
    @Transient
    private String createBy;

    /**
     * 审批表单是否自定义（Y是 2否）
     */
    @Column(name = "form_custom")
    private String formCustom;

    /**
     * 审批表单路径
     */
    @Column(name = "form_path")
    private String formPath;

}
