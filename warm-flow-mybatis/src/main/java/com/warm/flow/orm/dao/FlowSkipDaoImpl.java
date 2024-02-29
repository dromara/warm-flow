package com.warm.flow.orm.dao;

import com.warm.flow.core.dao.FlowSkipDao;
import com.warm.flow.core.entity.Skip;
import com.warm.flow.orm.invoker.MapperInvoker;
import com.warm.flow.orm.mapper.FlowSkipMapper;

import java.util.List;

/**
 * 节点跳转关联Mapper接口
 *
 * @author warm
 * @date 2023-03-29
 */
public class FlowSkipDaoImpl extends WarmDaoImpl<Skip> implements FlowSkipDao {

    @Override
    public FlowSkipMapper getMapper() {
        return MapperInvoker.getMapper(FlowSkipMapper.class);
    }

    /**
     * 根据nodeId删除
     *
     * @param nodeId 需要删除的nodeId
     * @return 结果
     */
    @Override
    public int deleteByNodeId(Long nodeId) {
        return getMapper().deleteByNodeId(nodeId);
    }

    /**
     * 根据nodeIds删除
     *
     * @param nodeIds 需要删除的nodeIds
     * @return 结果
     */
    @Override
    public int deleteByNodeIds(List<Long> nodeIds) {
        return getMapper().deleteByNodeIds(nodeIds);
    }

    /**
     * 获取当前节点跳转
     *
     * @param definitionId
     * @param nowNodeCode
     * @return
     */
    @Override
    public List<Skip> queryByDefAndCode(Long definitionId, String nowNodeCode) {
        return getMapper().queryByDefAndCode(definitionId, nowNodeCode);
    }

}
