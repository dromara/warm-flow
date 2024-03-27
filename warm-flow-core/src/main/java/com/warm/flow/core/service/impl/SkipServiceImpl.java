package com.warm.flow.core.service.impl;

import com.warm.flow.core.dao.FlowSkipDao;
import com.warm.flow.core.entity.Skip;
import com.warm.flow.core.orm.service.impl.WarmServiceImpl;
import com.warm.flow.core.service.SkipService;
import com.warm.flow.core.utils.SqlHelper;

import java.util.List;

/**
 * 节点跳转关联Service业务层处理
 *
 * @author warm
 * @date 2023-03-29
 */
public class SkipServiceImpl extends WarmServiceImpl<FlowSkipDao, Skip> implements SkipService {

    @Override
    public SkipService setDao(FlowSkipDao warmDao) {
        this.warmDao = warmDao;
        return this;
    }


    @Override
    public List<Skip> queryByDefAndCode(Long definitionId, String nowNodeCode) {
        return getDao().queryByDefAndCode(definitionId, nowNodeCode);
    }
}
