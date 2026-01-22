package org.dromara.warm.flow.orm.dao;

import com.easy.query.core.expression.lambda.SQLActionExpression1;
import org.dromara.warm.flow.core.orm.dao.FlowFormDao;
import org.dromara.warm.flow.core.utils.StringUtils;
import org.dromara.warm.flow.orm.entity.FlowForm;
import org.dromara.warm.flow.orm.entity.proxy.FlowFormProxy;

import java.util.List;
import java.util.Objects;

/**
 * @author vanlin
 * @className FlowFormDaoImpl
 * @description
 * @since 2024/12/9 14:29
 */
public class FlowFormDaoImpl extends WarmDaoImpl<FlowForm, FlowFormProxy> implements FlowFormDao<FlowForm> {

    @Override
    public FlowForm newEntity() {
        return new FlowForm();
    }

    @Override
    public List<FlowForm> queryByCodeList(List<String> formCodeList) {
        return queryable()
                .useLogicDelete(isLogicDelete())
                .where(proxy -> proxy.formCode().in(formCodeList)).toList();
    }

    @Override
    public int delete(FlowForm entity) {
        // 没有启用逻辑删除， 执行物理删除
        return (int) deletable()
            .useLogicDelete(isLogicDelete())
            .allowDeleteStatement(!isLogicDelete())
            .where(buildWhereCondition(entity))
            .executeRows();
    }

    /** 参考 mybatis 实现 构建删除语句条件， 使用 = 拼接 */
    @Override
    public SQLActionExpression1<FlowFormProxy> buildWhereCondition(FlowForm entity) {
        return o -> {
            o.id().eq(Objects.nonNull(entity.getId()), entity.getId());
            o.formCode().eq(StringUtils.isNotEmpty(entity.getFormCode()), entity.getFormCode());
            o.formName().eq(StringUtils.isNotEmpty(entity.getFormName()), entity.getFormName());
            o.formType().eq(Objects.nonNull(entity.getFormType()), entity.getFormType());
            o.formPath().eq(StringUtils.isNotEmpty(entity.getFormPath()), entity.getFormPath());
            o.formContent().eq(StringUtils.isNotEmpty(entity.getFormContent()), entity.getFormContent());
            o.ext().eq(StringUtils.isNotEmpty(entity.getExt()), entity.getExt());
            o.createTime().eq(Objects.nonNull(entity.getCreateTime()), entity.getCreateTime());
            o.updateTime().eq(Objects.nonNull(entity.getUpdateTime()), entity.getUpdateTime());
            o.tenantId().eq(StringUtils.isNotEmpty(entity.getTenantId()), entity.getTenantId());
            o.createBy().eq(StringUtils.isNotEmpty(entity.getCreateBy()), entity.getCreateBy());
            o.updateBy().eq(StringUtils.isNotEmpty(entity.getUpdateBy()), entity.getUpdateBy());
        };

    }

}
