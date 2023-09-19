package com.warm.flow.core.service;

import com.warm.flow.core.domain.entity.FlowTask;
import com.warm.mybatis.core.page.Page;
import com.warm.mybatis.core.service.IWarmService;

import java.util.List;

/**
 * 待办任务Service接口
 *
 * @author warm
 * @date 2023-03-29
 */
public interface TaskService extends IWarmService<FlowTask> {

    /**
     * 根据实例ids获取待办任务
     *
     * @param instanceId
     * @return
     */
    List<FlowTask> getByInsId(Long instanceId);

    /**
     * 分页查询待办任务
     *
     * @param flowTask 条件实体
     * @param page
     * @return
     */
    Page<FlowTask> toDoPage(FlowTask flowTask, Page<FlowTask> page);

    /**
     * 根据instanceIds删除
     *
     * @param instanceIds
     * @return
     */
    boolean deleteByInsIds(List<Long> instanceIds);
}
