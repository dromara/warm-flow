package com.warm.flow.orm.dao;

import com.warm.flow.core.dao.FlowNodeDao;
import com.warm.flow.core.entity.Node;
import com.warm.flow.core.invoker.FrameInvoker;
import com.warm.flow.orm.entity.FlowNode;
import com.warm.flow.orm.mapper.FlowNodeMapper;

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
    public List<FlowNode> getByNodeCodes(List<String> nodeCodes, Long definitionId) {
        return getMapper().getByNodeCodes(nodeCodes, definitionId);
    }

    /**
     * 批量删除流程节点
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    @Override
    public int deleteNodeByDefIds(Collection<? extends Serializable> ids) {
        return getMapper().deleteNodeByDefIds(ids);
    }
}
