package com.warm.flow.orm.entity;

import com.warm.flow.core.entity.User;
import com.warm.flow.orm.utils.JPAPredicateFunction;
import com.warm.flow.orm.utils.JPAUtil;
import com.warm.flow.core.utils.StringUtils;

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

    @Override
    public JPAPredicateFunction<CriteriaBuilder, Root<FlowUser>, List<Predicate>> entityPredicate() {
        return this.entityPredicate;
    }

    @Override
    public String orderByField(String orderByColumn) {
        return MAPPING.get(orderByColumn);
    }


    @Override
    public void initDefaultValue() {
    }

    @Override
    public void mergeUpdate(FlowUser updateEntity) {
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
    }

    /**
     * 1-审批人权限 2-转办人权限 3-抄送人权限 4-已审批人
     */
    private String type;

    /**
     * 权限(role:1/user:1)/已审批人(用户id)
     */
    @Column(name = "processed_by")
    private String processedBy;

    /**
     * 关联id（审批人和转办人是代办任务id，抄送人是实例id，已审批人是历史表id）
     */
    private Long associated;


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
