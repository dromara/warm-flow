package org.dromara.warm.flow.orm.entity;

import com.easy.query.core.annotation.*;
import com.easy.query.core.basic.extension.logicdel.LogicDeleteStrategyEnum;
import com.easy.query.core.proxy.ProxyEntityAvailable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.dromara.warm.flow.core.entity.Form;
import org.dromara.warm.flow.orm.entity.proxy.FlowFormProxy;

import java.util.Date;

/**
 * @author vanlin
 * @className FlowForm
 * @description
 * @since 2024/8/19 10:30
 */
@Getter
@Setter
@EntityProxy
@Accessors(chain = true)
@EasyAlias("flowForm")
@Table("flow_form")
public class FlowForm implements Form, ProxyEntityAvailable<FlowForm, FlowFormProxy> {

    /**
     * 主键
     */
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    @Column(value = "id", primaryKey = true)
    private Long id;

    /**
     * 表单编码
     */
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    private String formCode;

    /**
     * 表单名称
     */
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    private String formName;

    /**
     * 表单版本
     */
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    private String version;

    /**
     * 是否发布（0未发布 1已发布 9失效）
     */
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    private Integer isPublish;

    /**
     * 表单类型（0内置表单 存 form_content        1外挂表单 存form_path）
     */
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    private Integer formType;

    /**
     * 表单路径
     */
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    private String formPath;

    /**
     * 表单内容
     */
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    private String formContent;

    /**
     * 表单扩展，用户自行使用
     */
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    private String ext;
    /**
     * 创建时间
     */
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    private Date createTime;

    /**
     * 更新时间
     */
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    private Date updateTime;

    /**
     * 租户ID
     */
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    private String tenantId;

    /**
     * 删除标记
     */
    @LogicDelete(strategy = LogicDeleteStrategyEnum.CUSTOM,strategyName = "WarmFlowLogicDelete")
    private String delFlag;

    @Override
    public String toString() {
        return "FlowForm{" +
                "id=" + id +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", tenantId='" + tenantId + '\'' +
                ", delFlag='" + delFlag + '\'' +
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
