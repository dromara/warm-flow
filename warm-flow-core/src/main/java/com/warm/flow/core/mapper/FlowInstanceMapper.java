package com.warm.flow.core.mapper;

import com.warm.flow.core.domain.entity.FlowInstance;
import com.warm.mybatis.core.mapper.FlowMapper;

import java.util.List;

/**
 * 流程实例Mapper接口
 *
 * @author hh
 * @date 2023-03-29
 */
public interface FlowInstanceMapper extends FlowMapper<FlowInstance> {

    List<FlowInstance> getByIdWithLock(List<Long> ids);

}
