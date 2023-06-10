package com.monkey.flow.core.mapper;

import com.monkey.flow.core.domain.entity.FlowTask;
import com.monkey.mybatis.core.mapper.FlowBaseMapper;

import java.util.List;

/**
 * 待办任务Mapper接口
 *
 * @author hh
 * @date 2023-03-29
 */
public interface FlowTaskMapper extends FlowBaseMapper<FlowTask> {

    List<FlowTask> getByInsIds(List<Long> instanceIds);

    /**
     * 获取待办任务
     * @param flowTask
     * @return
     */
    List<FlowTask> toDoList(FlowTask flowTask);
}
