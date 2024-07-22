package com.warm.flow.orm.dao;

import com.warm.flow.core.FlowFactory;
import com.warm.flow.core.dao.FlowTaskDao;
import com.warm.flow.core.utils.StringUtils;
import com.warm.flow.orm.entity.FlowTask;
import com.warm.flow.orm.entity.proxy.FlowTaskProxy;
import com.warm.flow.orm.utils.TenantDeleteUtil;

import java.util.List;
import java.util.Objects;

/**
 * 待办任务Mapper接口
 * @author link2fun
 */
public class FlowTaskDaoImpl extends WarmDaoImpl<FlowTask, FlowTaskProxy> implements FlowTaskDao<FlowTask> {
    @Override
    public int deleteByInsIds(List<Long> instanceIds) {
        FlowTask entity = newEntity();
        TenantDeleteUtil.getEntity(entity);
        if (StringUtils.isNotEmpty(entity.getDelFlag())) {
            // 使用了逻辑删除
            return (int) entityQuery().updatable(entityClass())
                .where(proxy -> {
                    proxy.instanceId().in(instanceIds); // 流程实例表id
                    proxy.tenantId().eq(StringUtils.isNotEmpty(entity.getTenantId()), entity.getTenantId()); // 租户ID
                    proxy.delFlag().eq(StringUtils.isNotEmpty(entity.getDelFlag()), entity.getDelFlag()); // 删除标记
                })
                .setColumns(proxy -> proxy.delFlag().set(FlowFactory.getFlowConfig().getLogicDeleteValue()))
                .executeRows();
        }
        // 没有使用逻辑删除， 直接物理删除
        return (int) entityQuery().deletable(entityClass())
            .where(proxy -> {
                proxy.instanceId().in(instanceIds); // 流程实例表id
                proxy.tenantId().eq(StringUtils.isNotEmpty(entity.getTenantId()), entity.getTenantId()); // 租户ID
            })
            .allowDeleteStatement(true)
            .executeRows();

    }

    @Override
    public Class<FlowTask> entityClass() {
        return FlowTask.class;
    }

    @Override
    public FlowTask newEntity() {
        return new FlowTask();
    }

    @Override
    public int delete(FlowTask entity) {
        TenantDeleteUtil.getEntity(entity);

        if (StringUtils.isNotEmpty(entity.getDelFlag())) {
            // 使用了逻辑删除
           return (int) entityQuery().updatable(entityClass())
                .where(proxy -> {
                    buildDeleteEqCondition(entity, proxy);
                })
                .setColumns(f -> f.delFlag().set(FlowFactory.getFlowConfig().getLogicDeleteValue()))
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

    /** 参照 mybatis 实现， 构建删除条件 */
    private static void buildDeleteEqCondition(FlowTask entity, FlowTaskProxy proxy) {
        proxy.id().eq(Objects.nonNull(entity.getId()), entity.getId()); // 主键
        proxy.nodeCode().eq(StringUtils.isNotEmpty(entity.getNodeCode()), entity.getNodeCode()); // 节点编码
        proxy.nodeName().eq(StringUtils.isNotEmpty(entity.getNodeName()), entity.getNodeName()); // 节点名称
        proxy.nodeType().eq(Objects.nonNull(entity.getNodeType()), entity.getNodeType()); // 节点类型
        proxy.definitionId().eq(Objects.nonNull(entity.getDefinitionId()), entity.getDefinitionId()); // 对应flow_definition表的id
        proxy.instanceId().eq(Objects.nonNull(entity.getInstanceId()), entity.getInstanceId()); // 流程实例表id
        proxy.createTime().eq(Objects.nonNull(entity.getCreateTime()), entity.getCreateTime()); // 创建时间
        proxy.updateTime().eq(Objects.nonNull(entity.getUpdateTime()), entity.getUpdateTime()); // 更新时间
        proxy.tenantId().eq(StringUtils.isNotEmpty(entity.getTenantId()), entity.getTenantId()); // 租户ID
        proxy.delFlag().eq(StringUtils.isNotEmpty(entity.getDelFlag()), entity.getDelFlag()); // 删除标记
    }
}
