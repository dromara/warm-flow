package com.warm.flow.orm.dao;

import com.easy.query.core.util.EasyArrayUtil;
import com.easy.query.core.util.EasyCollectionUtil;
import com.warm.flow.core.FlowFactory;
import com.warm.flow.core.dao.FlowUserDao;
import com.warm.flow.core.utils.StringUtils;
import com.warm.flow.orm.entity.FlowUser;
import com.warm.flow.orm.entity.proxy.FlowUserProxy;
import com.warm.flow.orm.utils.TenantDeleteUtil;

import java.util.List;
import java.util.Objects;

/**
 * 流程用户Mapper接口
 * @author link2fun
 */
public class FlowUserDaoImpl extends WarmDaoImpl<FlowUser, FlowUserProxy> implements FlowUserDao<FlowUser> {
    @Override
    public int deleteByTaskIds(List<Long> taskIdList) {
        FlowUser entity = newEntity();
        TenantDeleteUtil.applyContextCondition(entity);
        final boolean logicDelete = FlowFactory.getFlowConfig().isLogicDelete();

        return (int) entityQuery().deletable(entityClass())
            .where(proxy -> {
                proxy.associated().in(taskIdList);  // 任务id
                proxy.tenantId().eq(StringUtils.isNotEmpty(entity.getTenantId()), entity.getTenantId()); // 租户ID
            })
            .useLogicDelete(logicDelete)
            .allowDeleteStatement(!logicDelete)
            .executeRows();

    }

    @Override
    public List<FlowUser> listByAssociatedAndTypes(List<Long> associateds, String[] types) {

        FlowUser entity = newEntity();
        TenantDeleteUtil.applyContextCondition(entity);
        final boolean logicDelete = FlowFactory.getFlowConfig().isLogicDelete();
        return entityQuery().queryable(entityClass())
            .useLogicDelete(logicDelete)
            .where(proxy -> {
                proxy.associated().in(EasyCollectionUtil.isNotEmpty(associateds), associateds); // 任务表id
                proxy.type().in(EasyArrayUtil.isNotEmpty(types), types); // 人员类型
                proxy.tenantId().eq(StringUtils.isNotEmpty(entity.getTenantId()), entity.getTenantId()); // 租户ID
            })
            .toList();
    }

    @Override
    public List<FlowUser> listByProcessedBys(Long associated, List<String> processedBys, String[] types) {
        FlowUser entity = newEntity();
        TenantDeleteUtil.applyContextCondition(entity);
        final boolean logicDelete = FlowFactory.getFlowConfig().isLogicDelete();
        return  entityQuery().queryable(entityClass())
            .useLogicDelete(logicDelete)
            .where(proxy -> {
                proxy.associated().eq(Objects.nonNull(associated), associated); // 任务表id
                proxy.processedBy().in(EasyCollectionUtil.isNotEmpty(processedBys), processedBys); // 权限人
                proxy.type().in(EasyArrayUtil.isNotEmpty(types),types); // 人员类型
                proxy.tenantId().eq(StringUtils.isNotEmpty(entity.getTenantId()), entity.getTenantId()); // 租户ID
            })
            .toList();
    }

    @Override
    public Class<FlowUser> entityClass() {
        return FlowUser.class;
    }

    @Override
    public FlowUser newEntity() {
        return new FlowUser();
    }

    @Override
    public int delete(FlowUser entity) {
        TenantDeleteUtil.applyContextCondition(entity);
        final boolean logicDelete = FlowFactory.getFlowConfig().isLogicDelete();
        // 没有使用逻辑删除， 直接物理删除
        return (int) entityQuery().deletable(entityClass())
            .useLogicDelete(logicDelete)
            .allowDeleteStatement(!logicDelete)
            .where(f -> {
                buildDeleteEqCondition(entity, f);
            })
            .executeRows();
    }

    /** 参照 mybatis 实现， 构建删除条件 */
    private void buildDeleteEqCondition(FlowUser entity, FlowUserProxy proxy) {
        proxy.id().eq(Objects.nonNull(entity.getId()), entity.getId()); // 主键
        proxy.type().eq(StringUtils.isNotEmpty(entity.getType()), entity.getType()); // 人员类型
        proxy.processedBy().eq(StringUtils.isNotEmpty(entity.getProcessedBy()), entity.getProcessedBy()); // 权限人
        proxy.associated().eq(Objects.nonNull(entity.getAssociated()), entity.getAssociated()); // 任务表id
        proxy.createBy().eq(StringUtils.isNotEmpty(entity.getCreateBy()), entity.getCreateBy()); // 创建人
        proxy.createTime().eq(Objects.nonNull(entity.getCreateTime()), entity.getCreateTime()); // 创建时间
        proxy.updateTime().eq(Objects.nonNull(entity.getUpdateTime()), entity.getUpdateTime()); // 更新时间
        proxy.tenantId().eq(StringUtils.isNotEmpty(entity.getTenantId()), entity.getTenantId()); // 租户ID
    }
}
