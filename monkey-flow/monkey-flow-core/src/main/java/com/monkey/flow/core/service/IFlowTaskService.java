package com.monkey.flow.core.service;

import com.monkey.flow.core.domain.entity.FlowTask;
import com.monkey.mybatis.core.service.IFlowBaseService;

import java.util.List;

/**
 * 待办任务Service接口
 *
 * @author hh
 * @date 2023-03-29
 */
public interface IFlowTaskService extends IFlowBaseService<FlowTask> {

    /**
     * 根据实例ids获取待办任务
     * @param instanceIds
     * @return
     */
    List<FlowTask> getByInsIds(List<Long> instanceIds);

    /**
     * 获取待办任务
     * @param flowTask
     * @return
     */
    List<FlowTask> toDoList(FlowTask flowTask);
}
