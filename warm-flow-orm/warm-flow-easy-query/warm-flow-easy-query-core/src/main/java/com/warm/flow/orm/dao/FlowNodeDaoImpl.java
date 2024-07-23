package com.warm.flow.orm.dao;

import com.easy.query.core.util.EasyCollectionUtil;
import com.warm.flow.core.FlowFactory;
import com.warm.flow.core.dao.FlowNodeDao;
import com.warm.flow.core.utils.StringUtils;
import com.warm.flow.orm.entity.FlowNode;
import com.warm.flow.orm.entity.proxy.FlowNodeProxy;
import com.warm.flow.orm.utils.TenantDeleteUtil;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * 流程节点Mapper接口
 * @author link2fun
 */
public class FlowNodeDaoImpl extends WarmDaoImpl<FlowNode, FlowNodeProxy> implements FlowNodeDao<FlowNode> {

    @Override
    public List<FlowNode> getByNodeCodes(List<String> nodeCodes, Long definitionId) {
        FlowNode entityFilter = newEntity();
        TenantDeleteUtil.getEntity(entityFilter);

        return entityQuery().queryable(entityClass())
            .where(proxy -> {
                proxy.definitionId().eq(definitionId);
                proxy.nodeCode().in(EasyCollectionUtil.isNotEmpty(nodeCodes), nodeCodes);
                proxy.delFlag().eq(StringUtils.isNotEmpty(entityFilter.getDelFlag()), entityFilter.getDelFlag());
                proxy.tenantId().eq(StringUtils.isNotEmpty(entityFilter.getTenantId()), entityFilter.getTenantId());
            })
            .toList();
    }

    @Override
    public int deleteNodeByDefIds(Collection<? extends Serializable> defIds) {

        final FlowNode entity = newEntity();
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
    public Class<FlowNode> entityClass() {
        return FlowNode.class;
    }

    @Override
    public FlowNode newEntity() {
        return new FlowNode();
    }

    @Override
    public int delete(FlowNode entity) {

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

        // 不适用逻辑删除
        return (int) entityQuery().deletable(entityClass())
            .where(proxy -> {
                buildDeleteEqCondition(entity, proxy);
            })
            .allowDeleteStatement(true)
            .executeRows();
    }

    /** 参考 mybatis 的实现 构建删除时的条件 */
    private static void buildDeleteEqCondition(FlowNode entity, FlowNodeProxy proxy) {
        proxy.id().eq(Objects.nonNull(entity.getId()), entity.getId()); // 主键
        proxy.nodeType().eq(Objects.nonNull(entity.getNodeType()), entity.getNodeType()); // 节点类型
        proxy.definitionId().eq(Objects.nonNull(entity.getDefinitionId()), entity.getDefinitionId()); // 流程id
        proxy.nodeCode().eq(StringUtils.isNotEmpty(entity.getNodeCode()), entity.getNodeCode()); // 流程节点编码
        proxy.nodeName().eq(StringUtils.isNotEmpty(entity.getNodeName()), entity.getNodeName()); // 流程节点名称
        proxy.permissionFlag().eq(StringUtils.isNotEmpty(entity.getPermissionFlag()), entity.getPermissionFlag()); // 权限标识
        proxy.nodeRatio().eq(Objects.nonNull(entity.getNodeRatio()), entity.getNodeRatio()); // 流程签署比例值
        proxy.coordinate().eq(StringUtils.isNotEmpty(entity.getCoordinate()), entity.getCoordinate()); // 流程节点坐标
        proxy.skipAnyNode().eq(StringUtils.isNotEmpty(entity.getSkipAnyNode()), entity.getSkipAnyNode()); // 是否可以跳转任意节点（Y是 N否）
        proxy.listenerType().eq(StringUtils.isNotEmpty(entity.getListenerType()), entity.getListenerType()); // 监听器类型
        proxy.listenerPath().eq(StringUtils.isNotEmpty(entity.getListenerPath()), entity.getListenerPath()); // 监听器路径
        proxy.handlerType().eq(StringUtils.isNotEmpty(entity.getHandlerType()), entity.getHandlerType()); // 处理器类型
        proxy.handlerPath().eq(StringUtils.isNotEmpty(entity.getHandlerPath()), entity.getHandlerPath()); // 处理器路径
        proxy.formCustom().eq(StringUtils.isNotEmpty(entity.getFormCustom()), entity.getFormCustom()); // 处理器类型
        proxy.formPath().eq(StringUtils.isNotEmpty(entity.getFormPath()), entity.getFormPath()); // 处理器路径
        proxy.version().eq(StringUtils.isNotEmpty(entity.getVersion()), entity.getVersion()); // 版本
        proxy.createTime().eq(Objects.nonNull(entity.getCreateTime()), entity.getCreateTime()); // 创建时间
        proxy.updateTime().eq(Objects.nonNull(entity.getUpdateTime()), entity.getUpdateTime()); // 更新时间
        proxy.tenantId().eq(StringUtils.isNotEmpty(entity.getTenantId()), entity.getTenantId()); // 租户id
        proxy.delFlag().eq(StringUtils.isNotEmpty(entity.getDelFlag()), entity.getDelFlag()); // 删除标记
    }
}
