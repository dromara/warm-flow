package com.warm.flow.orm.entity;

import com.warm.flow.core.entity.Form;
import com.warm.flow.core.utils.StringUtils;
import com.warm.flow.orm.utils.JPAPredicateFunction;
import com.warm.flow.orm.utils.JPAUpdateMergeFunction;
import com.warm.flow.orm.utils.JPAUtil;

import jakarta.persistence.Column;
import jakarta.persistence.Transient;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * @author vanlin
 * @className FlowForm
 * @description
 * @since 2024/8/19 10:30
 */
public class FlowForm extends JPARootEntity<FlowForm> implements Form {
    public final static HashMap<String, String> MAPPING = new HashMap<>();

    static {
        JPAUtil.initMapping(FlowForm.class, MAPPING);
        MAPPING.putAll(JPARootEntity.JPA_ROOT_ENTITY_MAPPING);
    }

    @Transient
    private JPAPredicateFunction<CriteriaBuilder, Root<FlowForm>, List<Predicate>> entityPredicate =
            (criteriaBuilder, root, predicates) -> {
                if (StringUtils.isNotEmpty(this.formCode)) {
                    predicates.add(criteriaBuilder.equal(root.get("formCode"), this.formCode));
                }
                if (StringUtils.isNotEmpty(this.formName)) {
                    predicates.add(criteriaBuilder.equal(root.get("formName"), this.formName));
                }
                if (StringUtils.isNotEmpty(this.version)) {
                    predicates.add(criteriaBuilder.equal(root.get("version"), this.version));
                }
                if (Objects.nonNull(this.isPublish)) {
                    predicates.add(criteriaBuilder.equal(root.get("isPublish"), this.isPublish));
                }
                if (Objects.nonNull(this.formType)) {
                    predicates.add(criteriaBuilder.equal(root.get("formType"), this.formType));
                }
                if (StringUtils.isNotEmpty(this.formPath)) {
                    predicates.add(criteriaBuilder.equal(root.get("formPath"), this.formPath));
                }
                if (Objects.nonNull(this.formContent)) {
                    predicates.add(criteriaBuilder.equal(root.get("formContent"),this.formContent));
                }
                if (StringUtils.isNotEmpty(this.ext)) {
                    predicates.add(criteriaBuilder.equal(root.get("ext"), this.ext));
                }
            };

    @Transient
    private JPAUpdateMergeFunction<FlowForm> entityMerge = (updateEntity) -> {
        if (StringUtils.isNotEmpty(updateEntity.formCode)) {
            this.formCode = updateEntity.formCode;
        }
        if (StringUtils.isNotEmpty(updateEntity.formName)) {
            this.formName = updateEntity.formName;
        }
        if (StringUtils.isNotEmpty(updateEntity.version)) {
            this.version = updateEntity.version;
        }
        if (Objects.nonNull(updateEntity.isPublish)) {
            this.isPublish = updateEntity.isPublish;
        }
        if (Objects.nonNull(updateEntity.formType)) {
            this.formType = updateEntity.formType;
        }
        if (StringUtils.isNotEmpty(updateEntity.formPath)) {
            this.formPath = updateEntity.formPath;
        }
        if (StringUtils.isNotEmpty(updateEntity.formContent)) {
            this.formContent = updateEntity.formContent;
        }
        if (StringUtils.isNotEmpty(updateEntity.ext)) {
            this.ext = updateEntity.ext;
        }
        if (Objects.nonNull(updateEntity.getCreateTime())) {
            this.setCreateTime(updateEntity.getCreateTime());
        }
        if (Objects.nonNull(updateEntity.getUpdateTime())) {
            this.setUpdateTime(updateEntity.getUpdateTime());
        }
    };
    @Override
    public String orderByField(String orderByColumn) {
        return MAPPING.get(orderByColumn);
    }

    @Override
    public JPAPredicateFunction<CriteriaBuilder, Root<FlowForm>, List<Predicate>> entityPredicate() {
        return this.entityPredicate;
    }

    @Override
    public JPAUpdateMergeFunction<FlowForm> entityMerge() {
        return this.entityMerge;
    }

    @Override
    public void initDefaultValue() {
        if (Objects.isNull(this.isPublish)) {
            this.isPublish = 0;
        }
    }

    @Column(name = "form_code")
    private String formCode;

    @Column(name = "form_name")
    private String formName;

    private String version;

    @Column(name = "is_publish")
    private Integer isPublish;

    @Column(name = "form_type")
    private Integer formType;

    @Column(name = "form_path")
    private String formPath;

    @Column(name = "form_content")
    private String formContent;

    private String ext;

    @Override
    public String getFormCode() {
        return formCode;
    }

    @Override
    public FlowForm setFormCode(String formCode) {
        this.formCode = formCode;
        return this;
    }

    @Override
    public String getFormName() {
        return formName;
    }

    @Override
    public FlowForm setFormName(String formName) {
        this.formName = formName;
        return this;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public FlowForm setVersion(String version) {
        this.version = version;
        return this;
    }

    @Override
    public Integer getIsPublish() {
        return isPublish;
    }

    @Override
    public FlowForm setIsPublish(Integer isPublish) {
        this.isPublish = isPublish;
        return this;
    }

    @Override
    public Integer getFormType() {
        return formType;
    }

    @Override
    public FlowForm setFormType(Integer formType) {
        this.formType = formType;
        return this;
    }

    @Override
    public String getFormPath() {
        return formPath;
    }

    @Override
    public FlowForm setFormPath(String formPath) {
        this.formPath = formPath;
        return this;
    }

    @Override
    public String getFormContent() {
        return formContent;
    }

    @Override
    public FlowForm setFormContent(String formContent) {
        this.formContent = formContent;
        return this;
    }

    @Override
    public String getExt() {
        return ext;
    }

    @Override
    public FlowForm setExt(String ext) {
        this.ext = ext;
        return this;
    }

    @Override
    public String toString() {
        return "FlowForm{" +
                "id=" + super.getId() +
                ", createTime=" + super.getCreateTime() +
                ", updateTime=" + super.getUpdateTime() +
                ", formCode='" + formCode + '\'' +
                ", formName='" + formName + '\'' +
                ", version='" + version + '\'' +
                ", isPublish=" + isPublish +
                ", formType=" + formType +
                ", formPath='" + formPath + '\'' +
                ", formContent='" + formContent + '\'' +
                ", ext='" + ext + '\'' +
                '}';
    }
}
