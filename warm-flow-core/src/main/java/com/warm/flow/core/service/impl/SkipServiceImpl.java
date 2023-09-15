package com.warm.flow.core.service.impl;

import com.warm.flow.core.domain.entity.FlowSkip;
import com.warm.flow.core.mapper.FlowSkipMapper;
import com.warm.flow.core.service.SkipService;
import com.warm.mybatis.core.service.impl.WarmServiceImpl;
import com.warm.mybatis.core.utils.SqlHelper;

import java.util.List;

/**
 * 结点跳转关联Service业务层处理
 *
 * @author warm
 * @date 2023-03-29
 */
public class SkipServiceImpl extends WarmServiceImpl<FlowSkipMapper, FlowSkip> implements SkipService {

    @Override
    public boolean deleteByNodeId(Long nodeId) {
        return SqlHelper.retBool(getMapper().deleteByNodeId(nodeId));
    }

    /**
     * 根据nodeIds删除
     *
     * @param nodeIds 需要删除的nodeIds
     * @return 结果
     */
    @Override
    public boolean deleteByNodeIds(List<Long> nodeIds) {
        return SqlHelper.retBool(getMapper().deleteByNodeIds(nodeIds));
    }

    @Override
    public List<FlowSkip> queryByDefAndCode(Long definitionId, String nowNodeCode) {
        return getMapper().queryByDefAndCode(definitionId, nowNodeCode);
    }
}
