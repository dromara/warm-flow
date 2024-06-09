package com.warm.flow.orm.entity;

import com.warm.flow.core.entity.User;
import com.warm.flow.orm.utils.JPAPredicateFunction;
import com.warm.flow.orm.utils.JPAUpdateMergeFunction;
import com.warm.flow.orm.utils.JPAUtil;
import com.warm.flow.core.utils.StringUtils;

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
 * 流程用户对象 flow_user
 *
 * @author xiarg
 * @date 2024/5/10 10:58
 */
@Entity
@Table(name = "flow_user")
public class FlowUser extends JPARootEntity<FlowUser> implements User{
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
    private JPAUpdateMergeFunction<FlowUser> entityMerge = (updateEntity) ->  {
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
     * 人员类型（1代办任务的审批人权限 2代办任务的转办人权限 3流程实例的抄送人权限 4待办任务的委托人权限）
     */
    private String type;

    /**
     * 权限人
     */
    @Column(name = "processed_by")
    private String processedBy;

    /**
     * 关联表id
     */
    private Long associated;

    /**
     * 创建人：比如作为委托的人保存
     */
    @Column(name = "create_by")
    private String createBy;

    @Override
    public String getType() {
        return type;
    }

    @Override
    public FlowUser setType(String type) {
        this.type = type;
        return this;
    }

    @Override
    public String getProcessedBy() {
        return processedBy;
    }

    @Override
    public FlowUser setProcessedBy(String processedBy) {
        this.processedBy = processedBy;
        return this;
    }

    @Override
    public Long getAssociated() {
        return associated;
    }

    @Override
    public FlowUser setAssociated(Long associated) {
        this.associated = associated;
        return this;
    }

    @Override
    public String getCreateBy() {
        return this.createBy;
    }

    @Override
    public User setCreateBy(String createBy) {
        this.createBy = createBy;
        return this;
    }

    @Override
    public String toString() {
        return "FlowUser{" +
                "id=" + super.getId() +
                ", createTime=" + super.getCreateTime() +
                ", updateTime=" + super.getUpdateTime() +
                ", tenantId='" + super.getTenantId() + '\'' +
                ", delFlag='" + super.getDelFlag() + '\'' +
                ", type='" + type + '\'' +
                ", processed_by='" + processedBy + '\'' +
                ", associated=" + associated +
                '}';
    }
}
