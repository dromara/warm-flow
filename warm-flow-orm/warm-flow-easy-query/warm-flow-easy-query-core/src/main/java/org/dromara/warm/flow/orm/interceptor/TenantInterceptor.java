package org.dromara.warm.flow.orm.interceptor;

import com.easy.query.core.basic.extension.interceptor.EntityInterceptor;
import com.easy.query.core.basic.extension.interceptor.PredicateFilterInterceptor;
import com.easy.query.core.expression.parser.core.base.WherePredicate;
import com.easy.query.core.expression.segment.condition.PredicateSegment;
import com.easy.query.core.expression.segment.index.EntitySegmentComparer;
import com.easy.query.core.expression.sql.builder.EntityInsertExpressionBuilder;
import com.easy.query.core.expression.sql.builder.EntityUpdateExpressionBuilder;
import com.easy.query.core.expression.sql.builder.LambdaEntityExpressionBuilder;
import com.easy.query.core.expression.sql.builder.impl.DeleteExpressionBuilder;
import com.easy.query.core.expression.sql.builder.impl.QueryExpressionBuilder;
import com.easy.query.core.expression.sql.builder.impl.UpdateExpressionBuilder;
import lombok.extern.slf4j.Slf4j;
import org.dromara.warm.flow.core.FlowEngine;
import org.dromara.warm.flow.core.entity.RootEntity;
import org.dromara.warm.flow.core.handler.TenantHandler;
import org.dromara.warm.flow.core.utils.ObjectUtil;
import org.dromara.warm.flow.core.utils.StringUtils;

/**
 * 租户拦截器
 *
 * @author warm
 * @since 2025/12/20
 */
@Slf4j
public class TenantInterceptor implements EntityInterceptor, PredicateFilterInterceptor {

    public static final String NAME = "TenantInterceptor";

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public boolean apply(Class<?> entityClass) {
        return true;
    }

    @Override
    public void configure(Class<?> entityClass, LambdaEntityExpressionBuilder lambdaEntityExpressionBuilder, WherePredicate<Object> wherePredicate) {
        // 如果已经设置了租户id查询，就不需要在获取当前租户
        EntitySegmentComparer tenantIdComparer = new EntitySegmentComparer(entityClass, "tenantId");
        PredicateSegment where = getPredicateSegment(lambdaEntityExpressionBuilder);
        if (where == null) {
            return;
        }

        where.forEach(s -> {
            tenantIdComparer.visit(s);
            return tenantIdComparer.isInSegment();
        });

        if (tenantIdComparer.isInSegment()) {
            return;
        }

        TenantHandler tenantHandler = FlowEngine.tenantHandler();
        if (ObjectUtil.isNotNull(tenantHandler)) {
            wherePredicate.eq("tenantId", tenantHandler.getTenantId());
        }
    }

    @Override
    public void configureInsert(Class<?> entityClass, EntityInsertExpressionBuilder entityInsertExpressionBuilder, Object entity) {
        if (ObjectUtil.isNotNull(entity)) {
            RootEntity rootEntity = (RootEntity) entity;
            if (StringUtils.isNotEmpty(rootEntity.getTenantId())) {
                TenantHandler tenantHandler = FlowEngine.tenantHandler();
                if (ObjectUtil.isNotNull(tenantHandler)) {
                    rootEntity.setTenantId(tenantHandler.getTenantId());
                }
            }
        }
    }

    @Override
    public void configureUpdate(Class<?> entityClass, EntityUpdateExpressionBuilder entityUpdateExpressionBuilder, Object entity) {
        if (ObjectUtil.isNotNull(entity)) {
            RootEntity rootEntity = (RootEntity) entity;
            if (StringUtils.isNotEmpty(rootEntity.getTenantId())) {
                TenantHandler tenantHandler = FlowEngine.tenantHandler();
                if (ObjectUtil.isNotNull(tenantHandler)) {
                    rootEntity.setTenantId(tenantHandler.getTenantId());
                }
            }
        }
    }

    private PredicateSegment getPredicateSegment(LambdaEntityExpressionBuilder lambdaEntityExpressionBuilder) {
        PredicateSegment where = null;
        if (lambdaEntityExpressionBuilder instanceof QueryExpressionBuilder) {
            where = ((QueryExpressionBuilder) lambdaEntityExpressionBuilder).getWhere();
        } else if (lambdaEntityExpressionBuilder instanceof UpdateExpressionBuilder) {
            where = ((UpdateExpressionBuilder) lambdaEntityExpressionBuilder).getWhere();
        } else if (lambdaEntityExpressionBuilder instanceof DeleteExpressionBuilder) {
            where = ((DeleteExpressionBuilder) lambdaEntityExpressionBuilder).getWhere();
        }
        return where;
    }
}
