package com.warm.flow.orm.dao;

import com.warm.flow.core.dao.FlowInstanceDao;
import com.warm.flow.core.entity.Instance;
import com.warm.flow.orm.invoker.MapperInvoker;
import com.warm.flow.orm.mapper.FlowInstanceMapper;

import java.util.List;

/**
 * 流程实例Mapper接口
 *
 * @author warm
 * @date 2023-03-29
 */
public class FlowInstanceDaoImpl extends WarmDaoImpl<Instance> implements FlowInstanceDao {

    @Override
    public FlowInstanceMapper getMapper() {
        return MapperInvoker.getMapper(FlowInstanceMapper.class);
    }

    @Override
    public List<Instance> getByIdWithLock(List<Long> ids) {
        return getMapper().getByIdWithLock(ids);
    }

}
