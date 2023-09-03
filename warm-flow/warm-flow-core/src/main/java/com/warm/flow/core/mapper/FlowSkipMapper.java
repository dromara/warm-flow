package com.warm.flow.core.mapper;

import com.warm.flow.core.domain.entity.FlowSkip;
import com.warm.mybatis.core.mapper.FlowMapper;

import java.util.List;

/**
 * 结点跳转关联Mapper接口
 *
 * @author hh
 * @date 2023-03-29
 */
public interface FlowSkipMapper extends FlowMapper<FlowSkip> {
    /**
     * 根据nodeId删除
     *
     * @param nodeId 需要删除的nodeId
     * @return 结果
     */
    int deleteByNodeId(Long nodeId);

    /**
     * 根据nodeIds删除
     *
     * @param nodeIds 需要删除的nodeIds
     * @return 结果
     */
    int deleteByNodeIds(List<Long> nodeIds);
}
