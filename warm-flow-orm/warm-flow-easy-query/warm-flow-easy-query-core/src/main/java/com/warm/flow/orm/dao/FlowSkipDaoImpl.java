package com.warm.flow.orm.dao;

import com.warm.flow.core.FlowFactory;
import com.warm.flow.core.dao.FlowSkipDao;
import com.warm.flow.core.utils.StringUtils;
import com.warm.flow.orm.entity.FlowSkip;
import com.warm.flow.orm.entity.proxy.FlowSkipProxy;
import com.warm.flow.orm.utils.TenantDeleteUtil;

import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;

/**
 * 节点跳转关联Mapper接口
 * @author link2fun
 */
public class FlowSkipDaoImpl extends WarmDaoImpl<FlowSkip, FlowSkipProxy> implements FlowSkipDao<FlowSkip> {
    @Override
    public int deleteSkipByDefIds(Collection<? extends Serializable> defIds) {
        FlowSkip entity = newEntity();
        TenantDeleteUtil.getEntity(entity);

        if (StringUtils.isNotEmpty(entity.getDelFlag())) {
            // 使用了逻辑删除
            return (int) entityQuery().updatable(entityClass())
                .where(proxy -> {
                    //noinspection unchecked
                    proxy.definitionId().in((Collection<? extends Long>) defIds);
                    proxy.tenantId().eq(StringUtils.isNotEmpty(entity.getTenantId()), entity.getTenantId());
                    proxy.delFlag().eq(StringUtils.isNotEmpty(entity.getDelFlag()), FlowFactory.getFlowConfig().getLogicNotDeleteValue());
                })
                .setColumns(proxy -> proxy.delFlag().set(FlowFactory.getFlowConfig().getLogicDeleteValue()))
                .executeRows();
        }
        // 没有使用逻辑删除， 直接物理删除
        return (int) entityQuery().deletable(entityClass())
            .where(proxy -> {
                //noinspection unchecked
                proxy.definitionId().in((Collection<? extends Long>) defIds);
                proxy.tenantId().eq(StringUtils.isNotEmpty(entity.getTenantId()), entity.getTenantId());
            })
            .allowDeleteStatement(true)
            .executeRows();
    }

    @Override
    public Class<FlowSkip> entityClass() {
        return FlowSkip.class;
    }

    @Override
    public FlowSkip newEntity() {
        return new FlowSkip();
    }

    @Override
    public int delete(FlowSkip entity) {
        TenantDeleteUtil.getEntity(entity);

        if (StringUtils.isNotEmpty(entity.getDelFlag())) {
            // 使用了逻辑删除
            return (int) entityQuery().updatable(entityClass())
                .where(proxy -> {
                    buildDeleteEqCondition(entity, proxy);
                })
                .setColumns(proxy -> proxy.delFlag().set(FlowFactory.getFlowConfig().getLogicDeleteValue()))
                .executeRows();
        }

        // 没有使用逻辑删除， 直接物理删除
        return (int) entityQuery().deletable(entityClass())
            .where(proxy -> {
                buildDeleteEqCondition(entity, proxy);
            })
            .allowDeleteStatement(true)
            .executeRows();
    }


    /** 参照 mybatis 实现 构建删除时的条件 */
    private static void buildDeleteEqCondition(FlowSkip entity, FlowSkipProxy proxy) {
        proxy.id().eq(Objects.nonNull(entity.getId()), entity.getId());// 主键
        proxy.definitionId().eq(Objects.nonNull(entity.getDefinitionId()), entity.getDefinitionId());// 流程id
        proxy.nowNodeCode().eq(StringUtils.isNotEmpty(entity.getNowNodeCode()), entity.getNowNodeCode());// 当前流程节点的编码
        proxy.nowNodeType().eq(Objects.nonNull(entity.getNowNodeType()), entity.getNowNodeType());// 当前节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关）
        proxy.nextNodeCode().eq(StringUtils.isNotEmpty(entity.getNextNodeCode()), entity.getNextNodeCode());// 下一个流程节点的编码
        proxy.nextNodeType().eq(Objects.nonNull(entity.getNextNodeType()), entity.getNextNodeType());// 下一个节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关）
        proxy.skipName().eq(StringUtils.isNotEmpty(entity.getSkipName()), entity.getSkipName());// 跳转名称
        proxy.skipType().eq(StringUtils.isNotEmpty(entity.getSkipType()), entity.getSkipType());// 跳转类型（PASS审批通过 REJECT退回）
        proxy.coordinate().eq(StringUtils.isNotEmpty(entity.getCoordinate()), entity.getCoordinate());// 流程跳转坐标
        proxy.skipCondition().eq(StringUtils.isNotEmpty(entity.getSkipCondition()), entity.getSkipCondition());// 跳转条件
        proxy.createTime().eq(Objects.nonNull(entity.getCreateTime()), entity.getCreateTime());// 创建时间
        proxy.updateTime().eq(Objects.nonNull(entity.getUpdateTime()), entity.getUpdateTime());// 更新时间
        proxy.tenantId().eq(StringUtils.isNotEmpty(entity.getTenantId()), entity.getTenantId());// 租户ID
        proxy.delFlag().eq(StringUtils.isNotEmpty(entity.getDelFlag()), entity.getDelFlag());// 删除标记
    }
}
