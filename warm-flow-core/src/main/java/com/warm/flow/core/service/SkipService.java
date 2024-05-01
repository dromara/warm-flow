package com.warm.flow.core.service;

import com.warm.flow.core.entity.Skip;
import com.warm.flow.core.orm.service.IWarmService;

import java.io.Serializable;
import java.util.Collection;

/**
 * 节点跳转关联Service接口
 *
 * @author warm
 * @date 2023-03-29
 */
public interface SkipService extends IWarmService<Skip> {

    /**
     * 批量删除节点跳转关联
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSkipByDefIds(Collection<? extends Serializable> ids);
}
