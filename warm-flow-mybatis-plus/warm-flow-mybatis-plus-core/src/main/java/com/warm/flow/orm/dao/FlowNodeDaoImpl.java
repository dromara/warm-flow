package com.warm.flow.orm.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.warm.flow.core.dao.FlowNodeDao;
import com.warm.flow.core.invoker.FrameInvoker;
import com.warm.flow.orm.entity.FlowNode;
import com.warm.flow.orm.mapper.FlowNodeMapper;
import com.warm.flow.orm.utils.TenantDeleteUtil;
import com.warm.tools.utils.CollUtil;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;


/**
 * 流程节点Mapper接口
 *
 * @author warm
 * @date 2023-03-29
 */
public class FlowNodeDaoImpl extends WarmDaoImpl<FlowNode> implements FlowNodeDao<FlowNode> {

    @Override
    public FlowNodeMapper getMapper() {
        return FrameInvoker.getBean(FlowNodeMapper.class);
    }

    @Override
    public FlowNode newEntity() {
        return new FlowNode();
    }

    @Override
    public List<FlowNode> getByNodeCodes(List<String> nodeCodes, Long definitionId) {
        LambdaQueryWrapper<FlowNode> queryWrapper = TenantDeleteUtil.getLambdaWrapperDefault(newEntity());
        queryWrapper.in(CollUtil.isEmpty(nodeCodes), FlowNode::getNodeCode, nodeCodes)
                .eq(FlowNode::getDefinitionId, definitionId);
        return getMapper().selectList(queryWrapper);
    }

    /**
     * 批量删除流程节点
     *
     * @param defIds 需要删除的数据主键集合
     * @return 结果
     */
    @Override
    public int deleteNodeByDefIds(Collection<? extends Serializable> defIds) {
        return delete(newEntity(), (luw) -> luw.in(FlowNode::getDefinitionId, defIds)
                , (lqw) -> lqw.in(FlowNode::getDefinitionId, defIds));
    }

}
