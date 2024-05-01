package com.warm.flow.core.dao;

import com.warm.flow.core.entity.Skip;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * 节点跳转关联Mapper接口
 *
 * @author warm
 * @date 2023-03-29
 */
public interface FlowSkipDao<T extends Skip> extends WarmDao<T> {

    /**
     * 批量删除节点跳转关联
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSkipByDefIds(Collection<? extends Serializable> ids);
}
