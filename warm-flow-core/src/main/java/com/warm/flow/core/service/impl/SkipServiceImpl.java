package com.warm.flow.core.service.impl;

import com.warm.flow.core.dao.FlowSkipDao;
import com.warm.flow.core.entity.Skip;
import com.warm.flow.core.orm.service.impl.WarmServiceImpl;
import com.warm.flow.core.service.SkipService;

import java.io.Serializable;
import java.util.Collection;

/**
 * 节点跳转关联Service业务层处理
 *
 * @author warm
 * @date 2023-03-29
 */
public class SkipServiceImpl extends WarmServiceImpl<FlowSkipDao<Skip>, Skip> implements SkipService {

    @Override
    public SkipService setDao(FlowSkipDao<Skip> warmDao) {
        this.warmDao = warmDao;
        return this;
    }

    @Override
    public int deleteSkipByDefIds(Collection<? extends Serializable> ids) {
        return getDao().deleteSkipByDefIds(ids);
    }
}
