package org.dromara.warm.flow.orm.dao;

import org.dromara.warm.flow.core.FlowEngine;
import org.dromara.warm.flow.core.orm.dao.FlowFormDao;
import org.dromara.warm.flow.core.utils.StringUtils;
import org.dromara.warm.flow.orm.entity.FlowForm;
import org.dromara.warm.flow.orm.entity.proxy.FlowFormProxy;
import org.dromara.warm.flow.orm.utils.TenantDeleteUtil;

import java.util.Objects;

/**
 * @author vanlin
 * @className FlowFormDaoImpl
 * @description
 * @since 2024/12/9 14:29
 */
public class FlowFormDaoImpl extends WarmDaoImpl<FlowForm, FlowFormProxy> implements FlowFormDao<FlowForm> {

    @Override
    public Class<FlowForm> entityClass() {
        return FlowForm.class;
    }

    @Override
    public FlowForm newEntity() {
        return new FlowForm();
    }

    @Override
    public int delete(FlowForm entity) {
        TenantDeleteUtil.applyContextCondition(entity);
        final boolean logicDelete = FlowEngine.getFlowConfig().isLogicDelete();

        // 没有启用逻辑删除， 执行物理删除
        return (int) entityQuery().deletable(entityClass())
            .useLogicDelete(logicDelete)
            .allowDeleteStatement(!logicDelete)
            .where(proxy -> buildDeleteEqCondition(entity, proxy))
            .executeRows();
    }

    /** 参考 mybatis 实现 构建删除语句条件， 使用 = 拼接 */
    private static void buildDeleteEqCondition(FlowForm entity, FlowFormProxy proxy) {
        proxy.id().eq(Objects.nonNull(entity.getId()), entity.getId()); // 主键
        proxy.formCode().eq(StringUtils.isNotEmpty(entity.getFormCode()), entity.getFormCode()); // 表单编码
        proxy.formName().eq(StringUtils.isNotEmpty(entity.getFormName()), entity.getFormName()); // 表单名称
        proxy.formType().eq(Objects.nonNull(entity.getFormType()), entity.getFormType()); // 表单类型
        proxy.formPath().eq(StringUtils.isNotEmpty(entity.getFormPath()), entity.getFormPath()); // 表单路径
        proxy.formContent().eq(StringUtils.isNotEmpty(entity.getFormContent()), entity.getFormContent()); // 表单内容
        proxy.ext().eq(StringUtils.isNotEmpty(entity.getExt()), entity.getExt()); // 业务详情 存业务类的json
        proxy.createTime().eq(Objects.nonNull(entity.getCreateTime()), entity.getCreateTime()); // 创建时间
        proxy.updateTime().eq(Objects.nonNull(entity.getUpdateTime()), entity.getUpdateTime()); // 更新时间
        proxy.tenantId().eq(StringUtils.isNotEmpty(entity.getTenantId()), entity.getTenantId()); // 租户ID
    }

}
