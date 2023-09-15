package com.warm.flow.core.mapper;

import com.warm.flow.core.domain.entity.FlowInstance;
import com.warm.mybatis.core.mapper.WarmMapper;

import java.util.List;

/**
 * 流程实例Mapper接口
 *
 * @author warm
 * @date 2023-03-29
 */
public interface FlowInstanceMapper extends WarmMapper<FlowInstance> {

    List<FlowInstance> getByIdWithLock(List<Long> ids);

}
