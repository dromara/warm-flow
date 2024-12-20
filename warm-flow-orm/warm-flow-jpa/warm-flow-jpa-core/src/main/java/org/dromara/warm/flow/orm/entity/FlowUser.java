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
import org.dromara.warm.flow.core.entity.User;
import org.dromara.warm.flow.core.utils.StringUtils;
import org.dromara.warm.flow.orm.utils.JPAPredicateFunction;
import org.dromara.warm.flow.orm.utils.JPAUpdateMergeFunction;
import org.dromara.warm.flow.orm.utils.JPAUtil;

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
 * 流程用户对象 flow_user
 *
 * @author xiarg
 * @since 2024/5/10 10:58
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "flow_user")
public class FlowUser extends JPARootEntity<FlowUser> implements User {
    public final static HashMap<String, String> MAPPING = new HashMap<>();

    static {
        JPAUtil.initMapping(FlowUser.class, MAPPING);
        MAPPING.putAll(JPARootEntity.JPA_ROOT_ENTITY_MAPPING);
    }

    @Transient
    private JPAPredicateFunction<CriteriaBuilder, Root<FlowUser>, List<Predicate>> entityPredicate =
            (criteriaBuilder, root, predicates) -> {
                if (StringUtils.isNotEmpty(this.type)) {
                    predicates.add(criteriaBuilder.equal(root.get("type"), this.type));
                }
                if (StringUtils.isNotEmpty(this.processedBy)) {
                    predicates.add(criteriaBuilder.equal(root.get("processedBy"), this.processedBy));
                }
                if (Objects.nonNull(this.associated)) {
                    predicates.add(criteriaBuilder.equal(root.get("associated"), this.associated));
                }
            };

    @Transient
    private JPAUpdateMergeFunction<FlowUser> entityMerge = (updateEntity) -> {
        if (StringUtils.isNotEmpty(updateEntity.type)) {
            this.type = updateEntity.type;
        }
        if (StringUtils.isNotEmpty(updateEntity.processedBy)) {
            this.processedBy = updateEntity.processedBy;
        }
        if (Objects.nonNull(updateEntity.associated)) {
            this.associated = updateEntity.associated;
        }
        if (Objects.nonNull(updateEntity.getCreateTime())) {
            this.setCreateTime(updateEntity.getCreateTime());
        }
        if (Objects.nonNull(updateEntity.getUpdateTime())) {
            this.setUpdateTime(updateEntity.getUpdateTime());
        }
    };

    @Override
    public JPAPredicateFunction<CriteriaBuilder, Root<FlowUser>, List<Predicate>> entityPredicate() {
        return this.entityPredicate;
    }

    @Override
    public JPAUpdateMergeFunction<FlowUser> entityMerge() {
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
     * 人员类型（1待办任务的审批人权限 2待办任务的转办人权限 3待办任务的委托人权限）
     */
    private String type;

    /**
     * 权限人
     */
    @Column(name = "processed_by")
    private String processedBy;

    /**
     * 任务表id
     */
    private Long associated;

    /**
     * 创建人：比如作为委托的人保存
     */
    @Column(name = "create_by")
    private String createBy;

}
