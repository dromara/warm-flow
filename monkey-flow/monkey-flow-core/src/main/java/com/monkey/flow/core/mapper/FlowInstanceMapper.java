package com.monkey.flow.core.mapper;

import com.monkey.flow.core.domain.entity.FlowInstance;
import com.monkey.mybatis.core.mapper.FlowMapper;

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
