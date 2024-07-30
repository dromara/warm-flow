package com.warm.flow.orm.dao;

import com.warm.flow.core.dao.FlowInstanceDao;
import com.warm.flow.core.utils.StringUtils;
import com.warm.flow.orm.entity.FlowInstance;
import com.warm.flow.orm.entity.proxy.FlowInstanceProxy;
import com.warm.flow.orm.utils.TenantDeleteUtil;

import java.util.Objects;

/**
 * 流程实例Mapper接口
 *
 * @author link2fun
 */
public class FlowInstanceDaoImpl extends WarmDaoImpl<FlowInstance, FlowInstanceProxy> implements FlowInstanceDao<FlowInstance> {
    @Override
    public Class<FlowInstance> entityClass() {
        return FlowInstance.class;
    }

    @Override
    public FlowInstance newEntity() {
        return new FlowInstance();
    }

    @Override
    public int delete(FlowInstance entity) {

        TenantDeleteUtil.getEntity(entity);

        if (StringUtils.isNotEmpty(entity.getDelFlag())) {
            // 有逻辑删除
            return (int) entityQuery().updatable(entityClass())
                .where(proxy -> {
                    buildDeleteEqCondition(entity, proxy);
                })
                .executeRows();
        }


        return (int) entityQuery().deletable(entityClass())
            .allowDeleteStatement(true)
            .where(proxy -> buildDeleteEqCondition(entity, proxy))
            .executeRows();
    }

    /** 构建删除的过滤条件 使用 = 拼接， 参考 mybatis 实现 */
    private static void buildDeleteEqCondition(FlowInstance entity, FlowInstanceProxy proxy) {
        proxy.id().eq(Objects.nonNull(entity.getId()), entity.getId());
        proxy.businessId().eq(StringUtils.isNotEmpty(entity.getBusinessId()), entity.getBusinessId()); // 业务id
        proxy.definitionId().eq(Objects.nonNull(entity.getDefinitionId()), entity.getDefinitionId()); // 流程定义id
        proxy.nodeCode().eq(StringUtils.isNotEmpty(entity.getNodeCode()), entity.getNodeCode()); // 节点编码
        proxy.nodeName().eq(StringUtils.isNotEmpty(entity.getNodeName()), entity.getNodeName()); // 节点名称
        proxy.nodeType().eq(Objects.nonNull(entity.getNodeType()), entity.getNodeType()); // 节点类型
        proxy.flowStatus().eq(Objects.nonNull(entity.getFlowStatus()), entity.getFlowStatus()); // 流程状态
        proxy.variable().eq(StringUtils.isNotEmpty(entity.getVariable()), entity.getVariable()); // 流程变量
        proxy.createBy().eq(StringUtils.isNotEmpty(entity.getCreateBy()), entity.getCreateBy()); // 创建人
        proxy.createTime().eq(Objects.nonNull(entity.getCreateTime()), entity.getCreateTime()); // 创建时间
        proxy.updateTime().eq(Objects.nonNull(entity.getUpdateTime()), entity.getUpdateTime()); // 更新时间
        proxy.ext().eq(StringUtils.isNotEmpty(entity.getExt()), entity.getExt()); // 扩展字段
        proxy.tenantId().eq(StringUtils.isNotEmpty(entity.getTenantId()), entity.getTenantId()); // 租户id
        proxy.delFlag().eq(StringUtils.isNotEmpty(entity.getDelFlag()), entity.getDelFlag()); // 删除标记
    }
}
