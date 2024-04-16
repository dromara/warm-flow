package com.warm.flow.orm.dao;

import com.warm.flow.core.dao.FlowSkipDao;
import com.warm.flow.core.entity.Skip;
import com.warm.flow.core.invoker.FrameInvoker;
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
        return FrameInvoker.getBean(FlowSkipMapper.class);
    }


}
