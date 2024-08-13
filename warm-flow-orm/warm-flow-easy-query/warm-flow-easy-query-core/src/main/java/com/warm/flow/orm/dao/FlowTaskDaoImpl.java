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
        TenantDeleteUtil.applyContextCondition(entity);
        final boolean logicDelete = FlowFactory.getFlowConfig().isLogicDelete();
        return (int) entityQuery().deletable(entityClass())
            .where(proxy -> {
                proxy.instanceId().in(instanceIds); // 流程实例表id
                proxy.tenantId().eq(StringUtils.isNotEmpty(entity.getTenantId()), entity.getTenantId()); // 租户ID
            })
            .useLogicDelete(logicDelete)
            .allowDeleteStatement(!logicDelete)
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
        TenantDeleteUtil.applyContextCondition(entity);

        final boolean logicDelete = FlowFactory.getFlowConfig().isLogicDelete();
        return (int) entityQuery().deletable(entityClass())
            .useLogicDelete(logicDelete)
            .allowDeleteStatement(!logicDelete)
            .where(proxy -> {
                buildDeleteEqCondition(entity, proxy);
            })
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
        proxy.formCustom().eq(StringUtils.isNotEmpty(entity.getFormCustom()), entity.getFormCustom()); // 自定义表单
        proxy.formPath().eq(StringUtils.isNotEmpty(entity.getFormPath()), entity.getFormPath()); // 表单路径
        proxy.createTime().eq(Objects.nonNull(entity.getCreateTime()), entity.getCreateTime()); // 创建时间
        proxy.updateTime().eq(Objects.nonNull(entity.getUpdateTime()), entity.getUpdateTime()); // 更新时间
        proxy.tenantId().eq(StringUtils.isNotEmpty(entity.getTenantId()), entity.getTenantId()); // 租户ID
    }
}
